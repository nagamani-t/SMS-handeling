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