package com.example.basketballgame.utilities

import android.content.Context
import android.location.Location
import com.example.basketballgame.models.HighScore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object HighScoresManager {
    private const val PREFS_NAME = "high_scores_prefs"
    private const val SCORES_KEY = "scores"

    fun saveScore(context: Context, newScore: HighScore) {
        val scores = getTopScores(context).toMutableList()
        scores.add(newScore)
        scores.sortByDescending { it.score }
        val top10 = scores.take(10)

        val json = Gson().toJson(top10)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(SCORES_KEY, json)
            .apply()
    }



    fun getTopScores(context: Context): List<HighScore> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(SCORES_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<HighScore>>() {}.type
        return Gson().fromJson(json, type)
    }
}
