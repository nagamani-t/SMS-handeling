package com.example.myapplication

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GifWebView(gifUrl: String) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webViewClient = WebViewClient()
            loadUrl(gifUrl)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    },
        modifier =Modifier.size(200.dp)
    )
}
@Composable
fun GifImage(gifUrl: String) {
    GifWebView(gifUrl = gifUrl)
}