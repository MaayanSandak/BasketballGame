package com.example.basketballgame.logic

import com.example.basketballgame.utilities.SignalManager
import com.example.basketballgame.config.Constants

class GameController(
    private val listener: GameListener,
    private val rows: Int = Constants.ROWS,
    private val cols: Int = Constants.COLS,
    private val maxLives: Int = Constants.MAX_LIVES
) {
    private val gameManager = GameManager(rows, cols, maxLives)

    var playerPosition: Int = 1
    private var lastCollision = false



    fun gameTick() {
        gameManager.clearLastRow()
        gameManager.moveObstaclesDown()

        if (gameManager.checkCollision(playerPosition)) {
            SignalManager.getInstance().vibrate()
            SignalManager.getInstance().toast("You lose the ball!")
            gameManager.handleCrash()
            listener.updateLives(gameManager.lives)

            if (gameManager.isGameOver) {
                listener.gameOver(0)
                return
            }
        }

        gameManager.addNewObstacle()
        listener.updateObstacles(gameManager.getMatrix())
    }



    fun movePlayerLeft() {
        if (playerPosition > 0) {
            playerPosition--
        }
    }

    fun movePlayerRight() {
        if (playerPosition < cols - 1) {
            playerPosition++
        }
    }
}
