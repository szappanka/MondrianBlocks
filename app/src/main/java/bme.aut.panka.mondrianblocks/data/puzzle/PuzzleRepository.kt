package bme.aut.panka.mondrianblocks.data.puzzle

import javax.inject.Inject

class PuzzleRepository @Inject constructor(private val puzzleDao: PuzzleDao) {
    suspend fun addPuzzle(puzzle: Puzzle) = puzzleDao.addPuzzle(puzzle)
    fun getAllPuzzles() = puzzleDao.getAllPuzzles()
    fun getRandomPuzzle() = puzzleDao.getRandomPuzzle()
}