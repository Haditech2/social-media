package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Procedurally generates a QR code visual pattern based on the ticket hash code
 */
@Composable
fun QrCodeView(
  code: String,
  modifier: Modifier = Modifier,
  sizeDp: Int = 180
) {
  val matrix = remember(code) {
    val gridSize = 21
    val grid = Array(gridSize) { BooleanArray(gridSize) }
    val hash = abs(code.hashCode())

    // 1. Draw standard finder patterns at top-left, top-right, bottom-left
    fun drawFinder(startX: Int, startY: Int) {
      for (r in 0 until 7) {
        for (c in 0 until 7) {
          val isOuterBorder = r == 0 || r == 6 || c == 0 || c == 6
          val isInnerCenter = r in 2..4 && c in 2..4
          grid[startY + r][startX + c] = isOuterBorder || isInnerCenter
        }
      }
    }
    drawFinder(0, 0)
    drawFinder(gridSize - 7, 0)
    drawFinder(0, gridSize - 7)

    // 2. Fill pseudorandom data modules from ticket hash
    var seed = hash
    for (r in 0 until gridSize) {
      for (c in 0 until gridSize) {
        // Skip finder areas
        val inTopLeft = r < 8 && c < 8
        val inTopRight = r < 8 && c >= gridSize - 8
        val inBottomLeft = r >= gridSize - 8 && c < 8
        if (inTopLeft || inTopRight || inBottomLeft) continue

        seed = (seed * 1103515245 + 12345) and 0x7fffffff
        grid[r][c] = (seed % 3 == 0) || ((r + c) % 2 == 0 && (seed % 2 == 0))
      }
    }
    grid
  }

  Box(
    modifier = modifier
      .size(sizeDp.dp)
      .background(Color.White, RoundedCornerShape(12.dp))
      .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
      .padding(12.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size((sizeDp - 24).dp)) {
      val gridSize = 21
      val moduleSize = size.width / gridSize

      for (r in 0 until gridSize) {
        for (c in 0 until gridSize) {
          if (matrix[r][c]) {
            drawRect(
              color = Color(0xFF0F172A),
              topLeft = Offset(c * moduleSize, r * moduleSize),
              size = Size(moduleSize, moduleSize)
            )
          }
        }
      }
    }
  }
}
