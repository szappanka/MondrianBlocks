package bme.aut.panka.mondrianblocks.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType
import bme.aut.panka.mondrianblocks.ui.theme.difficultyColors
import bme.aut.panka.mondrianblocks.ui.theme.difficultyDarkColors
import androidx.compose.ui.geometry.Size
import bme.aut.panka.mondrianblocks.ui.theme.easy
import bme.aut.panka.mondrianblocks.ui.theme.extreme
import bme.aut.panka.mondrianblocks.ui.theme.hard
import bme.aut.panka.mondrianblocks.ui.theme.medium

@Composable
fun MondrianFilter(
    selectedDifficulty: PuzzleType?,
    onDifficultySelected: (PuzzleType?) -> Unit
) {
    val difficulties = PuzzleType.entries.toTypedArray()

    Row(horizontalArrangement = Arrangement.SpaceAround) {
        difficulties.forEachIndexed { index, difficulty ->
            if (difficulty == PuzzleType.NONE) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onDifficultySelected(null) }
                ) {
                    FourColorCircle(
                        colors = listOf(easy, medium, hard, extreme),
                        size = 30.dp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(
                            width = if (selectedDifficulty == difficulty) 5.dp else 0.dp,
                            color = difficultyDarkColors[difficulty.name] ?: Color.Gray,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(difficultyColors[difficulty.name] ?: Color.Gray)
                        .clickable { onDifficultySelected(difficulty) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                    content = {}
                )
            }
            if (index < difficulties.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun FourColorCircle(colors: List<Color>, size: androidx.compose.ui.unit.Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val circleDiameter = size.toPx()
        val circleSize = Size(circleDiameter, circleDiameter)
        val startAngles = listOf(0f, 90f, 180f, 270f)
        val sweepAngle = 90f

        colors.forEachIndexed { index, color ->
            drawArc(
                color = color,
                startAngle = startAngles[index],
                sweepAngle = sweepAngle,
                useCenter = true,
                size = circleSize
            )
        }
    }
}

@Preview
@Composable
fun MondrianFilterPreview() {
    MondrianFilter(selectedDifficulty = PuzzleType.EASY, onDifficultySelected = {})
}
