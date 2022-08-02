package com.example.expesestracker.models

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.expesestracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ortiz.touchview.TouchImageView
import java.io.File

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

        holder.imageVew.setOnClickListener{view ->
            val layoutName = R.layout.image_full_screen
            fullScreenImage(view, currentImage, layoutName, position)
        }

    }
    override fun getItemCount(): Int {
        return imagesList.size
    }

    private fun fullScreenImage(
        viewIn: View,
        item: Image,
        layoutNM: Int,
        position: Int
    ) {

        val builder = AlertDialog.Builder(viewIn.context)
        val layoutInflater: LayoutInflater = viewIn.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =  layoutInflater.inflate(layoutNM, null)
        val imageView = view.findViewById<TouchImageView>(R.id.fullScreenImage)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabCloseImage)
        val fabDel = view.findViewById<FloatingActionButton>(R.id.fabDeleteImage)



        with(builder) {
            val uri = Uri.parse(item.imagePath)
            imageView.setImageURI(uri)
            setView(view)
            setCancelable(true)
            val dialog = builder.create()
            fab.setOnClickListener{ view
                dialog.cancel()
            }

            fabDel.setOnClickListener{
                val delete = File(uri.getPath())
                val anim = AnimationUtils.loadAnimation(context, R.anim.fade_animation)
                imageView.animation = anim
                if (delete.exists()) {
                    if (delete.delete()) {
                        dialog.cancel()
                        Toast.makeText(context, R.string.image_deleted, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, R.string.image_deleted_issue, Toast.LENGTH_SHORT).show()
                    }
                    dialog.cancel()
                }
                imagesList.remove(item)
                notifyItemRemoved(position)
            }
            dialog.show()
        }
    }


}