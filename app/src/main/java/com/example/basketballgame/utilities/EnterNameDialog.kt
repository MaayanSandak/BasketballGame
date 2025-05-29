package com.example.basketballgame.utilities

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

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
            .setNegativeButton("Cancel", null)
            .show()
    }
}
