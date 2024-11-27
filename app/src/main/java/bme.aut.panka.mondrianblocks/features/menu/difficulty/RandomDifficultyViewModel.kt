package bme.aut.panka.mondrianblocks.features.menu.difficulty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleRepository
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomDifficultyViewModel @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {
    private val _puzzles = MutableStateFlow<List<Puzzle>>(emptyList())

    private val _puzzle = MutableStateFlow<Puzzle?>(null)
    val puzzle: StateFlow<Puzzle?> = _puzzle

    init {
        viewModelScope.launch {
            puzzleRepository.getAllPuzzles().collect { puzzles ->
                _puzzles.value = puzzles
            }

        }
    }

    fun getRandomPuzzle(type: PuzzleType? = null) {
        val filteredPuzzles = if (type != null) {
            _puzzles.value.filter { it.difficulty == type }
        } else {
            _puzzles.value
        }

        _puzzle.value = filteredPuzzles.filterNot { it == _puzzle.value }.randomOrNull() ?: filteredPuzzles.randomOrNull()
    }

    fun savePuzzle() {
        GameData.selectedPuzzle = _puzzle.value
    }
}