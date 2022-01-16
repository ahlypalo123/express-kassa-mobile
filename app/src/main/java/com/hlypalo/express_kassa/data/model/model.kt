package com.hlypalo.express_kassa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Anime(
    val image_url: String,
    val title: String
)

data class Result(
    val results: List<Anime>
)

@Entity
data class Product (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val price: Float,
    val photo_url: String?,
)

@Entity
data class CartProduct (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val price: Float
)

data class CartDto (
    val name: String,
    val price: Float,
    val count: Int
)