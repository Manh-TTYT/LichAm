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

/** Widget lịch tháng kiểu kính mờ: lưới ngày dương, ngày âm nhỏ bên dưới, hôm nay tô xanh. */
class LichVietMonthWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val views = build(context)
        for (id in appWidgetIds) appWidgetManager.updateAppWidget(id, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> refreshAll(context)
        }
    }

    companion object {
        private val ROWS = intArrayOf(
            R.id.m_row_0, R.id.m_row_1, R.id.m_row_2, R.id.m_row_3, R.id.m_row_4, R.id.m_row_5
        )
        private val CELL = intArrayOf(
            R.id.m_cell_0, R.id.m_cell_1, R.id.m_cell_2, R.id.m_cell_3, R.id.m_cell_4, R.id.m_cell_5,
            R.id.m_cell_6, R.id.m_cell_7, R.id.m_cell_8, R.id.m_cell_9, R.id.m_cell_10, R.id.m_cell_11,
            R.id.m_cell_12, R.id.m_cell_13, R.id.m_cell_14, R.id.m_cell_15, R.id.m_cell_16, R.id.m_cell_17,
            R.id.m_cell_18, R.id.m_cell_19, R.id.m_cell_20, R.id.m_cell_21, R.id.m_cell_22, R.id.m_cell_23,
            R.id.m_cell_24, R.id.m_cell_25, R.id.m_cell_26, R.id.m_cell_27, R.id.m_cell_28, R.id.m_cell_29,
            R.id.m_cell_30, R.id.m_cell_31, R.id.m_cell_32, R.id.m_cell_33, R.id.m_cell_34, R.id.m_cell_35,
            R.id.m_cell_36, R.id.m_cell_37, R.id.m_cell_38, R.id.m_cell_39, R.id.m_cell_40, R.id.m_cell_41
        )
        private val DAY = intArrayOf(
            R.id.m_d_0, R.id.m_d_1, R.id.m_d_2, R.id.m_d_3, R.id.m_d_4, R.id.m_d_5,
            R.id.m_d_6, R.id.m_d_7, R.id.m_d_8, R.id.m_d_9, R.id.m_d_10, R.id.m_d_11,
            R.id.m_d_12, R.id.m_d_13, R.id.m_d_14, R.id.m_d_15, R.id.m_d_16, R.id.m_d_17,
            R.id.m_d_18, R.id.m_d_19, R.id.m_d_20, R.id.m_d_21, R.id.m_d_22, R.id.m_d_23,
            R.id.m_d_24, R.id.m_d_25, R.id.m_d_26, R.id.m_d_27, R.id.m_d_28, R.id.m_d_29,
            R.id.m_d_30, R.id.m_d_31, R.id.m_d_32, R.id.m_d_33, R.id.m_d_34, R.id.m_d_35,
            R.id.m_d_36, R.id.m_d_37, R.id.m_d_38, R.id.m_d_39, R.id.m_d_40, R.id.m_d_41
        )
        private val LUNAR = intArrayOf(
            R.id.m_l_0, R.id.m_l_1, R.id.m_l_2, R.id.m_l_3, R.id.m_l_4, R.id.m_l_5,
            R.id.m_l_6, R.id.m_l_7, R.id.m_l_8, R.id.m_l_9, R.id.m_l_10, R.id.m_l_11,
            R.id.m_l_12, R.id.m_l_13, R.id.m_l_14, R.id.m_l_15, R.id.m_l_16, R.id.m_l_17,
            R.id.m_l_18, R.id.m_l_19, R.id.m_l_20, R.id.m_l_21, R.id.m_l_22, R.id.m_l_23,
            R.id.m_l_24, R.id.m_l_25, R.id.m_l_26, R.id.m_l_27, R.id.m_l_28, R.id.m_l_29,
            R.id.m_l_30, R.id.m_l_31, R.id.m_l_32, R.id.m_l_33, R.id.m_l_34, R.id.m_l_35,
            R.id.m_l_36, R.id.m_l_37, R.id.m_l_38, R.id.m_l_39, R.id.m_l_40, R.id.m_l_41
        )

        fun refreshAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, LichVietMonthWidget::class.java))
            if (ids.isEmpty()) return
            val views = build(context)
            for (id in ids) mgr.updateAppWidget(id, views)
        }

        private fun color(context: Context, res: Int) = ContextCompat.getColor(context, res)

        private fun build(context: Context): RemoteViews {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH) + 1
            val today = cal.get(Calendar.DAY_OF_MONTH)

            val first = Lunar.jdFromDate(1, m, y)
            val nextFirst = if (m == 12) Lunar.jdFromDate(1, 1, y + 1) else Lunar.jdFromDate(1, m + 1, y)
            val days = nextFirst - first
            val lead = first % 7                       // 0 = Thứ hai
            val rows = (lead + days + 6) / 7

            val v = RemoteViews(context.packageName, R.layout.widget_month)
            v.setTextViewText(R.id.m_year, y.toString())
            v.setTextViewText(R.id.m_title, "Tháng $m")
            v.setTextViewText(R.id.m_sub, "Năm " + Almanac.yearCanChi(Lunar.solarToLunar(15, m, y).year))
            for (r in 0 until 6) v.setViewVisibility(ROWS[r], if (r < rows) View.VISIBLE else View.GONE)

            val cText = color(context, R.color.g_text)
            val cDim = color(context, R.color.g_dim)
            val cWeekend = color(context, R.color.g_weekend)
            val cHoliday = color(context, R.color.g_holiday)
            val cGold = color(context, R.color.g_gold)
            val cTodayText = color(context, R.color.g_today_text)

            for (i in 0 until 42) {
                val day = i - lead + 1
                if (day < 1 || day > days) {
                    v.setTextViewText(DAY[i], "")
                    v.setTextViewText(LUNAR[i], "")
                    v.setInt(CELL[i], "setBackgroundResource", 0)
                    continue
                }
                val info = Almanac.get(y, m, day)
                val l = info.lunar
                v.setTextViewText(DAY[i], day.toString())
                v.setTextViewText(LUNAR[i], if (l.day == 1) "${l.day}/${l.month}" else l.day.toString())
                if (day == today) {
                    v.setInt(CELL[i], "setBackgroundResource", R.drawable.widget_today_bg)
                    v.setTextColor(DAY[i], cTodayText)
                    v.setTextColor(LUNAR[i], cTodayText)
                } else {
                    v.setInt(CELL[i], "setBackgroundResource", 0)
                    val col = i % 7
                    v.setTextColor(DAY[i], when {
                        info.holidayOfficial -> cHoliday
                        col >= 5 -> cWeekend
                        else -> cText
                    })
                    v.setTextColor(LUNAR[i], if (l.day == 1 || l.day == 15) cGold else cDim)
                }
            }

            val open = PendingIntent.getActivity(
                context, 1, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            v.setOnClickPendingIntent(R.id.m_root, open)
            return v
        }
    }
}
