// Lịch Việt - Phần mềm được phát triển bởi Lâm Mạnh - TTYT Tương Dương © 2026
package vn.tuongduong.lichviet

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import java.util.Calendar

/** Widget màn hình chính: ngày dương, âm lịch, can chi, ngày tốt/xấu hôm nay. Chạm vào để mở ứng dụng. */
class LichVietWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) appWidgetManager.updateAppWidget(id, build(context))
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        // Sang ngày mới, đổi giờ hoặc đổi múi giờ: vẽ lại ngay.
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> refreshAll(context)
        }
    }

    companion object {
        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, LichVietWidget::class.java))
            if (ids.isEmpty()) return
            val views = build(context)
            for (id in ids) mgr.updateAppWidget(id, views)
        }

        private fun build(context: Context): RemoteViews {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH) + 1
            val d = cal.get(Calendar.DAY_OF_MONTH)
            val info = Almanac.get(y, m, d)
            val l = info.lunar

            val v = RemoteViews(context.packageName, R.layout.widget_lichviet)
            v.setTextViewText(R.id.w_day, d.toString())
            v.setTextViewText(R.id.w_weekday, Almanac.weekdayName(info.jdn))
            v.setTextViewText(R.id.w_month, "Tháng $m / $y")
            v.setTextViewText(R.id.w_lunar, "${l.day} tháng ${l.month}${if (l.leap) " nhuận" else ""} âm lịch")
            v.setTextViewText(R.id.w_canchi, "Ngày ${info.dayCanChi} · năm ${info.yearCanChi}")
            v.setTextViewText(R.id.w_rating, (if (info.hoangDao) "Hoàng đạo" else "Hắc đạo") + " · " + info.rating)
            val color = when (info.level) {
                2, 1 -> R.color.w_good
                0 -> R.color.w_sub
                -1 -> R.color.w_warn
                else -> R.color.w_bad
            }
            v.setTextColor(R.id.w_rating, ContextCompat.getColor(context, color))

            val hol = info.holiday
            if (hol != null) {
                v.setTextViewText(R.id.w_holiday, hol)
                v.setTextColor(R.id.w_holiday, ContextCompat.getColor(context, if (info.holidayOfficial) R.color.w_bad else R.color.w_gold))
                v.setViewVisibility(R.id.w_holiday, View.VISIBLE)
            } else {
                v.setViewVisibility(R.id.w_holiday, View.GONE)
            }

            val open = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            v.setOnClickPendingIntent(R.id.widget_root, open)
            return v
        }
    }
}
