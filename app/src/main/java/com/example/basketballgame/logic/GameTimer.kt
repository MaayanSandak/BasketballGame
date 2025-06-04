package com.example.basketballgame.logic

import android.os.Handler
import android.os.Looper

class GameTimer(
    private var interval: Long,
    private val onTick: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private val runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                onTick()
                handler.postDelayed(this, interval)
            }
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            handler.postDelayed(runnable, interval)
        }
    }

    fun stop() {
        isRunning = false
        handler.removeCallbacks(runnable)
    }

    fun updateInterval(newInterval: Long) {
        interval = newInterval
    }
}
