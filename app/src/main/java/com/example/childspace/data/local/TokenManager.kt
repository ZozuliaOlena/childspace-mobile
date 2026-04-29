package com.example.childspace.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthData(token: String, role: String, userId: String) {
        prefs.edit()
            .putString("jwt_token", token)
            .putString("user_role", role)
            .putString("user_id", userId)
            .commit()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun getRole(): String? = prefs.getString("user_role", null)

    fun getUserId(): String = prefs.getString("user_id", "") ?: ""

    fun clearAuthData() {
        prefs.edit()
            .remove("jwt_token")
            .remove("user_role")
            .remove("user_id")
            .apply()
    }
}