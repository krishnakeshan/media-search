package com.example.mediasearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediasearch.model.Media
import com.squareup.picasso.Picasso

class SearchAdapter(
    private var data: List<Media> = listOf()
) : RecyclerView.Adapter<SearchResultViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_search_result, parent, false)

        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val media = data[position]

        Picasso.get().load(media.url).into(holder.imageView)
    }

    override fun getItemCount(): Int = data.size

    fun getData() = data

    fun setData(newData: List<Media>) {
        data = newData
        notifyDataSetChanged()
    }
}

class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val imageView: AppCompatImageView = view.findViewById(R.id.searchResultImageView)

}