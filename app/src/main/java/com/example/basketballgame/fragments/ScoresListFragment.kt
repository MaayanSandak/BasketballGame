package com.example.basketballgame.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basketballgame.R
import com.example.basketballgame.adapters.HighScoresAdapter
import com.example.basketballgame.models.HighScore
import com.example.basketballgame.utilities.HighScoresManager

class ScoresListFragment : Fragment() {

    private lateinit var adapter: HighScoresAdapter
    private var listener: OnScoreSelectedListener? = null

    interface OnScoreSelectedListener {
        fun onScoreSelected(location: Pair<Double, Double>)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScoreSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnScoreSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_scores_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_scores)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val scores = HighScoresManager.getTopScores(requireContext())
        adapter = HighScoresAdapter(scores) { score: HighScore ->
            listener?.onScoreSelected(Pair(score.latitude, score.longitude))
        }

        recyclerView.adapter = adapter
        return view
    }
}
