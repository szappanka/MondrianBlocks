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
import android.graphics.Rect
import bme.aut.panka.mondrianblocks.components.DisplayProcessedBitmap
import bme.aut.panka.mondrianblocks.features.processor.partials.BlueMaskProcessor
import bme.aut.panka.mondrianblocks.features.processor.partials.FindBlackAndHandProcessor
import bme.aut.panka.mondrianblocks.features.processor.partials.InitialisationProcessor
import bme.aut.panka.mondrianblocks.features.processor.partials.RawImageProcessor

enum class GameState {
    // még nincs semmi inicializálva
    STARTING,

    INITIALISING,

    // a megadott alakzatba belerakta, itt initeljük a színeket
    FIND_BLACK,

    // a színek inicializálva, ha a fekete elemek lekerülnek, akkor kezdődik a játék
    PLAYING,

    // a játék véget ért
    FINISHED
}

class GameActivity : ComponentActivity() {

    private var gameState by mutableStateOf(GameState.STARTING)
    private var processedBitmap by mutableStateOf<Bitmap?>(null)
    private var puzzleRect by mutableStateOf<Rect?>(null)


    val rawImageProcessor = RawImageProcessor()
    val blueMaskProcessor = BlueMaskProcessor().apply {
        onImageProcessed = { result ->
            processedBitmap = result.bitmap
            puzzleRect = result.boundingRect
        }
    }
    val initProcessor by lazy {
        InitialisationProcessor().apply {
            onInitProcessed = {
                runOnUiThread {
                    gameState = GameState.FIND_BLACK
                }

            }
        }
    }

    val findBlackAndHandProcessor = FindBlackAndHandProcessor()

    private var currentProcessor: ImageProcessor = blueMaskProcessor
    val selectedUser = GameData.selectedUser
    val selectedPuzzle = GameData.selectedPuzzle

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (OpenCVLoader.initLocal()) {
            Log.i("OpenCV", "OpenCV successfully loaded.")
        }

        getPermission()

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            val activity = this
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
                                    color = Color.Black,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                "Felhasználó: ${selectedUser?.name}",
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
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
                                        Text("INITIALISING")
                                        currentProcessor = initProcessor
                                    }

                                    GameState.FIND_BLACK -> {
                                        GameInit(selectedPuzzle!!)
                                        Text("COLOR_INIT")
                                        currentProcessor = findBlackAndHandProcessor
                                    }

                                    GameState.PLAYING -> {
                                        gameState = GameState.FINISHED
                                        currentProcessor = rawImageProcessor
                                    }

                                    GameState.FINISHED -> {
                                        gameState = GameState.STARTING
                                    }
                                }
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    CameraPreview(
                                        onPreviewViewCreated = { previewView ->
                                            startCamera(
                                                previewView,
                                                CameraSelector.LENS_FACING_BACK,
                                                this@GameActivity,
                                                this@GameActivity
                                            )
                                        },
                                        rectangle = puzzleRect,
                                        bitmap = processedBitmap
                                    )

                                    if(gameState == GameState.FIND_BLACK) {
                                        DisplayProcessedBitmap(processedBitmap)
                                    }
                                    //DisplayProcessedBitmap(processedBitmap)

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
                                            activity.finish()
                                        }, text = "Mérés befejezése"
                                    )

                                }
                            }
                        }

                    }
                }
            }
        }
    }

    fun startCamera(
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
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
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

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        }
    }
}