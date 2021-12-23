package io.github.a2en.mydiario.ui.home.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.a2en.mydiario.databinding.FragmentHomeBinding
import io.github.a2en.mydiario.databinding.HomeListItemBinding
import io.github.a2en.mydiario.domain.DiaryEntry

class HomeAdapter(var itemClickListener: (diaryEntry: DiaryEntry) -> Unit,var deleteClickListener: (diaryEntry: DiaryEntry) -> Unit): ListAdapter<DiaryEntry, HomeAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: HomeListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(diaryEntry: DiaryEntry) {
            binding.diaryEntry = diaryEntry
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
        holder.binding.delete.setOnClickListener {
            deleteClickListener(item)
        }
        holder.bind(item)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<DiaryEntry>() {
        override fun areItemsTheSame(oldItem: DiaryEntry, newItem: DiaryEntry): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DiaryEntry, newItem: DiaryEntry): Boolean {
            return oldItem.id == newItem.id
        }
    }

}