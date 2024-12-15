package bme.aut.panka.mondrianblocks.features.processor.partials

import android.content.Context
import android.util.Log
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.data.block.PuzzleBlock
import bme.aut.panka.mondrianblocks.data.block.Block
import bme.aut.panka.mondrianblocks.data.block.MondrianColor
import bme.aut.panka.mondrianblocks.data.block.MondrianOrientation

class BlockRecognitionHelper(val context: Context) {

    fun recognizeWhiteBlock(gridColors: Array<Array<String?>>): PuzzleBlock? {
        val whiteBlock = GameData.white_block
        for (x in 0 until gridColors.size - whiteBlock.height + 1) {
            for (y in 0 until gridColors[x].size - whiteBlock.width + 1) {
                val fittedBlock = isBlockFitToGrid(whiteBlock, gridColors, x, y)
                if (fittedBlock != null) {
                    markGridAsOccupied(gridColors, x, y, whiteBlock, fittedBlock.orientation)
                    return fittedBlock
                }
            }
        }
        return null
    }

    fun recognizeYellowBlocks(gridColors: Array<Array<String?>>): List<PuzzleBlock> {
        val recognizedBlocks = mutableListOf<PuzzleBlock>()
        val yellowBlocks = GameData.yellow_blocks
        for (block in yellowBlocks) {
            for (x in 0 until gridColors.size - block.height + 1) {
                for (y in 0 until gridColors[x].size - block.width + 1) {
                    val fittedBlock = isBlockFitToGrid(block, gridColors, x, y)
                    if (fittedBlock != null) {
                        markGridAsOccupied(gridColors, x, y, block, fittedBlock.orientation)
                        recognizedBlocks.add(fittedBlock)
                    }
                }
            }
        }
        return recognizedBlocks
    }

    fun recognizeRedBlocks(gridColors: Array<Array<String?>>): List<PuzzleBlock>? {
        val redBlocks = GameData.red_blocks
        val occupiedGrid = Array(gridColors.size) { Array(gridColors[0].size) { false } }
        val possibleArrangements =
            mutableListOf<MutableList<Triple<Block, Pair<Int, Int>, MondrianOrientation>>>()

        fun isValidPlacement(
            x: Int,
            y: Int,
            block: Block,
            orientation: MondrianOrientation
        ): Boolean {
            val width =
                if (orientation == MondrianOrientation.HORIZONTAL) block.width else block.height
            val height =
                if (orientation == MondrianOrientation.HORIZONTAL) block.height else block.width

            if (x + width > gridColors.size || y + height > gridColors[0].size) return false

            for (i in 0 until width) {
                for (j in 0 until height) {
                    if (gridColors[x + i][y + j] != MondrianColor.RED.toString() || occupiedGrid[x + i][y + j]) {
                        return false
                    }
                }
            }
            return true
        }

        fun placeBlock(x: Int, y: Int, block: Block, orientation: MondrianOrientation) {
            val width =
                if (orientation == MondrianOrientation.HORIZONTAL) block.width else block.height
            val height =
                if (orientation == MondrianOrientation.HORIZONTAL) block.height else block.width
            for (i in 0 until width) {
                for (j in 0 until height) {
                    occupiedGrid[x + i][y + j] = true
                }
            }
        }

        fun removeBlock(x: Int, y: Int, block: Block, orientation: MondrianOrientation) {
            val width =
                if (orientation == MondrianOrientation.HORIZONTAL) block.width else block.height
            val height =
                if (orientation == MondrianOrientation.HORIZONTAL) block.height else block.width
            for (i in 0 until width) {
                for (j in 0 until height) {
                    occupiedGrid[x + i][y + j] = false
                }
            }
        }

        fun findArrangements(
            blocks: List<Block>,
            arrangement: MutableList<Triple<Block, Pair<Int, Int>, MondrianOrientation>>,
            depth: Int = 0
        ) {
            if (depth == blocks.size) {
                if (arrangement.isNotEmpty()) {
                    possibleArrangements.add(ArrayList(arrangement))
                }
                return
            }

            val block = blocks[depth]
            for (x in gridColors.indices) {
                for (y in gridColors[0].indices) {
                    for (orientation in MondrianOrientation.entries) {
                            if (isValidPlacement(x, y, block, orientation)) {
                            placeBlock(x, y, block, orientation)
                            arrangement.add(Triple(block, x to y, orientation))

                            findArrangements(blocks, arrangement, depth + 1)

                            arrangement.removeAt(arrangement.lastIndex)
                            removeBlock(x, y, block, orientation)
                        }
                    }
                }
            }

            findArrangements(blocks, arrangement, depth + 1)
        }

        findArrangements(redBlocks, mutableListOf())

        if (possibleArrangements.isEmpty()) {
            return null
        }

        val maxArrangement = possibleArrangements.maxByOrNull { it.size }
        val maxArrangements = possibleArrangements.filter { it.size == maxArrangement?.size }

        return when {
            maxArrangement == null -> {
                null
            }

            maxArrangement.size == redBlocks.size -> {
                maxArrangement.map { (block, position, orientation) ->
                    val (x, y) = position
                    PuzzleBlock(block, x + 1, y + 1, orientation)
                }
            }

            maxArrangements.size == 1 -> {
                maxArrangement.map { (block, position, orientation) ->
                    val (x, y) = position
                    PuzzleBlock(block, x + 1, y + 1, orientation)
                }
            }

            else -> {
                val validArrangements = maxArrangements.filter { arrangement ->
                    occupiedGrid.forEach { row -> row.fill(false) }
                    arrangement.forEach { (block, position, orientation) ->
                        val (x, y) = position
                        placeBlock(x, y, block, orientation)
                    }

                    gridColors.indices.sumOf { x ->
                        gridColors[x].indices.count { y ->
                            gridColors[x][y] == MondrianColor.RED.toString() && !occupiedGrid[x][y]
                        }
                    } == 0
                }

                if (validArrangements.isEmpty()) {
                   return null
                }
                validArrangements[0].map { (block, position, orientation) ->
                    val (x, y) = position
                    PuzzleBlock(block, x + 1, y + 1, orientation)
                }
            }
        }
    }

