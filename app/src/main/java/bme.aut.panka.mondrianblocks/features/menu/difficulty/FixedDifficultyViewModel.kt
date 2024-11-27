package bme.aut.panka.mondrianblocks.features.menu.difficulty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FixedDifficultyViewModel @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) : ViewModel() {

    private val _puzzles = MutableStateFlow<List<Puzzle>>(emptyList())
    val puzzles: StateFlow<List<Puzzle>> = _puzzles

    init {
        viewModelScope.launch {
            puzzleRepository.getAllPuzzles().collect { puzzles ->
                _puzzles.value = puzzles
            }
        }
    }

    fun savePuzzle(puzzle: Puzzle) {
        GameData.selectedPuzzle = puzzle
    }
}