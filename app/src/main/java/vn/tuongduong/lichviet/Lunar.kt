// Lịch Việt - Phần mềm được phát triển bởi Lâm Mạnh - TTYT Tương Dương © 2026
// Âm lịch Việt Nam (UTC+7), thuật toán Hồ Ngọc Đức. Không phụ thuộc Android để dễ kiểm thử.
package vn.tuongduong.lichviet

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

data class LunarDate(val day: Int, val month: Int, val year: Int, val leap: Boolean)

object Lunar {
    private const val TZ = 7.0
    private fun fl(x: Double): Int = floor(x).toInt()

    fun jdFromDate(dd: Int, mm: Int, yy: Int): Int {
        val a = (14 - mm) / 12
        val y = yy + 4800 - a
        val m = mm + 12 * a - 3
        var jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        if (jd < 2299161) jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083
        return jd
    }

    private fun newMoon(k: Int): Double {
        val T = k / 1236.85
        val T2 = T * T
        val T3 = T2 * T
        val dr = PI / 180
        var jd1 = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3
        jd1 += 0.00033 * sin((166.56 + 132.87 * T - 0.009173 * T2) * dr)
        val M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3
        val Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3
        val F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3
        var c1 = (0.1734 - 0.000393 * T) * sin(M * dr) + 0.0021 * sin(2 * dr * M)
        c1 = c1 - 0.4068 * sin(Mpr * dr) + 0.0161 * sin(dr * 2 * Mpr)
        c1 = c1 - 0.0004 * sin(dr * 3 * Mpr)
        c1 = c1 + 0.0104 * sin(dr * 2 * F) - 0.0051 * sin(dr * (M + Mpr))
        c1 = c1 - 0.0074 * sin(dr * (M - Mpr)) + 0.0004 * sin(dr * (2 * F + M))
        c1 = c1 - 0.0004 * sin(dr * (2 * F - M)) - 0.0006 * sin(dr * (2 * F + Mpr))
        c1 = c1 + 0.0010 * sin(dr * (2 * F - Mpr)) + 0.0005 * sin(dr * (2 * Mpr + M))
        val dt = if (T < -11) 0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3
        else -0.000278 + 0.000265 * T + 0.000262 * T2
        return jd1 + c1 - dt
    }

    private fun sunLongitude(jdn: Double): Double {
        val T = (jdn - 2451545.0) / 36525
        val T2 = T * T
        val dr = PI / 180
        val M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - 0.00000048 * T * T2
        val L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2
        var dl = (1.914600 - 0.004817 * T - 0.000014 * T2) * sin(dr * M)
        dl += (0.019993 - 0.000101 * T) * sin(dr * 2 * M) + 0.000290 * sin(dr * 3 * M)
        var l = (L0 + dl) * dr
        l -= PI * 2 * floor(l / (PI * 2))
        return l
    }

    private fun getSun(dn: Int): Int = fl(sunLongitude(dn - 0.5 - TZ / 24) / PI * 6)
    private fun nmDay(k: Int): Int = fl(newMoon(k) + 0.5 + TZ / 24)

    private fun lunarMonth11(yy: Int): Int {
        val off = jdFromDate(31, 12, yy) - 2415021.076998695
        val k = fl(off / 29.530588853)
        var nm = nmDay(k)
        if (getSun(nm) >= 9) nm = nmDay(k - 1)
        return nm
    }

    private fun leapMonthOffset(a11: Int): Int {
        val k = fl((a11 - 2415021.076998695) / 29.530588853 + 0.5)
        var i = 1
        var arc = getSun(nmDay(k + i))
        var last: Int
        do {
            last = arc
            i++
            arc = getSun(nmDay(k + i))
        } while (arc != last && i < 14)
        return i - 1
    }

    fun solarToLunar(dd: Int, mm: Int, yy: Int): LunarDate {
        val dayNumber = jdFromDate(dd, mm, yy)
        var k = fl((dayNumber - 2415021.076998695) / 29.530588853)
        while (nmDay(k + 1) <= dayNumber) k++
        while (nmDay(k) > dayNumber) k--
        val monthStart = nmDay(k)
        var a11 = lunarMonth11(yy)
        var b11 = a11
        var lunarYear: Int
        if (a11 >= monthStart) {
            lunarYear = yy
            a11 = lunarMonth11(yy - 1)
        } else {
            lunarYear = yy + 1
            b11 = lunarMonth11(yy + 1)
        }
        val lunarDay = dayNumber - monthStart + 1
        val diff = fl((monthStart - a11) / 29.0)
        var leap = false
        var lunarMonth = diff + 11
        if (b11 - a11 > 365) {
            val leapOff = leapMonthOffset(a11)
            if (diff >= leapOff) {
                lunarMonth = diff + 10
                if (diff == leapOff) leap = true
            }
        }
        if (lunarMonth > 12) lunarMonth -= 12
        if (lunarMonth >= 11 && diff < 4) lunarYear -= 1
        return LunarDate(lunarDay, lunarMonth, lunarYear, leap)
    }

    /** Cộng n ngày vào ngày dương; trả về (ngày, tháng, năm). */
    fun addDays(dd: Int, mm: Int, yy: Int, n: Int): Triple<Int, Int, Int> {
        val jd = jdFromDate(dd, mm, yy) + n
        val a = jd + 32044
        val b = (4 * a + 3) / 146097
        val c = a - (b * 146097) / 4
        val d = (4 * c + 3) / 1461
        val e = c - (1461 * d) / 4
        val m = (5 * e + 2) / 153
        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = b * 100 + d - 4800 + m / 10
        return Triple(day, month, year)
    }
}