    fun recognizeBlueBlocks(gridColors: Array<Array<String?>>): List<PuzzleBlock>? {
        val blueBlocks = GameData.blue_blocks
        val occupiedGrid = Array(gridColors.size) { Array(gridColors[0].size) { false } }
        val possibleArrangements =
            mutableListOf<MutableList<Triple<Block, Pair<Int, Int>, MondrianOrientation>>>()

        fun isValidPlacement(
            x: Int,
            y: Int,
            block: Block,
            orientation: MondrianOrientation
        ): Boolean {
            val width =
                if (orientation == MondrianOrientation.HORIZONTAL) block.width else block.height
            val height =
                if (orientation == MondrianOrientation.HORIZONTAL) block.height else block.width

            if (x + width > gridColors.size || y + height > gridColors[0].size) return false

            for (i in 0 until width) {
                for (j in 0 until height) {
                    if (gridColors[x + i][y + j] != MondrianColor.BLUE.toString() || occupiedGrid[x + i][y + j]) {
                        return false
                    }
                }
            }
            return true
        }

        fun placeBlock(x: Int, y: Int, block: Block, orientation: MondrianOrientation) {
            val width =
                if (orientation == MondrianOrientation.HORIZONTAL) block.width else block.height
            val height =
                if (orientation == MondrianOrientation.HORIZONTAL) block.height else block.width
            for (i in 0 until width) {
                for (j in 0 until height) {
                    occupiedGrid[x + i][y + j] = true
                }
            }
        }

        fun removeBlock(x: Int, y: Int, block: Block, orientation: MondrianOrientation) {
            val width =
                if (orientation == MondrianOrientation.HORIZONTAL) block.width else block.height
            val height =
                if (orientation == MondrianOrientation.HORIZONTAL) block.height else block.width
            for (i in 0 until width) {
                for (j in 0 until height) {
                    occupiedGrid[x + i][y + j] = false
                }
            }
        }

        fun findArrangements(
            blocks: List<Block>,
            arrangement: MutableList<Triple<Block, Pair<Int, Int>, MondrianOrientation>>,
            depth: Int = 0
        ) {
            if (depth == blocks.size) {
                if (arrangement.isNotEmpty()) {
                    possibleArrangements.add(ArrayList(arrangement))
                }
                return
            }

            val block = blocks[depth]
            for (x in gridColors.indices) {
                for (y in gridColors[0].indices) {
                    for (orientation in MondrianOrientation.entries) {
                        if (isValidPlacement(x, y, block, orientation)) {
                            placeBlock(x, y, block, orientation)
                            arrangement.add(Triple(block, x to y, orientation))

                            findArrangements(blocks, arrangement, depth + 1)

                            arrangement.removeAt(arrangement.lastIndex)
                            removeBlock(x, y, block, orientation)
                        }
                    }
                }
            }

            findArrangements(blocks, arrangement, depth + 1)
        }

        findArrangements(blueBlocks, mutableListOf())

        if (possibleArrangements.isEmpty()) {
            return null
        }

        val maxArrangement = possibleArrangements.maxByOrNull { it.size }
        val maxArrangements = possibleArrangements.filter { it.size == maxArrangement?.size }

        return when {
            maxArrangement == null -> {
                null
            }

            maxArrangement.size == blueBlocks.size -> {
                maxArrangement.map { (block, position, orientation) ->
                    val (x, y) = position
                    PuzzleBlock(block, x + 1, y + 1, orientation)
                }
            }

            maxArrangements.size == 1 -> {
                maxArrangement.map { (block, position, orientation) ->
                    val (x, y) = position
                    PuzzleBlock(block, x + 1, y + 1, orientation)
                }
            }

            else -> {
                val validArrangements = maxArrangements.filter { arrangement ->
                    occupiedGrid.forEach { row -> row.fill(false) }
                    arrangement.forEach { (block, position, orientation) ->
                        val (x, y) = position
                        placeBlock(x, y, block, orientation)
                    }

                    gridColors.indices.sumOf { x ->
                        gridColors[x].indices.count { y ->
                            gridColors[x][y] == MondrianColor.BLUE.toString() && !occupiedGrid[x][y]
                        }
                    } == 0
                }

                if (validArrangements.isEmpty()) {
                    return null
                }
                validArrangements[0].map { (block, position, orientation) ->
                    val (x, y) = position
                    PuzzleBlock(block, x + 1, y + 1, orientation)
                }
            }
        }
    }


