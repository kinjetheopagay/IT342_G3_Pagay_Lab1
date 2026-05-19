package com.staffguard.mobile.utils

import android.content.Context

object TokenManager {

    private const val PREFS_NAME = "StaffGuardPrefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_ROLE = "user_role"

    fun saveToken(context: Context, token: String) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getToken(context: Context): String? {

        return context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).getString(KEY_TOKEN, null)
    }

    fun getBearerToken(context: Context): String {

        return "Bearer ${getToken(context)}"
    }

    fun saveRole(context: Context, role: String) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun getRole(context: Context): String? {

        return context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).getString(KEY_ROLE, null)
    }

    fun clearAll(context: Context) {

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .clear()
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {

        return getToken(context) != null
    }
}