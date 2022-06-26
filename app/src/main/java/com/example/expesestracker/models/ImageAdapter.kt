package com.example.expesestracker.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.expesestracker.R

class ImageAdapter(private var context: Context, private var imagesList: ArrayList<Image>):
    RecyclerView.Adapter<ImageAdapter.ViewHolder>()  {

    var imageVew: ImageView? = null

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageVew = itemView.findViewById<ImageView>(R.id.row_image)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_image_item, parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImage = imagesList[position]

        if (currentImage.imagePath.isNullOrEmpty()){

        }
        else{
            Glide.with(context)
                .load(currentImage.imagePath)
                .apply(RequestOptions().centerCrop())
                .into(holder.imageVew!!)
        }




        holder.imageVew.setOnClickListener{
            Toast.makeText(context, "Image Clicked" , Toast.LENGTH_SHORT).show()
        }

    }
    override fun getItemCount(): Int {
        return imagesList.size
    }


}