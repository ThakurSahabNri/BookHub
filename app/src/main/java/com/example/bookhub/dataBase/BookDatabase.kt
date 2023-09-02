package com.example.bookhub.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 1)
abstract class BookDatabase:RoomDatabase() {

  abstract fun booksDao():BookDao

}