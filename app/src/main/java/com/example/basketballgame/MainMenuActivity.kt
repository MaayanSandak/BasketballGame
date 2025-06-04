package com.example.basketballgame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.basketballgame.config.ControlMode
import com.google.android.material.button.MaterialButton

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val btnButtonsFast = findViewById<MaterialButton>(R.id.btn_buttons_fast)
        val btnButtonsSlow = findViewById<MaterialButton>(R.id.btn_buttons_slow)
        val btnTilt = findViewById<MaterialButton>(R.id.btn_tilt)
        val btnHighScores = findViewById<MaterialButton>(R.id.btn_high_scores)

        btnButtonsFast.setOnClickListener {
            startGame(ControlMode.BUTTONS_FAST)
        }

        btnButtonsSlow.setOnClickListener {
            startGame(ControlMode.BUTTONS_SLOW)
        }

        btnTilt.setOnClickListener {
            startGame(ControlMode.TILT)
        }

        btnHighScores.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGame(mode: ControlMode) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("control_mode", mode.name)
        startActivity(intent)
    }
}