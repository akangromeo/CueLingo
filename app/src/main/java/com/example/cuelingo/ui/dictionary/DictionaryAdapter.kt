package com.example.cuelingo.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cuelingo.data.remote.response.ListDictionaryItem
import com.example.cuelingo.databinding.ItemBinding

class DictionaryAdapter(private val onItemClickCallback: OnItemClickCallBack) : ListAdapter<ListDictionaryItem, DictionaryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemCallback: OnItemClickCallBack? = null

    inner class MyViewHolder(private val binding: ItemBinding ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dictionaryItem: ListDictionaryItem){
            binding.tvDictionaryName.text = dictionaryItem.name!!

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val dictionary = dictionaryItem
                    onItemClickCallback.onItemClicked(dictionary)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dictionary = getItem(position)
        holder.bind(dictionary)
    }



    interface OnItemClickCallBack {
        fun onItemClicked(data: ListDictionaryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListDictionaryItem>() {
            override fun areItemsTheSame(oldItem: ListDictionaryItem, newItem: ListDictionaryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListDictionaryItem, newItem: ListDictionaryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}