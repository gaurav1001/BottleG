package com.example.bottleg

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val img:String?,
    val price: String?,
    val qty:String?,
    val title:String?
):Parcelable
