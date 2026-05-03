package com.staffguard.mobile.utils

import android.content.Context

object TokenManager {

    private const val PREF_NAME = "staffguard_prefs"
    private const val KEY_TOKEN = "jwt_token"

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun getBearerToken(context: Context): String {
        return "Bearer ${getToken(context)}"
    }

    fun isLoggedIn(context: Context): Boolean {
        return getToken(context) != null
    }
}