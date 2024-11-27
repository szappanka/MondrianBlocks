package bme.aut.panka.mondrianblocks.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import bme.aut.panka.mondrianblocks.data.block.Block
import bme.aut.panka.mondrianblocks.data.block.BlockDao
import bme.aut.panka.mondrianblocks.data.block.MondrianColor
import bme.aut.panka.mondrianblocks.data.block.MondrianOrientation
import bme.aut.panka.mondrianblocks.data.block.PuzzleBlock
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleDao
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType
import bme.aut.panka.mondrianblocks.data.user.User
import bme.aut.panka.mondrianblocks.data.user.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(4)

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "users"
        ).fallbackToDestructiveMigration().addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                databaseWriteExecutor.execute {
                    runBlocking {
                        val blockDao = provideUserDatabase(context).blockDao()
                        val puzzleDao = provideUserDatabase(context).puzzleDao()
                        val userDao = provideUserDatabase(context).userDao()

                        userDao.addUser(User(name = "Fá Zoltán", birth = "1999.01.01"))
                        userDao.addUser(User(name = "Teszt Elek", birth = "1998.02.02"))
                        userDao.addUser(User(name = "Futó Rózsa", birth = "1997.03.03"))
                        userDao.addUser(User(name = "Gipsz Elek", birth = "1996.04.04"))
                        userDao.addUser(User(name = "Bármi Áron", birth = "1995.05.05"))
                        userDao.addUser(User(name = "Cserepesné Kis Virág", birth = "1994.06.06"))

                        // black blocks
                        val blackBlock1 = Block(width = 1, height = 1, color = MondrianColor.BLACK)
                        val blackBlock2 = Block(width = 2, height = 1, color = MondrianColor.BLACK)
                        val blackBlock3 = Block(width = 3, height = 1, color = MondrianColor.BLACK)

                        blockDao.addBlock(blackBlock1)
                        blockDao.addBlock(blackBlock2)
                        blockDao.addBlock(blackBlock3)

                        // colored blocks
                        blockDao.addBlock(
                            Block(
                                width = 4, height = 1, color = MondrianColor.RED
                            )
                        )
                        blockDao.addBlock(
                            Block(
                                width = 5, height = 1, color = MondrianColor.RED
                            )
                        )

                        blockDao.addBlock(
                            Block(
                                width = 3, height = 2, color = MondrianColor.BLUE
                            )
                        )
                        blockDao.addBlock(
                            Block(
                                width = 4, height = 2, color = MondrianColor.BLUE
                            )
                        )
                        blockDao.addBlock(
                            Block(
                                width = 5, height = 2, color = MondrianColor.BLUE
                            )
                        )

                        blockDao.addBlock(
                            Block(
                                width = 2, height = 2, color = MondrianColor.YELLOW
                            )
                        )
                        blockDao.addBlock(
                            Block(
                                width = 3, height = 3, color = MondrianColor.YELLOW
                            )
                        )

                        blockDao.addBlock(
                            Block(
                                width = 4, height = 3, color = MondrianColor.WHITE
                            )
                        )

                        // puzzles

                        // easy 1-22

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 1, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 7,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 2, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 7,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 3, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 4, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 2,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 2,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 5, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 6, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 7, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 8, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 9, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 10, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 11, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 12, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 13, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 14, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 15, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 16, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 17, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 2,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 18, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 19, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 20, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 21, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 7,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 22, difficulty = PuzzleType.EASY, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        // medium 23-44

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 23, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 24, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 25, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 7,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 26, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 2,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 27, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 28, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 29, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 30, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 31, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 8,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 32, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 33, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 8,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 34, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 35, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 2,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 36, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 37, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 38, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 39, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 40, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 41, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 42, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 43, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 8,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 44, difficulty = PuzzleType.MEDIUM, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 8,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        // hard 45-66

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 45, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 46, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 2,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 47, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 48, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 7,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 7,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 49, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 7,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 50, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 51, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 52, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 53, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 2,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 54, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 55, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 56, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 57, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 2,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 2,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 58, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 59, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 7,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 60, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 61, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 62, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 63, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 7,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 2,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 64, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 2,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 65, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 66, difficulty = PuzzleType.HARD, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 1,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        // extreme 67-88

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 67, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 8,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 68, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 7,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 69, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 8,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 70, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 2,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 71, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 72, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 7,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 73, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 74, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 75, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 7,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 8,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 4,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 76, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 5,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 77, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 7,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 8,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 78, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 3,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 8,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 79, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 5,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 80, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 1,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 81, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 6,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 82, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 1,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 83, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 2,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 84, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 8,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 85, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 4,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 8,
                                        y = 4,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 86, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 6,
                                        y = 4,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 7,
                                        y = 8,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 6,
                                        y = 5,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 87, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 3,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 5,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 3,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    )
                                ), blocks = listOf()
                            )
                        )

                        puzzleDao.addPuzzle(
                            Puzzle(
                                id = 88, difficulty = PuzzleType.EXTREME, blackBlocks = listOf(
                                    PuzzleBlock(
                                        block = blackBlock1,
                                        x = 5,
                                        y = 6,
                                        orientation = MondrianOrientation.HORIZONTAL
                                    ), PuzzleBlock(
                                        block = blackBlock2,
                                        x = 4,
                                        y = 3,
                                        orientation = MondrianOrientation.VERTICAL
                                    ), PuzzleBlock(
                                        block = blackBlock3,
                                        x = 1,
                                        y = 1,
                                        orientation = MondrianOrientation.VERTICAL
                                    )
                                ), blocks = listOf()
                            )
                        )
                    }
                }
            }
        }).allowMainThreadQueries().build()
    }

    @Provides
    fun provideUserDao(userDatabase: AppDatabase): UserDao {
        return userDatabase.userDao()
    }

    @Provides
    fun provideBlockDao(appDatabase: AppDatabase): BlockDao {
        return appDatabase.blockDao()
    }

    @Provides
    fun providePuzzleDao(appDatabase: AppDatabase): PuzzleDao {
        return appDatabase.puzzleDao()
    }
}