package bme.aut.panka.mondrianblocks.data.block

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MondrianColor {
    YELLOW, RED, WHITE, BLUE, BLACK
}

enum class MondrianOrientation {
    HORIZONTAL, VERTICAL
}

@Entity(tableName = "blocks")
data class Block(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val width: Int,
    val height: Int,
    val color: MondrianColor
)

class PuzzleBlock(
    val block: Block,
    val x: Int,
    val y: Int,
    val orientation: MondrianOrientation
)