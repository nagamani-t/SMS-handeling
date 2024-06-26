package com.example.myapplication

import MainViewModel
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest

import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.launch


private val RequestPermissionTextArray = arrayOf(
    Manifest.permission.READ_SMS,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
// mainactivity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                MainScreen(modifier = Modifier, context = this, viewModel = viewModel)

            }
        }
    }
}


@Composable
fun PermissionScreen(navController: NavHostController, modifier: Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = viewModel<MainViewModel>()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val activity = LocalContext.current as ComponentActivity
    val context = LocalContext.current

    val smsPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(
                permission = Manifest.permission.READ_SMS,
                isGranted = isGranted
            )
        }
    )

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            RequestPermissionTextArray.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }

            if (viewModel.allPermissionsGranted()) {
                coroutineScope.launch {
                    viewModel.fetchSmsMessages(context)
                }

                navController.navigate("loadingScreen") {
                    popUpTo("permissions") { inclusive = true }
                }
            }
        }
    )
    // Check permissions when the composable is recomposed
    LaunchedEffect(key1 = context) {
        Log.d("PermissionScreen", "LaunchedEffect checking permissions")
        if (viewModel.checkPermissions(activity)) {
            if (viewModel.allPermissionsGranted()) {
                Log.d("PermissionScreen", "All permissions granted")
                coroutineScope.launch {
                    viewModel.fetchSmsMessages(context)
                }
                navController.navigate("loadingScreen") {
                    popUpTo("permissions") { inclusive = true }
                }
            }
        }
    }

    Log.i("launcher", viewModel.visiblePermissionDialogQueue.toString())
    Surface {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            10.dp
                        )
                    )
                    .background(Color.LightGray)
                    .padding(16.dp)
                    .height(130.dp)
            ) {
                Column {
                    Text(
                        text = "Please allow the following, to proceed\n" +
                                "further", fontSize = 18.sp, lineHeight = 20.sp, color = Color.Black
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Replace R.drawable.ic_sample with your actual XML drawable resource
                        Image(
                            painter = painterResource(R.drawable.email),
                            contentDescription = null, // provide a meaningful description if needed
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SMS", color = Color.Black
                        )
                    }
                    Spacer(modifier = modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Replace R.drawable.ic_sample with your actual XML drawable resource
                        Image(
                            painter = painterResource(R.drawable.location),
                            contentDescription = null, // provide a meaningful description if needed
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Location", color = Color.Black
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Don't worry, we take your privacy seriously and will never misuse\n" +
                        "your information. Allow us, and get ready to experience our app\n" +
                        "in a whole new way!",
                fontSize = 12.sp, lineHeight = 14.sp,
            )
            Spacer(modifier = Modifier.height(44.dp))
            Button(
                onClick = {
                    multiplePermissionResultLauncher.launch(
                        RequestPermissionTextArray
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Grant Permission", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(70.dp))

        }

    }


    dialogQueue.reversed().forEach { permission ->
        PermissionDailog(
            permissionTextProvider = when (permission) {
                Manifest.permission.READ_SMS -> {
                    SmsPermissionTextProvider()
                }

                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    LocationPermissionTextProvider()
                }

                else -> return@forEach
            },
            isPermanentlyDeclained = !ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission
            ),
            onDismiss = { viewModel.dismissDialog() },
            onOkClick = {
                viewModel.dismissDialog()
                smsPermissionResultLauncher.launch(permission)
            },
            onGoToAppSettingClick = { activity.openAppSettings() },
            modifier = modifier
        )
    }
}

fun Activity.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

@Composable
fun MainScreen(modifier: Modifier, context: Context, viewModel: MainViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, modifier,) }
        composable("permissions") { PermissionScreen(navController, modifier) }
        composable("loadingScreen") { LoadingScreen(navController,modifier) }
        composable("loginScreen") {
            LoginScreen(onLogin = { email, phone, name ->
                // Save credentials to SharedPreferences using SharedPreferencesHelper
                SharedPreferencesHelper.saveCredentials(context, email, phone, name)

                // Navigate to the next screen
                navController.navigate("permissions")
            })
        }
        composable("webscreen") { WebScreen(navController = navController, modifier = modifier) }
    }
}

