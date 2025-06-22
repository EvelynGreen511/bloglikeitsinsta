package com.example.bloglikeitsinsta.ui.home

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bloglikeitsinsta.databinding.ItemPostBinding
import com.example.bloglikeitsinsta.wordpress.model.WordPressPost
import java.text.SimpleDateFormat
import java.util.*

class PostsAdapter(
    private val onPostClick: (WordPressPost) -> Unit
) : ListAdapter<WordPressPost, PostsAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onPostClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val onPostClick: (WordPressPost) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: WordPressPost) {
            binding.apply {
                textTitle.text = Html.fromHtml(post.title.rendered, Html.FROM_HTML_MODE_COMPACT)
                textExcerpt.text = Html.fromHtml(post.excerpt.rendered, Html.FROM_HTML_MODE_COMPACT)
                textDate.text = formatDate(post.date)

                root.setOnClickListener {
                    onPostClick(post)
                }
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString
            }
        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<WordPressPost>() {
        override fun areItemsTheSame(oldItem: WordPressPost, newItem: WordPressPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WordPressPost, newItem: WordPressPost): Boolean {
            return oldItem == newItem
        }
    }
}