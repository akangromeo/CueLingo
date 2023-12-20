package com.example.cuelingo.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cuelingo.R
import com.example.cuelingo.data.local.database.dummyData.LeaderboardItem

class RankAdapter(private val items: List<LeaderboardItem>) :
    RecyclerView.Adapter<RankAdapter.ViewHolder>() {

    // ViewHolder class to hold references to the views in each item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankTextView: TextView = itemView.findViewById(R.id.tv_rank)
        val dictionaryNameTextView: TextView = itemView.findViewById(R.id.tv_dictionary_name)
    }

    // Create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leaderboard_item, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the views in each item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.rankTextView.text = item.rank
        holder.dictionaryNameTextView.text = item.dictionaryName
    }

    // Return the number of items in the data set
    override fun getItemCount(): Int {
        return items.size
    }
}