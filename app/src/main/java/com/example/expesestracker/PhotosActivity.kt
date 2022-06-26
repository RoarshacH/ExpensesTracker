package com.example.expesestracker

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity




class PhotosActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 100
    lateinit var pictureImageView : ImageView
    lateinit var imageUri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        pictureImageView = findViewById(R.id.pictureImageView)

        val btnTakePicture = findViewById<Button>(R.id.btnTakePicture);
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



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK ){
            Toast.makeText(this, "Receipt Captured", Toast.LENGTH_SHORT ).show()
//            val imageBitmap = data?.extras?.get(("data")) as Bitmap
        //            pictureImageView.setImageBitmap(imageBitmap)

        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }

    }


    fun createImage(): Uri? {
        var uri: Uri? = null
        var contentResolver = contentResolver
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
}