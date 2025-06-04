package com.example.basketballgame.utilities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.EditText
import com.example.basketballgame.MainMenuActivity

object EnterNameDialog {
    fun show(context: Context, onNameEntered: (String) -> Unit) {
        val input = EditText(context)
        AlertDialog.Builder(context)
            .setTitle("New High Score!")
            .setMessage("Enter your name:")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    onNameEntered(name)
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                val intent = Intent(context, MainMenuActivity::class.java)
                context.startActivity(intent)
                if (context is Activity) context.finish()
            }
            .show()
    }
}
