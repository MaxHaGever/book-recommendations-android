package com.max.bookrecommendations.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(
    private val posts: MutableList<Post>,
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.postImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.postTitleTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.postAuthorTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.postDescriptionTextView)
        val ownerTextView: TextView = itemView.findViewById(R.id.postOwnerTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)

        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.titleTextView.text = post.bookTitle
        holder.authorTextView.text = post.bookAuthor
        holder.descriptionTextView.text = post.description
        holder.ownerTextView.text = "Shared by ${post.ownerName}"

        val imageUrl = post.customImageUrl ?: post.bookThumbnailUrl

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.default_book)
                .error(R.drawable.default_book)
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.default_book)
        }

        holder.itemView.setOnClickListener {
            onPostClick(post)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun submitPosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}
