package com.example.cuelingo.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cuelingo.data.remote.response.DictionaryItem
import com.example.cuelingo.databinding.ItemBinding

class DictionaryAdapter(private val onItemClickCallback: OnItemClickCallBack) : ListAdapter<DictionaryItem, DictionaryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemCallback: OnItemClickCallBack? = null

    inner class MyViewHolder(private val binding: ItemBinding ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dictionaryItem: DictionaryItem){
            binding.tvDictionaryName.text = dictionaryItem.name!!
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
        fun onItemClicked(data: DictionaryItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DictionaryItem>() {
            override fun areItemsTheSame(oldItem: DictionaryItem, newItem: DictionaryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DictionaryItem, newItem: DictionaryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}