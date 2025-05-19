package com.example.mygameshelf.domain.model

data class GameBrief(
    val igdbId: Long,
    val title: String,
    val imageId: String? = null,
    val platform: String? = null,
) {
    val coverUrl: String?
        get() = imageId?.let { "https://images.igdb.com/igdb/image/upload/t_cover_small/$it.jpg" }
}