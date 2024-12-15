package bme.aut.panka.mondrianblocks

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import bme.aut.panka.mondrianblocks.components.CameraPreview
import bme.aut.panka.mondrianblocks.components.MondrianButton
import bme.aut.panka.mondrianblocks.features.processor.GameInit
import bme.aut.panka.mondrianblocks.features.processor.GameStarting
import bme.aut.panka.mondrianblocks.features.processor.partials.ImageProcessor
import bme.aut.panka.mondrianblocks.ui.theme.MondrianBlocksTheme
import bme.aut.panka.mondrianblocks.ui.theme.yellow
import org.opencv.android.OpenCVLoader
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import android.os.SystemClock
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.puzzle.toFormattedString
import bme.aut.panka.mondrianblocks.features.processor.GamePlaying
import bme.aut.panka.mondrianblocks.features.processor.partials.BlueMaskProcessor
import bme.aut.panka.mondrianblocks.features.processor.partials.FileHandler
import bme.aut.panka.mondrianblocks.features.processor.partials.FindBlackAndHandProcessor
import bme.aut.panka.mondrianblocks.features.processor.partials.InitialisationProcessor
import bme.aut.panka.mondrianblocks.features.processor.partials.PuzzleMatchingProcessor
import bme.aut.panka.mondrianblocks.network.ApiService
import bme.aut.panka.mondrianblocks.network.CoglicaAuthManager
import bme.aut.panka.mondrianblocks.network.GameplayResult
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.toString

enum class GameState {
    // a játék indítása folyamatban
    STARTING,

    // a színek inicializálása folyamatban
    INITIALISING,

    // a fekete elemek keresése
    FIND_BLACK,

    // a játék folyamatban van
    PLAYING,

    // a játék véget ért
    FINISHED
}

class GameActivity : ComponentActivity() {

    private var gameState by mutableStateOf(GameState.STARTING)
    private var processedBitmap by mutableStateOf<Bitmap?>(null)
    private var puzzleRect by mutableStateOf<Rect?>(null)
    private var puzzleLandmarks by mutableStateOf<List<List<PointF>>?>(null)
    val fileHandler = FileHandler(this)
    private lateinit var authManager: CoglicaAuthManager
    private var isHeaderAdded = false
    var savedToken = ""

    @Inject
    lateinit var apiService: ApiService

    private val puzzleStateLog = StringBuilder()

    private fun logPuzzleState() {
        if (!isHeaderAdded) {
            puzzleStateLog.append("time;puzzle;\n")
            isHeaderAdded = true
        }

        val elapsedTime =
            SystemClock.elapsedRealtime() - (playingStartTime ?: SystemClock.elapsedRealtime())
        val formattedPuzzle = actualPuzzle?.toFormattedString() ?: "N/A"
        puzzleStateLog.append("${elapsedTime};$formattedPuzzle;\n")
    }

    private fun savePuzzleStateToFile() {
        val fileName = selectedUser?.id ?: 0
        fileHandler.saveToFile(fileName.toString(), puzzleStateLog.toString())
    }

    private fun createGameplayLog(): String {
        val log = GameplayResult(solution = puzzleStateLog.toString())
        return Gson().toJson(log)
    }


    private fun sendPuzzleInfoToServer() {
        val gameplayLog = createGameplayLog()

        lifecycleScope.launch {
            try {
                val response = apiService.postGameplayResult(gameplayLog)
                if (response.isSuccessful) {
                    Log.d("GameActivity", "Mérési adatok sikeresen elküldve.")
                } else {
                    Log.e(
                        "GameActivity",
                        "Hiba az adatok elküldésekor: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("GameActivity", "Hiba történt: ${e.localizedMessage}")
            }
        }
    }


    val selectedUser = GameData.selectedUser
    val selectedPuzzle = GameData.selectedPuzzle
    var actualPuzzle by mutableStateOf<Puzzle?>(GameData.selectedPuzzle)
    private var playingStartTime: Long? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupContent()
        } else {
            showPermissionRequestScreen()
        }
    }

    private lateinit var findBlackAndHandProcessor: FindBlackAndHandProcessor
    private var blueMaskProcessor: BlueMaskProcessor = BlueMaskProcessor().apply {
        onImageProcessed = { result ->
            processedBitmap = result.bitmap
            puzzleRect = result.boundingRect
        }
    }
    private lateinit var initProcessor: InitialisationProcessor
    private lateinit var puzzleMatchingProcessor: PuzzleMatchingProcessor

    private var currentProcessor: ImageProcessor = blueMaskProcessor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        authManager = CoglicaAuthManager(application)
        savedToken = authManager.loadToken().toString()
        initProcessors()

