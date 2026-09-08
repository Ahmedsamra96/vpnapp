package com.example.data

import com.example.model.VpnServer

object DefaultServers {

    fun getDefaultServers(): List<VpnServer> = listOf(
        VpnServer(
            id = "optimal_server",
            country = "Fastest Server",
            countryAr = "أسرع خادم تلقائي",
            countryCode = "AUTO",
            city = "Auto Selection",
            ip = "104.28.19.44",
            pingMs = 18,
            speedMbps = 185.0,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 1240,
            score = 999999,
            flagEmoji = "⚡",
            isOptimal = true
        ),
        VpnServer(
            id = "us_new_york",
            country = "United States",
            countryAr = "الولايات المتحدة",
            countryCode = "US",
            city = "New York",
            ip = "198.51.100.22",
            pingMs = 24,
            speedMbps = 120.4,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 384,
            score = 852000,
            flagEmoji = "🇺🇸"
        ),
        VpnServer(
            id = "de_frankfurt",
            country = "Germany",
            countryAr = "ألمانيا",
            countryCode = "DE",
            city = "Frankfurt",
            ip = "203.0.113.14",
            pingMs = 32,
            speedMbps = 110.8,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 219,
            score = 794000,
            flagEmoji = "🇩🇪"
        ),
        VpnServer(
            id = "jp_tokyo",
            country = "Japan",
            countryAr = "اليابان",
            countryCode = "JP",
            city = "Tokyo",
            ip = "133.242.18.91",
            pingMs = 45,
            speedMbps = 145.2,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 512,
            score = 920000,
            flagEmoji = "🇯🇵"
        ),
        VpnServer(
            id = "gb_london",
            country = "United Kingdom",
            countryAr = "المملكة المتحدة",
            countryCode = "GB",
            city = "London",
            ip = "185.190.140.5",
            pingMs = 29,
            speedMbps = 98.5,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 187,
            score = 750000,
            flagEmoji = "🇬🇧"
        ),
        VpnServer(
            id = "sg_singapore",
            country = "Singapore",
            countryAr = "سنغافورة",
            countryCode = "SG",
            city = "Singapore",
            ip = "128.199.200.8",
            pingMs = 52,
            speedMbps = 130.0,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 310,
            score = 810000,
            flagEmoji = "🇸🇬"
        ),
        VpnServer(
            id = "nl_amsterdam",
            country = "Netherlands",
            countryAr = "هولندا",
            countryCode = "NL",
            city = "Amsterdam",
            ip = "194.135.12.89",
            pingMs = 28,
            speedMbps = 115.0,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 190,
            score = 760000,
            flagEmoji = "🇳🇱"
        ),
        VpnServer(
            id = "ca_toronto",
            country = "Canada",
            countryAr = "كندا",
            countryCode = "CA",
            city = "Toronto",
            ip = "142.250.180.12",
            pingMs = 38,
            speedMbps = 89.2,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 145,
            score = 690000,
            flagEmoji = "🇨🇦"
        ),
        VpnServer(
            id = "fr_paris",
            country = "France",
            countryAr = "فرنسا",
            countryCode = "FR",
            city = "Paris",
            ip = "51.15.80.10",
            pingMs = 35,
            speedMbps = 94.0,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 162,
            score = 710000,
            flagEmoji = "🇫🇷"
        ),
        VpnServer(
            id = "kr_seoul",
            country = "South Korea",
            countryAr = "كوريا الجنوبية",
            countryCode = "KR",
            city = "Seoul",
            ip = "211.234.120.4",
            pingMs = 49,
            speedMbps = 150.0,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 420,
            score = 880000,
            flagEmoji = "🇰🇷"
        ),
        VpnServer(
            id = "au_sydney",
            country = "Australia",
            countryAr = "أستراليا",
            countryCode = "AU",
            city = "Sydney",
            ip = "139.130.4.5",
            pingMs = 88,
            speedMbps = 75.0,
            protocol = "UDP",
            port = 1194,
            sessionsCount = 98,
            score = 540000,
            flagEmoji = "🇦🇺"
        )
    )

    fun getCountryFlag(countryCode: String): String {
        if (countryCode.equals("AUTO", ignoreCase = true)) return "⚡"
        if (countryCode.length != 2) return "🌐"
        val firstChar = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
        val secondChar = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6
        return if (firstChar in 0x1F1E6..0x1F1FF && secondChar in 0x1F1E6..0x1F1FF) {
            String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        } else {
            "🌐"
        }
    }

    fun getArabicCountryName(countryCode: String, englishName: String): String {
        return when (countryCode.uppercase()) {
            "US" -> "الولايات المتحدة"
            "DE" -> "ألمانيا"
            "JP" -> "اليابان"
            "GB", "UK" -> "المملكة المتحدة"
            "SG" -> "سنغافورة"
            "NL" -> "هولندا"
            "CA" -> "كندا"
            "FR" -> "فرنسا"
            "KR" -> "كوريا الجنوبية"
            "AU" -> "أستراليا"
            "CH" -> "سويسرا"
            "SE" -> "السويد"
            "NO" -> "النرويج"
            "ES" -> "إسبانيا"
            "IT" -> "إيطاليا"
            "BR" -> "البرازيل"
            "IN" -> "الهند"
            "TR" -> "تركيا"
            "AE" -> "الإمارات"
            "SA" -> "السعودية"
            "EG" -> "مصر"
            "AUTO" -> "أسرع خادم تلقائي"
            else -> englishName
        }
    }
}
