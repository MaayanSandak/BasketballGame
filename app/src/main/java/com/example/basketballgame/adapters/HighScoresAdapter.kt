package com.example.basketballgame.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.basketballgame.R
import com.example.basketballgame.models.HighScore

class HighScoresAdapter(
    private val scores: List<HighScore>,
    private val onLocationClick: (HighScore) -> Unit
) : RecyclerView.Adapter<HighScoresAdapter.ScoreViewHolder>() {

    inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.score_name)
        val scoreText: TextView = itemView.findViewById(R.id.score_value)
        val locationBtn: ImageButton = itemView.findViewById(R.id.btn_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]
        holder.nameText.text = score.name
        holder.scoreText.text = "Score: ${score.score}"
        holder.locationBtn.setOnClickListener { onLocationClick(score) }
    }

    override fun getItemCount(): Int = scores.size
}
