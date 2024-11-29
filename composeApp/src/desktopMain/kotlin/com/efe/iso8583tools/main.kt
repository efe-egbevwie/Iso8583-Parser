package com.efe.iso8583tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.efe.iso8583tools.home.HomeScreen
import com.efe.iso8583tools.theme.AppTheme
import java.awt.Dimension

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Iso8583 Parser",
    ) {
        window.minimumSize = Dimension(800, 600)
        Column {
            var isDarkTheme by remember {
                mutableStateOf(false)
            }
            AppTheme(isDarkTheme) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = { isDarkTheme = !isDarkTheme })
//            DevelopmentEntryPoint {
//                HomeScreen(modifier = Modifier.fillMaxSize())
//            }

            }
        }
    }

}