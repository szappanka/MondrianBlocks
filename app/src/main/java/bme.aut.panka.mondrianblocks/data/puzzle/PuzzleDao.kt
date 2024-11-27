package bme.aut.panka.mondrianblocks.data.puzzle

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPuzzle(block: Puzzle): Long

    @Query("SELECT * FROM puzzles")
    fun getAllPuzzles(): Flow<List<Puzzle>>

    @Query("SELECT * FROM puzzles ORDER BY RANDOM() LIMIT 1")
    fun getRandomPuzzle(): Flow<Puzzle>
}