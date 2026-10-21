// Lịch Việt - Phần mềm được phát triển bởi Lâm Mạnh - TTYT Tương Dương © 2026
// Xem ngày theo quan niệm dân gian, chỉ mang tính tham khảo. Giữ nguyên cách tính của bản Windows/JS.
package vn.tuongduong.lichviet

data class DayInfo(
    val jdn: Int,
    val lunar: LunarDate,
    val dayCanChi: String,
    val monthCanChi: String,
    val yearCanChi: String,
    val truc: String,
    val star: String,
    val hoangDao: Boolean,
    val level: Int,        // 2 tốt .. -2 xấu
    val rating: String,
    val taboo: Boolean,
    val holiday: String?,
    val holidayOfficial: Boolean,
)

object Almanac {
    private val CAN = arrayOf("Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý")
    private val CHI = arrayOf("Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi")
    private val STARS = arrayOf("Thanh Long", "Minh Đường", "Thiên Hình", "Chu Tước", "Kim Quỹ", "Bảo Quang (Thiên Đức)", "Bạch Hổ", "Ngọc Đường", "Thiên Lao", "Huyền Vũ", "Tư Mệnh", "Câu Trần")
    private val STAR_GOOD = intArrayOf(1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0)
    private val TRUC = arrayOf("Kiến", "Trừ", "Mãn", "Bình", "Định", "Chấp", "Phá", "Nguy", "Thành", "Thu", "Khai", "Bế")
    private val TRUC_SCORE = intArrayOf(1, 0, 1, 0, 1, -1, -1, -1, 1, 0, 1, -1)
    private val DUONG_CONG = arrayOf(
        intArrayOf(13), intArrayOf(11), intArrayOf(9), intArrayOf(7), intArrayOf(5), intArrayOf(3),
        intArrayOf(1, 29), intArrayOf(27), intArrayOf(25), intArrayOf(23), intArrayOf(21), intArrayOf(19),
    )
    private val WEEKDAYS = arrayOf("Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy")

    // (ngày, tháng, tên đầy đủ, nghỉ lễ chính thức)
    private val SOLAR_HOL = listOf(
        Hol(1, 1, "Tết Dương lịch", true), Hol(3, 2, "Ngày thành lập Đảng Cộng sản Việt Nam", false),
        Hol(27, 2, "Ngày Thầy thuốc Việt Nam", false), Hol(8, 3, "Quốc tế Phụ nữ", false),
        Hol(7, 4, "Ngày Sức khỏe Thế giới", false), Hol(30, 4, "Giải phóng miền Nam, thống nhất đất nước", true),
        Hol(1, 5, "Quốc tế Lao động", true), Hol(1, 6, "Quốc tế Thiếu nhi", false),
        Hol(28, 6, "Ngày Gia đình Việt Nam", false), Hol(27, 7, "Ngày Thương binh Liệt sĩ", false),
        Hol(2, 9, "Quốc khánh", true), Hol(10, 10, "Giải phóng Thủ đô", false),
        Hol(20, 10, "Ngày Phụ nữ Việt Nam", false), Hol(20, 11, "Ngày Nhà giáo Việt Nam", false),
        Hol(22, 12, "Ngày thành lập Quân đội nhân dân", false), Hol(24, 12, "Giáng sinh", false),
    )
    private val LUNAR_HOL = listOf(
        Hol(1, 1, "Mùng 1 Tết Nguyên đán", true), Hol(2, 1, "Mùng 2 Tết Nguyên đán", true), Hol(3, 1, "Mùng 3 Tết Nguyên đán", true),
        Hol(15, 1, "Rằm tháng Giêng (Nguyên tiêu)", false), Hol(10, 3, "Giỗ Tổ Hùng Vương", true),
        Hol(15, 4, "Lễ Phật Đản", false), Hol(5, 5, "Tết Đoan Ngọ", false), Hol(15, 7, "Lễ Vu Lan", false),
        Hol(15, 8, "Tết Trung thu", false), Hol(23, 12, "Ông Táo chầu trời", false),
    )

    private class Hol(val d: Int, val m: Int, val name: String, val official: Boolean)

    fun weekdayName(jdn: Int): String = WEEKDAYS[(jdn + 1) % 7]
    fun yearCanChi(y: Int): String = CAN[Math.floorMod(y + 6, 10)] + " " + CHI[Math.floorMod(y + 8, 12)]

    fun get(y: Int, m: Int, d: Int): DayInfo {
        val lunar = Lunar.solarToLunar(d, m, y)
        val jdn = Lunar.jdFromDate(d, m, y)
        val can = (jdn + 9) % 10
        val chi = (jdn + 1) % 12
        val monthChi = (lunar.month + 1) % 12
        val monthCan = (lunar.year * 12 + lunar.month + 3) % 10
        val trucIdx = Math.floorMod(chi - monthChi, 12)
        val starIdx = Math.floorMod(chi - ((lunar.month - 1) % 6) * 2, 12)
        val hoangDao = STAR_GOOD[starIdx] == 1

        var score = (if (hoangDao) 2 else -2) + TRUC_SCORE[trucIdx]
        var taboo = false
        val ld = lunar.day
        if (ld in intArrayOf(3, 7, 13, 18, 22, 27)) { taboo = true; score -= 1 }
        if (ld in intArrayOf(5, 14, 23)) { taboo = true; score -= 1 }
        if (DUONG_CONG[lunar.month - 1].contains(ld)) { taboo = true; score -= 2 }
        val level: Int
        val rating: String
        when {
            score >= 3 -> { level = 2; rating = "Ngày tốt" }
            score >= 1 -> { level = 1; rating = "Khá tốt" }
            score == 0 -> { level = 0; rating = "Trung bình" }
            score >= -2 -> { level = -1; rating = "Không tốt" }
            else -> { level = -2; rating = "Ngày xấu" }
        }

        val off = ArrayList<String>()
        val mem = ArrayList<String>()
        for (h in SOLAR_HOL) if (h.d == d && h.m == m) (if (h.official) off else mem).add(h.name)
        if (!lunar.leap) for (h in LUNAR_HOL) if (h.d == lunar.day && h.m == lunar.month) (if (h.official) off else mem).add(h.name)
        if (lunar.day >= 29 && lunar.month == 12 && !lunar.leap) {
            val (nd, nm, ny) = Lunar.addDays(d, m, y, 1)
            val n = Lunar.solarToLunar(nd, nm, ny)
            if (n.day == 1 && n.month == 1 && !n.leap) off.add("Giao thừa (ngày cuối năm âm lịch)")
        }
        val all = off + mem

        return DayInfo(
            jdn = jdn, lunar = lunar,
            dayCanChi = CAN[can] + " " + CHI[chi], monthCanChi = CAN[monthCan] + " " + CHI[monthChi],
            yearCanChi = yearCanChi(lunar.year), truc = TRUC[trucIdx], star = STARS[starIdx],
            hoangDao = hoangDao, level = level, rating = rating, taboo = taboo,
            holiday = if (all.isEmpty()) null else all.joinToString(" · "), holidayOfficial = off.isNotEmpty(),
        )
    }
}
