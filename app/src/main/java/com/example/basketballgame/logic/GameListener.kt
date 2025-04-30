package com.example.basketballgame.logic

interface GameListener {
    fun updateObstacles(matrix: Array<IntArray>)
    fun updateLives(lives: Int)
    fun gameOver(score: Int)
}