package com.parrotdad.soundboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.parrotdad.soundboard.data.soundboardItems
import com.parrotdad.soundboard.data.violinItem
import com.parrotdad.soundboard.ui.SoundboardScreen
import com.parrotdad.soundboard.ui.theme.ParrotDadTheme

/**
 * Single activity that hosts the entire Compose UI.
 * Audio lifecycle is managed inside [SoundboardScreen] via DisposableEffect,
 * so this activity stays minimal.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParrotDadTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SoundboardScreen(items = soundboardItems, wideItem = violinItem)
                }
            }
        }
    }
}
