package com.example.basketballgame.fragments

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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HighScoresAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_scores_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_scores)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val scores = HighScoresManager.getTopScores(requireContext())
        adapter = HighScoresAdapter(scores) { location ->
            (activity as? OnScoreSelectedListener)?.onScoreSelected(location)
        }
        recyclerView.adapter = adapter

        return view
    }

    interface OnScoreSelectedListener {
        fun onScoreSelected(location: Pair<Double, Double>)
    }
}
