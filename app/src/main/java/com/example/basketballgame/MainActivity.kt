package com.example.basketballgame

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.basketballgame.config.Constants
import com.example.basketballgame.logic.GameController
import com.example.basketballgame.logic.GameListener
import com.example.basketballgame.logic.GameTimer
import com.example.basketballgame.utilities.SignalManager

class MainActivity : AppCompatActivity(), GameListener {

    private lateinit var player: ImageView
    private lateinit var buttonLeft: ImageButton
    private lateinit var buttonRight: ImageButton
    private lateinit var hearts: Array<ImageView>
    private lateinit var gameOverText: TextView
    private lateinit var obstaclesLayer: FrameLayout

    private lateinit var gameTimer: GameTimer
    private lateinit var gameController: GameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(this)

        initViews()
        initGame()
    }

    private fun initViews() {
        player = findViewById(R.id.player)
        buttonLeft = findViewById(R.id.button_left)
        buttonRight = findViewById(R.id.button_right)
        gameOverText = findViewById(R.id.game_over_text)
        obstaclesLayer = findViewById(R.id.obstacles_layer)

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
    }

    private fun initGame() {
        gameController = GameController(this)
        gameTimer = GameTimer(Constants.TIMER_INTERVAL) {
            gameController.gameTick()
        }
        player.post {
            updatePlayerPosition()
        }
        gameTimer.start()
    }

    private fun updatePlayerPosition() {
        val screenWidth = resources.displayMetrics.widthPixels
        val laneWidth = screenWidth / 3

        val params = player.layoutParams as FrameLayout.LayoutParams
        params.leftMargin = gameController.playerPosition * laneWidth + laneWidth / 2 - player.width / 2
        player.layoutParams = params
    }

    override fun updateObstacles(matrix: Array<IntArray>) {
        obstaclesLayer.removeAllViews()

        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val laneWidth = screenWidth / 3
        val rowHeight = screenHeight / 5

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == 1) {
                    val obstacle = ImageView(this)
                    obstacle.setImageResource(R.drawable.pin)

                    val params = FrameLayout.LayoutParams(laneWidth / 2, rowHeight / 2)
                    params.leftMargin = j * laneWidth + laneWidth / 2 - (laneWidth / 4)
                    params.topMargin = i * rowHeight + rowHeight / 2 - (rowHeight / 4)

                    obstacle.layoutParams = params
                    obstaclesLayer.addView(obstacle)
                }
            }
        }
    }

    override fun updateLives(lives: Int) {
        for (i in hearts.indices) {
            hearts[i].visibility = if (i < lives) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun restartGame() {
        SignalManager.getInstance().toast(getString(R.string.toast_new_game_started))

        gameOverText.visibility = View.GONE
        gameController = GameController(this)
        gameTimer = GameTimer(Constants.TIMER_INTERVAL) {
            gameController.gameTick()
        }
        updateLives(Constants.MAX_LIVES)
        obstaclesLayer.removeAllViews()
        player.post {
            updatePlayerPosition()
        }
        gameTimer.start()
    }


    override fun gameOver(score: Int) {
        gameOverText.visibility = View.VISIBLE
        gameTimer.stop()

        gameOverText.postDelayed({
            restartGame()
        }, 3000)
    }
}
