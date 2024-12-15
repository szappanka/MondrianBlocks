package bme.aut.panka.mondrianblocks.features.menu.difficulty

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.components.MondrianButton

@Composable
fun CoglicaDifficultyView(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: CoglicaDifficultyViewModel = viewModel()
    val response by viewModel.response.collectAsState()
    val user by viewModel.user.collectAsState()
    val gameConfigurations by viewModel.gameConfigurations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> {
                Text("Loading...")
            }

            user != null && gameConfigurations.isNotEmpty() -> {
                Text("Szia ${user?.username}! Válassz pályát a \"Puzzle kiválasztása\" gombbal")
            }

            user != null -> {
                Text("Kedves ${user?.username}! Nem érhető el konfiguráció, jelentkezz be újra")
            }

            response != null -> {
                Text("Response received: $response")
            }

            else -> {
                Text("Something went wrong!")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (user != null && gameConfigurations.isNotEmpty()) {
                MondrianButton(
                    text = "Puzzle kiválasztása",
                    onClick = onNextClick
                )
            }
            MondrianButton(
                text = "Vissza",
                onClick = {
                    GameData.coglicaPuzzleId = null
                    onBackClick
                }
            )
        }
    }
}

