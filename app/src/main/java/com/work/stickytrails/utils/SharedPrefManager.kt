package com.work.stickytrails.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {

    private const val PREF_NAME = "user_preferences"
    private const val KEY_EMAIL = "email"
    private const val KEY_USERNAME = "username"
    private const val KEY_TOKEN = "token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserData(context: Context, email: String, username: String, token: String) {
        val sharedPreferences = getSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun getUserData(context: Context): UserData? {
        val sharedPreferences = getSharedPreferences(context)
        val email = sharedPreferences.getString(KEY_EMAIL, null)
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val token = sharedPreferences.getString(KEY_TOKEN, null)

        return if (email != null && username != null && token != null) {
            UserData(email, username, token)
        } else {
            null
        }
    }

    data class UserData(val email: String, val username: String, val token: String)
}
