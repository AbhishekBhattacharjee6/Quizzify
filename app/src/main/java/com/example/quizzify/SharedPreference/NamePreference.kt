package com.example.quizzify.SharedPreference

import android.content.Context
import android.content.SharedPreferences

class NamePreference(context:Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("Name", Context.MODE_PRIVATE)
    fun saveUserName(name: String) {
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String {
        return sharedPreferences.getString("user_name", "No Name") ?: "No Name"
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }
}