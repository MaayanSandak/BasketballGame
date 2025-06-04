package com.example.basketballgame

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.basketballgame.config.Constants
import com.example.basketballgame.config.ControlMode
import com.example.basketballgame.logic.GameController
import com.example.basketballgame.logic.GameListener
import com.example.basketballgame.logic.GameTimer
import com.example.basketballgame.models.HighScore
import com.example.basketballgame.utilities.EnterNameDialog
import com.example.basketballgame.utilities.HighScoresManager
import com.example.basketballgame.utilities.SignalManager
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity(), GameListener {

    private lateinit var player: AppCompatImageView
    private lateinit var buttonLeft: MaterialButton
    private lateinit var buttonRight: MaterialButton
    private lateinit var hearts: Array<AppCompatImageView>
    private lateinit var gameOverText: TextView
    private lateinit var obstaclesLayer: FrameLayout
    private lateinit var scoreText: TextView

    private lateinit var gameTimer: GameTimer
    private lateinit var gameController: GameController
    private lateinit var controlMode: ControlMode

    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var sensorListener: SensorEventListener? = null
    private var lastTiltTime = 0L
    private var smoothedX = 0f
    private var lastSpeedLevel = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(this)
        requestLocationPermission()

        val modeString = intent.getStringExtra("control_mode")
        controlMode = ControlMode.valueOf(modeString ?: ControlMode.BUTTONS_SLOW.name)

        initViews()

        if (controlMode == ControlMode.TILT) {
            setupTiltControls()
        }

        startGame()
    }

    private fun requestLocationPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    private fun initViews() {
        player = findViewById(R.id.player)
        buttonLeft = findViewById(R.id.button_left)
        buttonRight = findViewById(R.id.button_right)
        gameOverText = findViewById(R.id.game_over_text)
        obstaclesLayer = findViewById(R.id.obstacles_layer)
        scoreText = findViewById(R.id.score_text)

        hearts = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        buttonLeft.setOnClickListener {
            gameController.movePlayerLeft()
            updatePlayerPosition()
        }
        buttonRight.setOnClickListener {
            gameController.movePlayerRight()
            updatePlayerPosition()
        }

        if (controlMode == ControlMode.TILT) {
            buttonLeft.visibility = View.GONE
            buttonRight.visibility = View.GONE
        }
    }

    private fun setupTiltControls() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val rawX = event.values[0]
                val rawY = event.values[1]
                val currentTime = System.currentTimeMillis()

                val alpha = 0.8f
                smoothedX = alpha * smoothedX + (1 - alpha) * rawX

                if (currentTime - lastTiltTime > 200) {
                    val sensitivity = 2.5f

                    if (smoothedX > sensitivity) {
                        gameController.movePlayerLeft()
                        updatePlayerPosition()
                    } else if (smoothedX < -sensitivity) {
                        gameController.movePlayerRight()
                        updatePlayerPosition()
                    }

                    lastTiltTime = currentTime
                }

                val speedLevel = when {
                    rawY < 3 -> 0
                    rawY > 7 -> 2
                    else -> 1
                }

                if (speedLevel != lastSpeedLevel) {
                    val newInterval = when (speedLevel) {
                        0 -> 400L
                        1 -> 550L
                        2 -> 750L
                        else -> 550L
                    }
                    gameTimer.updateInterval(newInterval)
                    lastSpeedLevel = speedLevel
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let {
            sensorManager?.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    private fun startGame() {
        val interval = when (controlMode) {
            ControlMode.BUTTONS_FAST -> 400L
            ControlMode.BUTTONS_SLOW -> 750L
            ControlMode.TILT -> 550L
        }

        gameController = GameController(this, this)
        gameTimer = GameTimer(interval) {
            gameController.gameTick()
            updateScore(gameController.score)
            if (gameController.isGameOver()) {
                gameTimer.stop()
            }
        }

        SignalManager.getInstance().toast(getString(R.string.toast_new_game_started))
        updatePlayerPosition()
        gameTimer.start()
    }

    private fun updatePlayerPosition() {
        player.post {
            val screenWidth = resources.displayMetrics.widthPixels
            val laneWidth = screenWidth / Constants.COLS
            val params = player.layoutParams as RelativeLayout.LayoutParams
            val effectiveWidth = if (player.width > 0) player.width else laneWidth / 2
            params.leftMargin = gameController.playerPosition * laneWidth + laneWidth / 2 - effectiveWidth / 2
            player.layoutParams = params
        }
    }

    override fun updateObstacles(matrix: Array<IntArray>) {
        obstaclesLayer.removeAllViews()
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val laneWidth = screenWidth / Constants.COLS
        val rowHeight = screenHeight / Constants.ROWS

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                val drawableRes = when (matrix[i][j]) {
                    Constants.OBJECT_OBSTACLE -> R.drawable.pin
                    Constants.OBJECT_HOOP -> R.drawable.hoop
                    Constants.OBJECT_HEART -> R.drawable.heart
                    else -> null
                }

                if (drawableRes != null) {
                    val itemView = AppCompatImageView(this)
                    itemView.setImageResource(drawableRes)
                    val (itemWidth, itemHeight) = when (matrix[i][j]) {
                        Constants.OBJECT_HOOP -> Pair(laneWidth * 5 / 6, rowHeight * 5 / 6)
                        Constants.OBJECT_OBSTACLE -> Pair(laneWidth * 4 / 5, rowHeight * 4 / 5)
                        Constants.OBJECT_HEART -> Pair((laneWidth * 2) / 3, (rowHeight * 2) / 3)
                        else -> Pair((laneWidth * 2) / 3, (rowHeight * 2) / 3)
                    }
                    val params = FrameLayout.LayoutParams(itemWidth, itemHeight)
                    params.leftMargin = j * laneWidth + laneWidth / 2 - itemWidth / 2
                    params.topMargin = i * rowHeight + rowHeight / 2 - itemHeight / 2
                    itemView.layoutParams = params
                    obstaclesLayer.addView(itemView)
                }
            }
        }
    }

    override fun updateLives(lives: Int) {
        for (i in hearts.indices) {
            hearts[i].visibility = if (i < lives) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updateScore(score: Int) {
        scoreText.text = "Score: $score"
    }

    override fun onPause() {
        super.onPause()
        gameTimer.stop()
        if (controlMode == ControlMode.TILT) {
            sensorManager?.unregisterListener(sensorListener)
        }
    }

    override fun gameOver(score: Int) {
        gameOverText.visibility = View.VISIBLE
        gameTimer.stop()

        SignalManager.getInstance().fetchLocation { location ->
            EnterNameDialog.show(this) { name ->
                if (location != null) {
                    val highScore = HighScore(name, score, location.latitude, location.longitude)
                    HighScoresManager.saveScore(this, highScore)
                }

                val intent = Intent(this, HighScoresActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
