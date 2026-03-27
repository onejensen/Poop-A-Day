package com.sozolabs.poop_a_day.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun TileBackground(isDarkMode: Boolean = isSystemInDarkTheme()) {
    val tileSize = 160f
    val groutWidth = 5f

    val tileColor1 = if (isDarkMode) Color(0xFF2E3338) else Color(0xFFEBF0F5)
    val tileColor2 = if (isDarkMode) Color(0xFF24292E) else Color(0xFFDCE3EB)
    val groutColor = if (isDarkMode) Color(0xFF141719) else Color(0xFFC7CCD1)

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = groutColor, size = size)

        val cols = (size.width / tileSize).toInt() + 1
        val rows = (size.height / tileSize).toInt() + 1

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val isAlternate = (row + col) % 2 == 0
                val color = if (isAlternate) tileColor1 else tileColor2

                val x = col * tileSize + groutWidth / 2
                val y = row * tileSize + groutWidth / 2

                drawRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(tileSize - groutWidth, tileSize - groutWidth)
                )
            }
        }
    }
}
