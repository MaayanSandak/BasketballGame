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
    private lateinit var scoreText: TextView

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
    }

    private fun startGame() {
        gameController = GameController(this, this)
        gameTimer = GameTimer(Constants.TIMER_INTERVAL) {
            gameController.gameTick()
            updateScore(gameController.score)
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
    }

    private fun restartGame() {
        gameOverText.visibility = View.GONE
        updateLives(Constants.MAX_LIVES)
        obstaclesLayer.removeAllViews()
        updateScore(0)
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
