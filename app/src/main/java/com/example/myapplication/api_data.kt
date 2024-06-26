import androidx.collection.ObjectList
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Objects
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject


data class SmsBatch(
    val messages: List<String>
)


