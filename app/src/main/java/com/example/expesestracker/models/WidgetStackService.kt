package com.example.expesestracker.models

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.example.expesestracker.R

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
        if (i == 1){
            totalWeek = sqlUtil.getTotalForWeek(1)
            totalMonth = sqlUtil.getLatestItem(1)
            textMonth = "This Month total income"
            textWeek = "This Week total income"
        }
        else{
            totalWeek = sqlUtil.getTotalForWeek(0)
            totalMonth = sqlUtil.getLatestItem(0)
            textMonth = "This Month total expenses"
            textWeek = "This Week total expenses"
        }

        val view = RemoteViews(context.packageName, R.layout.widget_stack_item )
        val num = i - 1
        Log.i("VIEW" , i.toString())
        view.setTextViewText(
            R.id.txtTotalWeekWidgetValue, "$ " + totalWeek.toString()
        )
        view.setTextViewText(
            R.id.txtTotalMonthWidgetValue, "$ " + totalMonth.toString()
        )

        view.setTextViewText(
            R.id.txtTotalMonthWidgetStr, textMonth
        )
        view.setTextViewText(
            R.id.txtTotalWeekWidgetStr, textWeek
        )

        return view
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