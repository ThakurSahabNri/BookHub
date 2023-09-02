package com.example.bookhub.model

data class Book(
    val bookId:String,
    val bookName: String,
    var bookAuthor:String,
    var bookPrice: String,
    var bookRating:String,
    val bookImage: String
     )