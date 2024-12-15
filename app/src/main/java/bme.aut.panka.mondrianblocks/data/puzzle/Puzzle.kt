package bme.aut.panka.mondrianblocks.data.puzzle

import androidx.room.Entity
import androidx.room.PrimaryKey
import bme.aut.panka.mondrianblocks.data.block.PuzzleBlock

enum class PuzzleType {
    EASY, MEDIUM, HARD, EXTREME,
    NONE
}

@Entity(tableName = "puzzles")
data class Puzzle(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val difficulty: PuzzleType,
    val blackBlocks: List<PuzzleBlock>,
    val blocks: MutableList<PuzzleBlock>,
)

