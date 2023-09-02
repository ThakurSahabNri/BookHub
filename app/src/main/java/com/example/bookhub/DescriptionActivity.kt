package com.example.bookhub

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookhub.dataBase.BookDatabase
import com.example.bookhub.dataBase.BookEntity
import com.example.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddFavourites: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolBar: Toolbar

    var bookId: String? = "100"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description2)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        btnAddFavourites = findViewById(R.id.btnFavourites)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        imgBookImage = findViewById(R.id.imgBookImage)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        toolBar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error has occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error has occurred",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE

                            val bookImageUrl=bookJsonObject.getString("image")
                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_image).into(imgBookImage)

                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")



                            val bookEntity=BookEntity(
                                bookId?.toInt() as  Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav=checkFav.get()
                            if(isFav){
                                btnAddFavourites.text="Remove From Favourites"
                                val favColor=ContextCompat.getColor(applicationContext,R.color.color_favourites)
                                btnAddFavourites.setBackgroundColor(favColor)
                            }else{
                                btnAddFavourites.text="Add to Favourites"
                                val noFavColor=ContextCompat.getColor(applicationContext,R.color.teal_700)
                                btnAddFavourites.setBackgroundColor(noFavColor)
                            }

                            btnAddFavourites.setOnClickListener{
                                if(!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                                    val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                        val result=async.get()
                                        if(result){
                                            Toast.makeText(this@DescriptionActivity,"Book Added To Favourites"
                                            ,Toast.LENGTH_SHORT).show()

                                            btnAddFavourites.text="Remove From Favourites"
                                            val favColor=ContextCompat.getColor(applicationContext,R.color.color_favourites)
                                            btnAddFavourites.setBackgroundColor(favColor)
                                        }else{
                                          Toast.makeText(this@DescriptionActivity,"Some Error Has Occurred",
                                          Toast.LENGTH_SHORT).show()

                                        }
                                }else{
                                    val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result=async.get()
                                    if(result){
                                        Toast.makeText(this@DescriptionActivity,"Book Removed From Favourites"
                                            ,Toast.LENGTH_SHORT).show()

                                        btnAddFavourites.text="Add To Favourites"
                                        val favColor=ContextCompat.getColor(applicationContext,R.color.teal_700)
                                        btnAddFavourites.setBackgroundColor(favColor)

                                    }else{
                                        Toast.makeText(this@DescriptionActivity,"Some Error Has Occurred",
                                            Toast.LENGTH_SHORT).show()

                                    }
                                }


                            }

                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some unexpected error has been occurred",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some unexpected error has been occurred ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }, Response.ErrorListener {

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {

            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    class DBAsyncTask(val context: Context,val bookEntity: BookEntity,val mode:Int):AsyncTask<Void,Void,Boolean>(){

val db=Room.databaseBuilder(context,BookDatabase::class.java,"book_db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
        when(mode){
            1->{
             //check book is in db or not
             val book:BookEntity?=db.booksDao().getBookId(bookEntity.book_id.toString())
                db.close()
                return book != null
            }
            2->{
             //add book to favourites in DB
             db.booksDao().insertBook(bookEntity)
                db.close()
                return true


            }
            3->{
                //remove book from DB
            db.booksDao().deleteBook(bookEntity)
            db.close()
            return true
            }
        }


            return false
        }

    }
}


