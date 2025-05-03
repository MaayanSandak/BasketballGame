package com.example.basketballgame

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.basketballgame.config.Constants
import com.example.basketballgame.logic.GameController
import com.example.basketballgame.logic.GameListener
import com.example.basketballgame.logic.GameTimer
import com.example.basketballgame.utilities.SignalManager
import com.google.android.material.button.MaterialButton
import android.widget.RelativeLayout

class MainActivity : AppCompatActivity(), GameListener {

    private lateinit var player: AppCompatImageView
    private lateinit var buttonLeft: MaterialButton
    private lateinit var buttonRight: MaterialButton
    private lateinit var hearts: Array<AppCompatImageView>
    private lateinit var gameOverText: TextView
    private lateinit var obstaclesLayer: FrameLayout

    private lateinit var gameTimer: GameTimer
    private lateinit var gameController: GameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(this)

        initViews()
        startGame()
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

    private fun startGame() {
        gameController = GameController(this)
        gameTimer = GameTimer(Constants.TIMER_INTERVAL) {
            gameController.gameTick()
        }
        SignalManager.getInstance().toast(getString(R.string.toast_new_game_started))
        updatePlayerPosition()
        gameTimer.start()
    }

    private fun updatePlayerPosition() {
        player.post {
            val screenWidth = resources.displayMetrics.widthPixels
            val laneWidth = screenWidth / 3

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
        val laneWidth = screenWidth / 3
        val rowHeight = screenHeight / 5

        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == 1) {
                    val obstacle = AppCompatImageView(this)
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
        gameOverText.visibility = View.GONE
        updateLives(Constants.MAX_LIVES)
        obstaclesLayer.removeAllViews()
        startGame()
    }

    override fun gameOver(score: Int) {
        gameOverText.visibility = View.VISIBLE
        gameTimer.stop()

        gameOverText.postDelayed({
            restartGame()
        }, 3000)
    }
}