    private fun isBlockFitToGrid(
        block: Block,
        gridColors: Array<Array<String?>>,
        x: Int,
        y: Int
    ): PuzzleBlock? {
        if (isFit(block.width, block.height, block.color, gridColors, x, y)) {
            return PuzzleBlock(block, x + 1, y + 1, MondrianOrientation.HORIZONTAL)
        }
        if (isFit(block.height, block.width, block.color, gridColors, x, y)) {
            return PuzzleBlock(block, x + 1, y + 1, MondrianOrientation.VERTICAL)
        }
        return null
    }

    private fun isFit(
        width: Int,
        height: Int,
        color: MondrianColor,
        gridColors: Array<Array<String?>>,
        x: Int,
        y: Int
    ): Boolean {
        if (x + width > gridColors.size || y + height > gridColors[0].size) {
            return false
        }
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (gridColors[x + i][y + j] != color.toString()) {
                    return false
                }
            }
        }
        return true
    }

    private fun markGridAsOccupied(
        gridColors: Array<Array<String?>>,
        x: Int,
        y: Int,
        block: Block,
        orientation: MondrianOrientation
    ) {
        val width = if (orientation == MondrianOrientation.HORIZONTAL) block.width else block.height
        val height =
            if (orientation == MondrianOrientation.HORIZONTAL) block.height else block.width
        for (i in 0 until width) {
            for (j in 0 until height) {
                gridColors[x + i][y + j] = "OCCUPIED"
            }
        }
    }
}
