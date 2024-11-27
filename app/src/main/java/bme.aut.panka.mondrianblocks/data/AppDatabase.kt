package bme.aut.panka.mondrianblocks.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import bme.aut.panka.mondrianblocks.data.block.Block
import bme.aut.panka.mondrianblocks.data.block.BlockDao
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleDao
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.user.User
import bme.aut.panka.mondrianblocks.data.user.UserDao

@Database(
    entities = [User::class, Block::class, Puzzle::class
    ], version = 4, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun blockDao(): BlockDao
    abstract fun puzzleDao(): PuzzleDao
}