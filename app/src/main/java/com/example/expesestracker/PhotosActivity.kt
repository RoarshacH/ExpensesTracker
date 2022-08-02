package com.example.expesestracker

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expesestracker.models.Image
import com.example.expesestracker.models.ImageAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File


class PhotosActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 100
    private lateinit var pictureRecyclerView : RecyclerView
    private var progressbar: ProgressBar?= null
    lateinit var imageUri : Uri
    private var picturesList: ArrayList<Image> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        pictureRecyclerView = findViewById(R.id.pictureRecyclerView)
        progressbar = findViewById(R.id.imagesProgress)

        pictureRecyclerView?.layoutManager= GridLayoutManager(this, 3)
        pictureRecyclerView?.setHasFixedSize(true)


        requestPermissions()

        val btnTakePicture = findViewById<FloatingActionButton>(R.id.fabTakePicture)
        btnTakePicture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val imagePath = createImage()
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath)

            try{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
            catch (e: ActivityNotFoundException){
                Toast.makeText(this, "No Camera Found", Toast.LENGTH_SHORT ).show()
            }
        }

        picturesList = ArrayList()
        loadPictures()

    }

    override fun onResume() {
        super.onResume()
        loadPictures()
    }

     private fun getAllImages(): ArrayList<Image>? {
        val images = ArrayList<Image>()
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val path = "Pictures/MyReceipts/"
        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionargs = arrayOf(path)


        val allImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        Log.i("URI", allImageUri.toString())

        val projection = arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME)
        val cursor = this@PhotosActivity.contentResolver.query(collection,projection,selection,selectionargs, null)

        try {
            cursor!!.moveToFirst()
            do {
                val image = Image()
                image.imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                image.imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val file = File(image.imagePath)
                if(file.exists()){
                    Log.i("URI", image.imageName.toString())
                    images.add(image)
                }


            }while (cursor.moveToNext())
            cursor.close()

        }catch (e: Exception){
            e.printStackTrace()
        }

        return images
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK ){
            Toast.makeText(this, "Receipt Captured", Toast.LENGTH_SHORT ).show()
            loadPictures()

        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun requestPermissions(){
        if(ContextCompat.checkSelfPermission(this@PhotosActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this@PhotosActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),101)
        }

        if(ContextCompat.checkSelfPermission(this@PhotosActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this@PhotosActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),102)
        }

    }


    private fun createImage(): Uri? {
        val uri: Uri?
        val contentResolver = contentResolver
        val imgName: String = System.currentTimeMillis().toString()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }
        else{
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imgName+".jpg")
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "MyReceipts/")
        val finalUri = contentResolver.insert(uri, contentValues)
        if (finalUri != null) {
            imageUri = finalUri
        }
        return finalUri
    }

    private fun loadPictures(){
        picturesList?.clear()
        progressbar?.visibility = View.VISIBLE
        picturesList = getAllImages()
        pictureRecyclerView?.adapter = ImageAdapter(this, picturesList!!)
        progressbar?.visibility = View.GONE


    }
}