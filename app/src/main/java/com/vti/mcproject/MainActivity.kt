package com.vti.mcproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vti.mcproject.ui.MCProjectApp
import com.vti.mcproject.ui.theme.MCProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MCProjectTheme {
                MCProjectApp()
            }
        }
    }
}