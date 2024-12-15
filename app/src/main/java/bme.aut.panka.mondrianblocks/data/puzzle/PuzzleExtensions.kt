package bme.aut.panka.mondrianblocks.data.puzzle

import android.util.Log
import bme.aut.panka.mondrianblocks.data.block.MondrianColor
import bme.aut.panka.mondrianblocks.data.block.MondrianOrientation

fun Puzzle.toFormattedString(): String {
    val rows = 8
    val cols = 8
    val grid = Array(rows) { Array(cols) { "-" } }

    blackBlocks.forEach { puzzleBlock ->
        val (width, height) = when (puzzleBlock.orientation) {
            MondrianOrientation.HORIZONTAL -> puzzleBlock.block.width to puzzleBlock.block.height
            MondrianOrientation.VERTICAL -> puzzleBlock.block.height to puzzleBlock.block.width
        }
        for (dy in 0 until height) {
            for (dx in 0 until width) {
                val newX = puzzleBlock.x - 1 + dx
                val newY = puzzleBlock.y - 1 + dy
                if (newY in 0 until rows && newX in 0 until cols) {
                    grid[newY][newX] = "x"
                } else {
                    Log.w("Puzzle", "BlackBlock out of bounds: x=$newX, y=$newY")
                }
            }
        }
    }

    blocks.forEach { puzzleBlock ->
        val colorInitial = when (puzzleBlock.block.color) {
            MondrianColor.YELLOW -> "y"
            MondrianColor.RED -> "r"
            MondrianColor.WHITE -> "w"
            MondrianColor.BLUE -> "b"
            MondrianColor.BLACK -> "k"
        }
        val (width, height) = when (puzzleBlock.orientation) {
            MondrianOrientation.HORIZONTAL -> puzzleBlock.block.width to puzzleBlock.block.height
            MondrianOrientation.VERTICAL -> puzzleBlock.block.height to puzzleBlock.block.width
        }
        for (dy in 0 until height) {
            for (dx in 0 until width) {
                val newX = puzzleBlock.x - 1 + dx
                val newY = puzzleBlock.y - 1 + dy
                if (newY in 0 until rows && newX in 0 until cols) {
                    grid[newY][newX] = "$colorInitial${puzzleBlock.block.height}x${puzzleBlock.block.width}"
                } else {
                    Log.w("Puzzle", "Block out of bounds: x=$newX, y=$newY")
                }
            }
        }
    }

    return grid.joinToString(
        separator = ",",
        prefix = "[",
        postfix = "]"
    ) { row -> row.joinToString(separator = ",", prefix = "[", postfix = "]") }
}
