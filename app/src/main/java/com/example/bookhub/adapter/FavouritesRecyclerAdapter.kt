package com.example.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookhub.DescriptionActivity
import com.example.bookhub.R
import com.example.bookhub.dataBase.BookEntity
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter(val context: Context,val bookList: List<BookEntity>):RecyclerView.Adapter<FavouritesRecyclerAdapter.FavoritesViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder{
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourites_single_row,parent,false)
        return FavoritesViewHolder(view)


    }



    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val book = bookList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookRating.text = book.bookRating
        holder.txtBookPrice.text = book.bookPrice
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_image)
            .into(holder.ivBookImage)

        holder.llFavContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", book.book_id.toString())
            context.startActivity(intent)


        }
    }
    override fun getItemCount(): Int {
        return bookList.size
    }


    class FavoritesViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtBookName:TextView=view.findViewById(R.id.favBookName)
        val txtBookAuthor:TextView=view.findViewById(R.id.favBookAuthor)
        val txtBookPrice:TextView=view.findViewById(R.id.favBookPrice)
        val txtBookRating:TextView=view.findViewById(R.id.favBookRating)
        val ivBookImage:ImageView=view.findViewById(R.id.ivFavBookImage)
        val llFavContent:LinearLayout=view.findViewById(R.id.llFavContent)

    }


}