package com.example.myapplication

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController

class MyWebViewClient : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        // Inject JavaScript interface
        view?.evaluateJavascript(
            """
            window.Android = {
                postMessage: function(data) {
                    window.jsInterface.postMessage(data);
                }
            };
            """, null
        )
    }
}

@Composable
fun WebViewComponent(
    url: String,
    modifier: Modifier,
    email: String,
    name: String,
    phoneNumber: String,
    navController: NavHostController

) {
    val headers = mapOf(
        "name" to name,
        "phoneNumber" to phoneNumber,
        "email" to email
    )
    val urlWithParams = "$url?name=$name&phoneNumber=$phoneNumber&email=$email"
    Log.i("urlWithParams", urlWithParams)
    Log.i("headers", headers.toString())
    var navigateBack = remember { mutableStateOf(false) }
    Log.i("navigate", navigateBack.value.toString())
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = MyWebViewClient()
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun postMessage(message: String) {
                        Log.i("WebViewInterface", "Message received from WebView: $message")
                        if (message == "navigateBack") {
                            navigateBack.value = true
                        }
                    }
                }, "jsInterface") // Name of the JavaScript interface object
                loadUrl(urlWithParams)
            }
        },
        update = { webView ->
            // Handle navigation when navigateBack is true
            if (navigateBack.value == true) {
                navController.navigate("home")
                navigateBack.value = false
            }

        },
        modifier = Modifier.fillMaxSize()
    )
}



