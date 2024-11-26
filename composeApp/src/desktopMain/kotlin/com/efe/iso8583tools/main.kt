package com.efe.iso8583tools

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.efe.iso8583tools.home.HomeScreen
import com.efe.iso8583tools.theme.AppTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Iso Parser",
    ) {
        AppTheme {
            HomeScreen(modifier = Modifier.fillMaxSize())
        }
    }
}