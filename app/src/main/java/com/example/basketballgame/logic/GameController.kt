package com.example.basketballgame.logic

import android.content.Context
import android.media.MediaPlayer
import com.example.basketballgame.utilities.SignalManager
import com.example.basketballgame.config.Constants
import com.example.basketballgame.R

class GameController(
    private val context: Context,
    private val listener: GameListener,
    private val rows: Int = Constants.ROWS,
    private val cols: Int = Constants.COLS,
    private val maxLives: Int = Constants.MAX_LIVES
) {
    private val gameManager = GameManager(rows, cols, maxLives)

    var playerPosition: Int = cols / 2
    var score: Int = 0
        private set

    fun gameTick() {
        gameManager.clearLastRow()
        gameManager.moveObstaclesDown()

        val lastRow = gameManager.getMatrix().last()
        val item = lastRow[playerPosition]

        if (item == Constants.OBJECT_OBSTACLE) {
            SignalManager.getInstance().vibrate()
            SignalManager.getInstance().toast("You lose the ball!")
            MediaPlayer.create(context, R.raw.ball_pop).start()
            gameManager.handleCrash()
            listener.updateLives(gameManager.lives)
            if (gameManager.isGameOver) {
                listener.gameOver(score)
                return
            }
        } else if (item == Constants.OBJECT_HOOP) {
            score += 2
            SignalManager.getInstance().toast("Scored!")
            MediaPlayer.create(context, R.raw.score).start()
        } else if (item == Constants.OBJECT_HEART) {
            if (gameManager.lives < maxLives) {
                gameManager.addLife()
                listener.updateLives(gameManager.lives)
                SignalManager.getInstance().toast("Life +1")
                MediaPlayer.create(context, R.raw.heart_pickup).start()
            }
        }

        gameManager.addNewObstacle()
        if ((0..2).random() == 0) gameManager.addNewHoop()
        if ((0..9).random() == 0) gameManager.addNewHeart()

        listener.updateObstacles(gameManager.getMatrix())
    }

    fun movePlayerLeft() {
        if (playerPosition > 0) playerPosition--
    }

    fun movePlayerRight() {
        if (playerPosition < cols - 1) playerPosition++
    }

    fun isGameOver(): Boolean {
        return gameManager.isGameOver
    }

}
