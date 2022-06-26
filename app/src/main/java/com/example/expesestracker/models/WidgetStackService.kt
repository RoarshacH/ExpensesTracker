package com.example.expesestracker.models

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.expesestracker.R
import java.lang.reflect.Type

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
        var sqlUtil = SQLUtilities(context)
        var totalWeek = 0F
        var totalMonth = 0F
        var textMonth = ""
        var textWeek = ""
        var textTitle = ""
        if (i == 1){
            totalWeek = sqlUtil.getTotalForWeek(1)
            totalMonth = sqlUtil.getLatestItem(1)
            textMonth = "This Month total income"
            textWeek = "This Week total income"
            textTitle = "Incomes"
        }
        else{
            totalWeek = sqlUtil.getTotalForWeek(0)
            totalMonth = sqlUtil.getLatestItem(0)
            textMonth = "This Month total expenses"
            textWeek = "This Week total expenses"
            textTitle = "Expenses"
        }

        val view = RemoteViews(context.packageName, R.layout.widget_stack_item )
        val num = i - 1
        Log.i("VIEW" , i.toString())
        view.setImageViewBitmap(R.id.widgetTitleText,BuildUpdate(textTitle, 150F, context))
        view.setImageViewBitmap(R.id.widgetBitmap1, BuildUpdate("$textWeek $ $totalWeek", 100F, context))
        view.setImageViewBitmap(R.id.widgetBitmap2, BuildUpdate("$textMonth $ $totalMonth", 100F, context))

//        view.setTextViewText(
//            R.id.txtTotalWeekWidgetValue, "$ " + totalWeek.toString()
//        )
//        view.setTextViewText(
//            R.id.txtTotalMonthWidgetValue, "$ " + totalMonth.toString()
//        )
//
//        view.setTextViewText(
//            R.id.txtTotalMonthWidgetStr, textMonth
//        )
//        view.setTextViewText(
//            R.id.txtTotalWeekWidgetStr, textWeek
//        )

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