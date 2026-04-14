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

    // Теперь сохраняем и токен, и роль одним махом
    fun saveAuthData(token: String, role: String) {
        prefs.edit()
            .putString("jwt_token", token)
            .putString("user_role", role)
            .apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    // Метод для получения роли
    fun getRole(): String? = prefs.getString("user_role", null)

    // При выходе удаляем все данные
    fun clearAuthData() {
        prefs.edit()
            .remove("jwt_token")
            .remove("user_role")
            .apply()
    }
}