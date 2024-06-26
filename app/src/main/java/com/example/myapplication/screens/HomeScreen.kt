package com.example.myapplication


import MainViewModel
import android.content.Context
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import androidx.core.content.edit
@Composable
fun HomeScreen(navController: NavHostController, modifier: Modifier) {
    val context = LocalContext.current
    val (email, _, _) = remember { SharedPreferencesHelper.getSavedCredentials(context) }
    val isFirstLogin = email.isNullOrEmpty()
    Log.i("email",email.toString())

    Log.i("loginfo", isFirstLogin.toString())

//    if (!isFirstLogin) {
//        LaunchedEffect(Unit) {
//            navController.navigate("loadingScreen")
//        }
//    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp), verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = "Your Finances\n" +
                    "at One place",
            fontSize = 46.sp,
            lineHeight = 54.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = modifier.height(10.dp))
        Text(
            text = "Get a bird's-eye view of your finances.\n" +
                    "Develop healthy financial habits.",
            fontSize = 16.sp, lineHeight = 18.sp,

            )
        Spacer(modifier = modifier.height(80.dp))
        Button(
            onClick = {
                if (isFirstLogin) {
                    navController.navigate("loginScreen")
                } else {
                    navController.navigate("loadingScreen")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Get Started", fontSize = 16.sp)
        }
        Spacer(modifier = modifier.height(70.dp))
    }
}