package com.example.mygameshelf.core

object Utils {
    fun coverBigUrl(imageId: String?): String? =
        imageId?.let { "https://images.igdb.com/igdb/image/upload/t_cover_big/$it.jpg" }

    fun coverSmallUrl(imageId: String?): String? =
        imageId?.let { "https://images.igdb.com/igdb/image/upload/t_cover_small/$it.jpg" }

    fun screenshotBigUrl(imageId: String?): String? =
        imageId?.let { "https://images.igdb.com/igdb/image/upload/t_screenshot_big/$it.jpg" }
}