        if (OpenCVLoader.initLocal()) {
            Log.i("OpenCV", "OpenCV successfully loaded.")
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            showPermissionRequestScreen()
        } else {
            setupContent()
        }
    }

    fun cameraSession(
        previewView: PreviewView,
        lensFacing: Int,
        context: Context,
        lifecycleOwner: LifecycleOwner,
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->

                        val bitmap = currentProcessor.processImageProxyToBitmap(imageProxy)

                        if (gameState == GameState.STARTING) {
                            val result = currentProcessor.process(bitmap, puzzleRect)
                            processedBitmap = result?.bitmap
                            puzzleRect = result?.boundingRect
                        } else if (gameState == GameState.INITIALISING && currentProcessor::class.simpleName == "InitialisationProcessor") {
                            val result = currentProcessor.process(bitmap, puzzleRect)
                            if (result != null) {
                                processedBitmap = result.bitmap
                                puzzleRect = result.boundingRect
                                gameState = GameState.FIND_BLACK
                            }
                        } else if (gameState == GameState.FIND_BLACK || gameState == GameState.PLAYING) {
                            val result = currentProcessor.process(bitmap, puzzleRect)
                            if (result != null) {
                                processedBitmap = result.bitmap
                                puzzleRect = result.boundingRect
                                puzzleLandmarks = result.landmarks
                            }
                        } else {
                            val result = currentProcessor.process(bitmap, puzzleRect)
                            if (result != null) {
                                processedBitmap = result.bitmap
                            }
                        }

                        imageProxy.close()
                    }
                }

            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, analysis
                )
            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }


    override fun onDestroy() {
        super.onDestroy()
        initProcessor.cleanup()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private fun showPermissionRequestScreen() {
        setContent {
            MondrianBlocksTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = yellow,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Kérjük, engedélyezze a kamera hozzáférést a továbbiakban.",
                            style = TextStyle(
                                color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    private fun setupContent() {
        setContent {
            MondrianBlocksTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = yellow,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                        Text(
                            text = "Játék", style = TextStyle(
                                color = Color.Black, fontSize = 30.sp, fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Felhasználó: ${selectedUser?.name}", style = TextStyle(
                                color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold
                            )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            when (gameState) {
                                GameState.STARTING -> {
                                    GameStarting()
                                    currentProcessor = blueMaskProcessor
                                }

                                GameState.INITIALISING -> {
                                    GameStarting()
                                    currentProcessor = initProcessor
                                }

                                GameState.FIND_BLACK -> {
                                    GameInit(selectedPuzzle!!)
                                    currentProcessor = findBlackAndHandProcessor
                                }

                                GameState.PLAYING -> {
                                    GamePlaying(
                                        actualPuzzle!!,
                                        SystemClock.elapsedRealtime() - playingStartTime!!
                                    )
                                    currentProcessor = puzzleMatchingProcessor
                                }

                                GameState.FINISHED -> {
                                    Text("FINISHED")
                                }
                            }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                CameraPreview(
                                    onPreviewViewCreated = { previewView ->
                                        cameraSession(
                                            previewView,
                                            CameraSelector.LENS_FACING_BACK,
                                            this@GameActivity,
                                            this@GameActivity
                                        )
                                    },
                                    rectangle = puzzleRect,
                                    bitmap = processedBitmap,
                                    landmarks = puzzleLandmarks
                                )

                                if (gameState == GameState.STARTING || gameState == GameState.INITIALISING) {
                                    MondrianButton(
                                        onClick = {
                                            gameState = GameState.INITIALISING
                                        },
                                        enabled = gameState != GameState.INITIALISING,
                                        text = if (gameState == GameState.INITIALISING) "Betöltés..." else "Színek inicializálása"
                                    )
                                }

                                MondrianButton(
                                    onClick = {
                                        if (GameData.coglicaPuzzleId != null) {
                                            sendPuzzleInfoToServer()
                                        } else {
                                            savePuzzleStateToFile()
                                        }
                                        authManager.logout(
                                            savedToken,
                                            onLogoutComplete = { finish() },
                                            onError = { Log.d("GameActivity", "Logout error: $it") }
                                        )
                                        finish()
                                    }, text = "Mérés befejezése"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initProcessors() {
        initProcessor = InitialisationProcessor().apply {
            onInitProcessed = {
                runOnUiThread {
                    gameState = GameState.FIND_BLACK
                }
            }
        }

        findBlackAndHandProcessor = FindBlackAndHandProcessor(this@GameActivity).apply {
            onAllBlackPlaced = {
                runOnUiThread {
                    playingStartTime = SystemClock.elapsedRealtime()
                    gameState = GameState.PLAYING
                }
            }
        }

        puzzleMatchingProcessor = PuzzleMatchingProcessor(
            actualPuzzle = actualPuzzle,
            updateActualPuzzle = { newPuzzle ->
                actualPuzzle = newPuzzle
                logPuzzleState()
            },
            getPlayingStartTime = { playingStartTime },
            onGameFinished = {
                runOnUiThread {
                    gameState = GameState.FINISHED
                }
            },
            context = this@GameActivity
        )
    }
}