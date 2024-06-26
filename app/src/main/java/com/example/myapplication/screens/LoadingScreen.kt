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
fun LoadingScreen(navController: NavHostController, modifier: Modifier) {
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            viewModel.fetchSmsMessages(context)
        }
    }

    val loading by remember { viewModel::loading }
    val progress by remember { viewModel::progress }
    val totalBatches by remember { viewModel::totalBatches }

    Log.i("LoadingScreen", "Loading: $loading, Progress: $progress, Total Batches: $totalBatches")
    if (loading) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = modifier
                    .padding(20.dp)
                    .size(width = 400.dp, height = 400.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    val gifUrl =
                        "https://i.pinimg.com/originals/c4/bf/d4/c4bfd4527e247b96d5da52733f2fd90c.gif"

                    // Remember the painter with the GIF URL
                    GifImage(gifUrl = gifUrl)


                    Spacer(modifier = Modifier.height(10.dp))

                }

            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hold on,\n" +
                            "We are scanning!",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "We are just scanning your transactional\n" +
                            "SMS, we promise we are not reading your\n" +
                            "Personal SMS.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(150.dp))
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Adjust the URL below to your actual GIF URL


                val progressPercentage =
                    if (totalBatches != 0) progress / totalBatches.toFloat() else 0f
                LinearProgressIndicator(
                    progress = progressPercentage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 100.dp, height = 7.dp)

                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Fetching and Processing messages... (${(progressPercentage * 100).toInt()}%)",

                        )
                }
            }
        }
    } else if (progress == totalBatches) {
        // Once loading is complete and all batches are processed, navigate to the next screen
        LaunchedEffect(Unit) {
            navController.navigate("webscreen")

        }
    }
}