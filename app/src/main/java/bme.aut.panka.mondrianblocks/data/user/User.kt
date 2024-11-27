package bme.aut.panka.mondrianblocks.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    var birth: String,
    val dateCreated: Long = Instant.now().epochSecond
)