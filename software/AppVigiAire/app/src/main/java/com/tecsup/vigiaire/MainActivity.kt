package com.tecsup.vigiaire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.vigiaire.navigation.AppNavigation
import com.tecsup.vigiaire.ui.theme.VigiAireTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VigiAireTheme {
                AppNavigation()
            }
        }
    }
}
