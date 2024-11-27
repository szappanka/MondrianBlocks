package bme.aut.panka.mondrianblocks

import bme.aut.panka.mondrianblocks.data.block.Block
import bme.aut.panka.mondrianblocks.data.block.MondrianColor
import bme.aut.panka.mondrianblocks.data.block.MondrianOrientation
import bme.aut.panka.mondrianblocks.data.block.PuzzleBlock
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType
import bme.aut.panka.mondrianblocks.data.user.User

object GameData {
    val STARTER_PUZZLE = Puzzle(
        id = 0,
        difficulty = PuzzleType.NONE,
        blackBlocks = listOf(
            PuzzleBlock(
                block = Block(
                    width = 2,
                    height = 1,
                    color = MondrianColor.BLACK
                ),
                x = 5,
                y = 4,
                orientation = MondrianOrientation.HORIZONTAL
            ),
            PuzzleBlock(
                block = Block(
                    width = 3,
                    height = 1,
                    color = MondrianColor.BLACK
                ),
                x = 2,
                y = 7,
                orientation = MondrianOrientation.HORIZONTAL
            ),
        ),
        blocks = listOf(
            PuzzleBlock(
                block = Block(
                    width = 3,
                    height = 2,
                    color = MondrianColor.BLUE
                ),
                x = 1,
                y = 1,
                orientation = MondrianOrientation.HORIZONTAL
            ),
            PuzzleBlock(
                block = Block(
                    width = 4,
                    height = 2,
                    color = MondrianColor.BLUE
                ),
                x = 5,
                y = 7,
                orientation = MondrianOrientation.HORIZONTAL
            ),
            PuzzleBlock(
                block = Block(
                    width = 3,
                    height = 3,
                    color = MondrianColor.YELLOW
                ),
                x = 1,
                y = 3,
                orientation = MondrianOrientation.HORIZONTAL
            ),
            PuzzleBlock(
                block = Block(
                    width = 2,
                    height = 2,
                    color = MondrianColor.YELLOW
                ),
                x = 7,
                y = 4,
                orientation = MondrianOrientation.HORIZONTAL
            ),
            PuzzleBlock(
                block = Block(
                    width = 5,
                    height = 1,
                    color = MondrianColor.RED
                ),
                x = 4,
                y = 1,
                orientation = MondrianOrientation.VERTICAL
            ),
            PuzzleBlock(
                block = Block(
                    width = 4,
                    height = 1,
                    color = MondrianColor.RED
                ),
                x = 1,
                y = 8,
                orientation = MondrianOrientation.HORIZONTAL
            ),
            PuzzleBlock(
                block = Block(
                    width = 4,
                    height = 3,
                    color = MondrianColor.WHITE
                ),
                x = 5,
                y = 1,
                orientation = MondrianOrientation.HORIZONTAL
            ),
        )
    )

    var selectedUser: User? = null
    var selectedPuzzle: Puzzle? = null

    val initializedColors: MutableMap<String, List<IntArray>> = mutableMapOf()

    const val INWARD_OFFSET_PERCENTAGE = 0.2

}