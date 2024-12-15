package bme.aut.panka.mondrianblocks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.data.block.Block
import bme.aut.panka.mondrianblocks.data.block.MondrianColor
import bme.aut.panka.mondrianblocks.data.block.MondrianOrientation
import bme.aut.panka.mondrianblocks.data.block.PuzzleBlock
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType
import bme.aut.panka.mondrianblocks.ui.theme.MondrianBlack
import bme.aut.panka.mondrianblocks.ui.theme.MondrianGray
import bme.aut.panka.mondrianblocks.ui.theme.blockColors
import bme.aut.panka.mondrianblocks.ui.theme.difficultyColors

@Composable
fun MondrianPreview(
    puzzle: Puzzle,
    squareSize: Dp = 10.dp,
    padding: Dp = 2.dp,
    coloredBorderPadding: Dp = 4.dp
) {
    val boxSize = squareSize * 8 + padding * 9
    val gridSize = 8

    Column(
        modifier = Modifier
            .size(boxSize + coloredBorderPadding * 2)
            .fillMaxSize()
            .clip(RoundedCornerShape(2.dp))
            .background(
                difficultyColors[puzzle.difficulty.name] ?: Color.White
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Column(
                Modifier
                    .size(boxSize)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MondrianGray)
                    .padding(padding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(gridSize) { row ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(gridSize) { column ->
                            Box(
                                modifier = Modifier
                                    .size(squareSize)
                                    .width(squareSize)
                                    .height(squareSize)
                                    .clip(RoundedCornerShape(2.dp))
                                    .border(1.dp, Color.Gray)
                            )
                        }
                    }
                }
            }

            repeat(puzzle.blackBlocks.size) {
                val block = puzzle.blackBlocks[it]

                val offsetX =
                    (block.x.times(padding.value) + (block.x - 1).times(squareSize.value)).dp
                val offsetY =
                    (block.y.times(padding.value) + (block.y - 1).times(squareSize.value)).dp
                val width = squareSize * block.block.width + padding * (block.block.width - 1)
                val height = squareSize * block.block.height + padding * (block.block.height - 1)

                Box(
                    modifier = Modifier
                        .offset(
                            x = offsetX,
                            y = offsetY
                        )
                        .clip(RoundedCornerShape(1.dp))
                        .width(if (block.orientation == MondrianOrientation.HORIZONTAL ) width else height)
                        .height(if (block.orientation == MondrianOrientation.HORIZONTAL ) height else width)
                        .background(MondrianBlack)
                )
            }

            repeat(puzzle.blocks.size) {
                val block = puzzle.blocks[it]
                val color = block.block.color

                val offsetX =
                    (block.x.times(padding.value) + (block.x - 1).times(squareSize.value)).dp
                val offsetY =
                    (block.y.times(padding.value) + (block.y - 1).times(squareSize.value)).dp
                val width = squareSize * block.block.width + padding * (block.block.width - 1)
                val height = squareSize * block.block.height + padding * (block.block.height - 1)

                Box(
                    modifier = Modifier
                        .offset(
                            x = offsetX,
                            y = offsetY
                        )
                        .clip(RoundedCornerShape(1.dp))
                        .width(if (block.orientation == MondrianOrientation.HORIZONTAL ) width else height)
                        .height(if (block.orientation == MondrianOrientation.HORIZONTAL ) height else width)
                        .background(blockColors[color.name] ?: Color.White)
                )
            }
        }
    }

}

@Preview
@Composable
fun MondrianPreviewPreview() {
    MondrianPreview(
        puzzle = Puzzle(
            id = 0,
            difficulty = PuzzleType.EASY,
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
                PuzzleBlock(
                    block = Block(
                        width = 1,
                        height = 1,
                        color = MondrianColor.BLACK
                    ),
                    x = 2,
                    y = 6,
                    orientation = MondrianOrientation.HORIZONTAL
                ),
            ),
            blocks = mutableListOf(
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
        ),
        squareSize = 10.dp,
    )
}

