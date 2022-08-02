package com.example.expesestracker.models

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.expesestracker.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class WidgetStackService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {

//        return object of class StackViewFactory
        return DemoWidgetStackViewFactory(this.applicationContext)
    }
}
internal class DemoWidgetStackViewFactory(var context: Context) :
    RemoteViewsFactory {
    override fun onCreate() {}
    override fun onDataSetChanged() {}
    override fun onDestroy() {}
    override fun getCount(): Int {
        return numberImages
    }

    override fun getViewAt(i: Int): RemoteViews {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/y H:m:ss", Locale.ENGLISH)
        var sqlUtil = SQLUtilities(context)
        var totalWeek = 0F
        var latestTextDetails = ""
        var latestTextTime = ""
        var latestTextAmount = ""
        var textWeek = ""
        var textTitle = ""
        var latestItemDetails: ExpenseItem? = null
        if (i == 1){
            totalWeek = sqlUtil.getTotalForWeek(1)
            latestItemDetails = sqlUtil.getLatestItemAll(1)
            latestTextDetails = "This Month total income"
            textWeek = "This Week total income"
            textTitle = "Incomes"
        }
        else{
            totalWeek = sqlUtil.getTotalForWeek(0)
            latestItemDetails = sqlUtil.getLatestItemAll(0)
            latestTextDetails = "This Month total expenses"
            textWeek = "This Weeks total expenses are"
            textTitle = "Expenses"
        }
        if (latestItemDetails != null){
            latestTextDetails  = "Latest Item is " + latestItemDetails.TYPE

            val originalStartDate = LocalDate.parse(latestItemDetails.DATE_TIME, dateFormatter)
            latestTextTime = "On " + latestItemDetails.DATE_TIME
            latestTextAmount = "For $ " + latestItemDetails.AMOUNT.toString()

        }

        val view = RemoteViews(context.packageName, R.layout.widget_stack_item )
        val num = i - 1
//        Log.i("VIEW" , i.toString())
        view.setImageViewBitmap(R.id.widgetTitleText,BuildUpdate(textTitle, 80F, context))
        view.setImageViewBitmap(R.id.widgetBitmap1, BuildUpdate("$textWeek" , 50F, context))
        view.setImageViewBitmap(R.id.widgetBitmap2, BuildUpdate("$latestTextDetails ", 100F, context))
        view.setImageViewBitmap(R.id.widgetBitmap3, BuildUpdate("$latestTextTime ", 100F, context))
        view.setImageViewBitmap(R.id.widgetBitmap4, BuildUpdate("$latestTextAmount ", 50F, context))
        view.setImageViewBitmap(R.id.widgetBitmap5, BuildUpdate("$ $totalWeek ", 50F, context))

        return view
    }
    fun BuildUpdate(text: String, size: Float, context: Context): Bitmap? {
        var paint = Paint()
        paint.textSize = size
        val typeFace = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        paint.isSubpixelText = true
        paint.isAntiAlias = true
        val baseline:Float = -paint.ascent()
        val width = paint.measureText(text)+0.5F
        val height = baseline+paint.descent()+0.5F
        val image = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text, 0F, baseline, paint)
        return image
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }


    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    companion object {
        var numberImages = 2
    }
}