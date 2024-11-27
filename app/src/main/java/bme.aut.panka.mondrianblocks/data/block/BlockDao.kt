package bme.aut.panka.mondrianblocks.data.block

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBlock(block: Block): Long

    @Query("SELECT * FROM blocks")
    fun getAllBlocks(): Flow<List<Block>>


}