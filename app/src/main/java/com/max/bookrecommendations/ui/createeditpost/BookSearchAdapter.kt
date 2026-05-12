package com.max.bookrecommendations.ui.createeditpost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.max.bookrecommendations.R
import com.max.bookrecommendations.data.model.BookSearchItem

class BookSearchAdapter(
    private val books: MutableList<BookSearchItem>,
    private val onBookClick: (BookSearchItem) -> Unit
) : RecyclerView.Adapter<BookSearchAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.bookResultTitleTextView)
        val authorTextView: TextView = itemView.findViewById(R.id.bookResultAuthorTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_result, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.titleTextView.text = book.title
        holder.authorTextView.text = book.authors

        holder.itemView.setOnClickListener {
            onBookClick(book)
        }
    }

    override fun getItemCount(): Int = books.size

    fun updateBooks(newBooks: List<BookSearchItem>) {
        books.clear()
        books.addAll(newBooks)
        notifyDataSetChanged()
    }
}