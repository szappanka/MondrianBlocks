package bme.aut.panka.mondrianblocks.data

import androidx.room.TypeConverter
import bme.aut.panka.mondrianblocks.data.block.PuzzleBlock
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromPuzzleBlockList(puzzleBlockList: List<PuzzleBlock>): String {
        return Gson().toJson(puzzleBlockList)
    }

    @TypeConverter
    fun toPuzzleBlockList(puzzleBlockListString: String): List<PuzzleBlock> {
        val objectType = object : TypeToken<List<PuzzleBlock>>() {}.type
        return Gson().fromJson(puzzleBlockListString, objectType)
    }
}