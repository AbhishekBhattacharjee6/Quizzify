package com.example.quizzify.SharedPreference

import android.content.Context
import android.content.SharedPreferences

class ImagePreference(context:Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Image", Context.MODE_PRIVATE)
    fun saveUserImgURI(uri: String) {
        sharedPreferences.edit().putString("user_img", uri).apply()
    }
    fun getUserImgURI(): String {
        return sharedPreferences.getString("user_img", "Empty") ?: "Empty"
    }
    fun clearUserImgData() {
        sharedPreferences.edit().clear().apply()
    }
}