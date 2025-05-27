package com.example.basketballgame.logic

import com.example.basketballgame.config.Constants

class GameManager(
    private val rows: Int = Constants.ROWS,
    private val cols: Int = Constants.COLS,
    private val maxLives: Int = Constants.MAX_LIVES
) {
    var lives: Int = maxLives
        private set

    private val matrix: Array<IntArray> = Array(rows) { IntArray(cols) { 0 } }

    var isGameOver: Boolean = false
        private set

    fun clearLastRow() {
        for (j in 0 until cols) {
            matrix[rows - 1][j] = 0
        }
    }

    fun moveObstaclesDown() {
        for (i in rows - 1 downTo 1) {
            for (j in 0 until cols) {
                matrix[i][j] = matrix[i - 1][j]
            }
        }
        for (j in 0 until cols) {
            matrix[0][j] = 0
        }
    }

    fun addNewObstacle() {
        var col = kotlin.random.Random.nextInt(0, cols)

        var count = 0
        for (row in matrix.indices) {
            if (matrix[row][col] == 1) {
                count++
            } else {
                count = 0
            }
            if (count >= 2) {
                val otherCols = (0 until cols).filter { it != col }
                col = otherCols.random()
                break
            }
        }

        matrix[0][col] = 1
    }

    fun addNewHoop() {
        val col = (0 until cols).random()
        matrix[0][col] = 2
    }

    fun addNewHeart() {
        if (lives < maxLives) {
            val col = (0 until cols).random()
            matrix[0][col] = 3
        }
    }

    fun addLife() {
        if (lives < maxLives) {
            lives++
        }
    }


    fun checkCollision(playerPosition: Int): Boolean {
        if (rows < 1) return false
        return matrix[rows - 1][playerPosition] == 1
    }

    fun handleCrash() {
        lives--
        if (lives <= 0) {
            isGameOver = true
        }
    }

    fun getMatrix(): Array<IntArray> {
        return matrix
    }
}