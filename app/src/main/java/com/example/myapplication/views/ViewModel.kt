import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Telephony
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import java.util.LinkedList
import java.util.Queue

private val RequestPermissionTextArray = arrayOf(
    Manifest.permission.READ_SMS,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
const val BATCH_SIZE = 100

private val BASE_URL = "http://dev-data-ai.neokred.tech:9080"
private val END_POINT = "category-entity-extraction"

class MainViewModel : ViewModel() {
    var visiblePermissionDialogQueue = mutableStateListOf<String>()
    var apiStatus by mutableStateOf("Idle")
    var loading by mutableStateOf(true)
    var progress by mutableStateOf(0)
    var smsCount by mutableStateOf(0)
    var totalBatches by mutableStateOf(0)
    private val batchCounter = AtomicInteger(0)
    private val batchStatusMap = mutableMapOf<Int, String>()
    private val batchQueue: Queue<List<String>> = LinkedList()
    private var isProcessing = false

    data class SmsMessage(val body: String)

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirstOrNull()
    }

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(0, permission)
        }
    }

    fun allPermissionsGranted(): Boolean {
        return RequestPermissionTextArray.all { permission ->
            visiblePermissionDialogQueue.contains(permission).not()
        }
    }

    fun checkPermissions(activity: Activity): Boolean {
        return RequestPermissionTextArray.all { permission ->
            ActivityCompat.checkSelfPermission(
                activity,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    suspend fun fetchSmsMessages(context: Context): String {
        loading = true
        progress = 0
        val smsUri = Telephony.Sms.CONTENT_URI
        val cursor = withContext(Dispatchers.IO) {
            context.contentResolver.query(smsUri, null, null, null, null)
        }

        cursor?.use {
            smsCount = it.count
            Log.i("smsCount", smsCount.toString())

            val bodyIndex = it.getColumnIndex(android.provider.Telephony.Sms.Inbox.BODY)
            val dateIndex = it.getColumnIndex(android.provider.Telephony.Sms.Inbox.DATE)
            val smsBatch = mutableListOf<String>()
            var smsBatchCount = 0
            while (it.moveToNext()) {
                if (bodyIndex != -1 && dateIndex != -1) {
                    val smsBody = it.getString(bodyIndex)
                    val smsDateLong = it.getLong(dateIndex)
                    val smsDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalDateTime.ofEpochSecond(smsDateLong / 1000, 0, ZoneOffset.UTC)
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
                    } else {
                        val sdf = SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss", Locale.getDefault())
                        sdf.format(Date(smsDateLong))
                    }
                    val smsWithDateTime = "$smsBody | $smsDate"
                    Log.d("smsWithDateTime", "smsWithDateTime $smsWithDateTime")
                    smsBatch.add(smsWithDateTime)
                    Log.i("SMS____", smsBatch.toString())
                    smsBatchCount++
                    if (smsBatchCount == 100) {
                        Log.i("smsBatch", smsBatch.toString())

                        // Queue this batch of SMS to send to API
                        batchQueue.add(smsBatch.toList())
                        totalBatches++ // Increment total batches
                        // Clear the batch for the next set
                        smsBatch.clear()
                        smsBatchCount = 0
                    }
                }
            }
            // If there are remaining SMS messages in the last batch, queue them to API
            if (smsBatch.isNotEmpty()) {
                batchQueue.add(smsBatch.toList())
                totalBatches++ // Increment total batches
                smsBatch.clear()
            }

            // Start processing the batches
            processNextBatch()
        }
        return "sms fetched"
    }

    private fun processNextBatch() {
        if (isProcessing) return
        val nextBatch = batchQueue.poll()
        if (nextBatch != null) {
            isProcessing = true
            sendSmsBatchesToApi(nextBatch)
        } else {

            Log.i("Batch Processing", "All batches processed")
            Log.i("Batch Status", batchStatusMap.toString())
        }
    }

    private fun retryBatch(batchId: Int, batches: List<String>) {
        batchStatusMap[batchId] = "Retrying"

        // Retry logic
        val retryCount = 3 // Number of retries before giving up
        var currentRetry = 0

        fun executeRetry() {
            if (currentRetry < retryCount) {
                Log.d("Batch $batchId", "Retrying attempt ${currentRetry + 1}")
                sendSmsBatchesToApi(batches)
                currentRetry++
            } else {
                Log.e("Batch $batchId", "Failed after $retryCount retries")
                batchStatusMap[batchId] = "Failed"
                progress-- // Adjust progress if needed
                isProcessing = false
                checkCompletion() // Check if all batches are completed
            }
        }

        executeRetry()
    }
    private fun sendSmsBatchesToApi(batches: List<String>) {
        val batchId = batchCounter.incrementAndGet()
        batchStatusMap[batchId] = "Sending"
        Log.i("Batch $batchId", "Sending batch $batchId")

        val SmsBatchArray = JSONArray(batches)
        val jsonRequest = JSONObject().apply {
            put("messages", SmsBatchArray)
        }
        Log.i("jsonRequest", jsonRequest.toString())

        // Make API call to send SMS batch
        val client = OkHttpClient()
        val apiUrl = "$BASE_URL/$END_POINT"
        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("Batch $batchId - API Failure", e.toString())
                retryBatch(batchId, batches)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("Batch $batchId - API Response", "Successfully fetched")
                val responseBody = response.body?.string()
                Log.i("Batch $batchId - Response", responseBody ?: "No response body")
                batchStatusMap[batchId] = "Completed"
                progress++
                isProcessing = false
                checkCompletion()
                processNextBatch() // Process the next batch
            }
        })
    }

    private fun checkCompletion() {
        if (progress >= totalBatches) {
            Log.i("progress value", progress.toString())
            Log.i("totalBatches value", totalBatches.toString())
            loading = false
            Log.i("Batch Processing", "All batches processed")
            Log.i("Batch Status", batchStatusMap.toString())
        }
    }
}
