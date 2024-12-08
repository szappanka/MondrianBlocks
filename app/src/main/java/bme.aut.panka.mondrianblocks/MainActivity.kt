package bme.aut.panka.mondrianblocks

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.features.menu.MainMenuView
import bme.aut.panka.mondrianblocks.ui.theme.MondrianBlocksTheme
import org.opencv.android.OpenCVLoader
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import bme.aut.panka.mondrianblocks.features.menu.difficulty.DifficultyMenuView
import bme.aut.panka.mondrianblocks.features.menu.difficulty.FixedDifficultyView
import bme.aut.panka.mondrianblocks.features.menu.difficulty.RandomDifficultyView
import bme.aut.panka.mondrianblocks.features.menu.user.CoglicaUserView
import bme.aut.panka.mondrianblocks.features.menu.user.NewUserView
import bme.aut.panka.mondrianblocks.features.menu.user.SavedUserView
import bme.aut.panka.mondrianblocks.features.menu.user.UserMenuView
import bme.aut.panka.mondrianblocks.features.results.ResultsView
import bme.aut.panka.mondrianblocks.ui.theme.yellow
import dagger.hilt.android.AndroidEntryPoint

enum class ScreenState {
    MAIN_MENU, USER_MENU, NEW_USER_MENU, SAVED_USER_MENU, COGLICA_USER_MENU, RESULTS, DIFFICULTY, DIFFICULTY_RANDOM, DIFFICULTY_FIXED, GAME
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val _currentScreen = mutableStateOf(ScreenState.MAIN_MENU)
    private var shouldResetScreen = true
    private var authCode by mutableStateOf("")

    private fun setScreen(screenState: ScreenState) {
        _currentScreen.value = screenState
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val savedToken = loadToken()

        if (savedToken != null) {
            authCode = savedToken
            setScreen(ScreenState.COGLICA_USER_MENU)
        } else {
            setScreen(ScreenState.MAIN_MENU)
        }

        setContent {
            MondrianBlocksTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    containerColor = yellow,
                ) {
                    MainContent()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Important to ensure 'getIntent()' returns the latest intent
        handleIntent(intent) // Handle the new intent
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.let { uri ->
                handleDeeplink(uri)
            }
        }
    }

    private fun handleDeeplink(data: Uri) {
        val code = data.getQueryParameter("code")
        if (code != null) {
            authCode = code
            shouldResetScreen = false // Prevent onResume from resetting screen
            setScreen(ScreenState.COGLICA_USER_MENU)
            Log.d("Panku", "Handled deep link with code: $code")
        } else {
            Log.d("Panku", "Deep link did not contain a code")
        }
    }

    override fun onResume() {
        super.onResume()
        if (shouldResetScreen) {
            setScreen(ScreenState.MAIN_MENU)
            Log.d("Panku", "Screen reset to main menu")
        } else {
            shouldResetScreen = true
        }
    }

    private fun loadToken(): String? {
        val sharedPreferences = getSharedPreferences("coglica_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
    }

    private fun clearToken() {
        val sharedPreferences = getSharedPreferences("coglica_prefs", MODE_PRIVATE)
        sharedPreferences.edit().remove("access_token").apply()
    }

    @Composable
    private fun MainContent() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(id = R.string.logo),
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxWidth(),
                alignment = Alignment.Center

            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (_currentScreen.value) {
                    ScreenState.MAIN_MENU -> MainMenuView(onNewGameClick = {
                        setScreen(
                            ScreenState.USER_MENU
                        )
                    },
                        onResultsClick = { setScreen(ScreenState.RESULTS) },
                        onExitClick = {
                            clearToken()
                            this@MainActivity.finish()
                        }
                    )

                    ScreenState.USER_MENU -> UserMenuView(
                        onNewUserClick = { setScreen(ScreenState.NEW_USER_MENU) },
                        onSaveUserClick = { setScreen(ScreenState.SAVED_USER_MENU) },
                        onCoglicaUserClick = { setScreen(ScreenState.COGLICA_USER_MENU) },
                        onBackClick = { setScreen(ScreenState.MAIN_MENU) },
                    )

                    ScreenState.NEW_USER_MENU -> NewUserView(
                        onNextClick = { setScreen(ScreenState.DIFFICULTY) },
                        onBackClick = { setScreen(ScreenState.USER_MENU) },
                    )

                    ScreenState.SAVED_USER_MENU -> SavedUserView(
                        onNextClick = { setScreen(ScreenState.DIFFICULTY) },
                        onBackClick = { setScreen(ScreenState.USER_MENU) },
                    )

                    ScreenState.COGLICA_USER_MENU -> CoglicaUserView(
                        onNextClick = { setScreen(ScreenState.DIFFICULTY) },
                        onBackClick = {
                            clearToken()
                            setScreen(ScreenState.USER_MENU)
                        },
                    )

                    ScreenState.RESULTS -> ResultsView(
                        onBackClick = { setScreen(ScreenState.MAIN_MENU) },
                    )

                    ScreenState.DIFFICULTY -> DifficultyMenuView(
                        onRandomClick = { setScreen(ScreenState.DIFFICULTY_RANDOM) },
                        onFixedClick = { setScreen(ScreenState.DIFFICULTY_FIXED) },
                        onBackClick = { setScreen(ScreenState.USER_MENU) },
                    )

                    ScreenState.DIFFICULTY_RANDOM -> RandomDifficultyView(
                        onNextClick = { setScreen(ScreenState.GAME) },
                        onBackClick = { setScreen(ScreenState.DIFFICULTY) },
                    )

                    ScreenState.DIFFICULTY_FIXED -> FixedDifficultyView(
                        onNextClick = { setScreen(ScreenState.GAME) },
                        onBackClick = { setScreen(ScreenState.DIFFICULTY) },
                    )

                    ScreenState.GAME -> {
                        val intent = Intent(this@MainActivity, GameActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainMenuViewPreview() {
    MondrianBlocksTheme {
        MainMenuView(onNewGameClick = {}, onResultsClick = {}, onExitClick = {})
    }
}