@Composable
fun WebScreen(navController: NavHostController, modifier: Modifier) {
    val context = LocalContext.current
    val (email, phoneNumber, name) = remember { SharedPreferencesHelper.getSavedCredentials(context) }

Surface(modifier = modifier.fillMaxHeight().background(Color.Blue)) {
    if (email != null) {
        if (name != null) {
            if (phoneNumber != null) {
                WebViewComponent(url = "https://messagefetch.vercel.app/",modifier = Modifier
                    .height(400.dp),email,name,phoneNumber,navController = navController)
            }
        }
    }
}



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLogin: (String, String, String) -> Unit) {
    val context = LocalContext.current
    val emailState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val nameState = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val gifUrl = "https://i.pinimg.com/originals/f3/08/12/f30812a99a20d8beac68937d9d939d0b.gif"

        // Remember the painter with the GIF URL
        GifImage(gifUrl =gifUrl )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Blue, // Customize text color
                containerColor = Color.Transparent, // Customize background color
                cursorColor = Color.Gray, // Customize cursor color
                focusedBorderColor = Color.Gray, // Customize focused border color
                unfocusedBorderColor = Color.LightGray, // Customize unfocused border color
                focusedLabelColor = Color.Blue, // Customize focused label color
                unfocusedLabelColor = Color.Gray, // Customize unfocused label color
                unfocusedPlaceholderColor = Color.Gray // Customize placeholder color
            )
            )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phoneState.value,
            onValueChange = { phoneState.value = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Blue, // Customize text color
                containerColor = Color.Transparent, // Customize background color
                cursorColor = Color.Gray, // Customize cursor color
                focusedBorderColor = Color.Gray, // Customize focused border color
                unfocusedBorderColor = Color.LightGray, // Customize unfocused border color
                focusedLabelColor = Color.Blue, // Customize focused label color
                unfocusedLabelColor = Color.Gray, // Customize unfocused label color
                unfocusedPlaceholderColor = Color.Gray // Customize placeholder color
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Name") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Blue, // Customize text color
                containerColor = Color.Transparent, // Customize background color
                cursorColor = Color.Gray, // Customize cursor color
                focusedBorderColor = Color.Gray, // Customize focused border color
                unfocusedBorderColor = Color.LightGray, // Customize unfocused border color
                focusedLabelColor = Color.Blue, // Customize focused label color
                unfocusedLabelColor = Color.Gray, // Customize unfocused label color
                unfocusedPlaceholderColor = Color.Gray // Customize placeholder color
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                val email = emailState.value
                val phone = phoneState.value
                val name = nameState.value
                Log.i("creds","email:$email,phone:$phone,name:$name")
                // Save credentials to SharedPreferences
                SharedPreferencesHelper.saveCredentials(context, email, phone, name)
                // Callback to notify login action
                onLogin(email, phone, name)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            modifier = Modifier
                .padding(6.dp)
                .height(50.dp)
                .width(280.dp)
        ) {
            Text("Login")

        }
    }
}


object SharedPreferencesHelper {
    private const val PREFS_NAME = "MyAppPrefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PHONE = "phone"
    private const val KEY_NAME = "name"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCredentials(context: Context, email: String, phone: String, name: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PHONE, phone)
        editor.putString(KEY_NAME, name)
        editor.apply()
    }

    fun getSavedCredentials(context: Context): Triple<String?, String?, String?> {
        val prefs = getSharedPreferences(context)
        val email = prefs.getString(KEY_EMAIL, null)
        val phone = prefs.getString(KEY_PHONE, null)
        val name = prefs.getString(KEY_NAME, null)
        return Triple(email, phone, name)
    }
}


