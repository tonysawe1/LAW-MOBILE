package com.example.data.api

import android.content.Context
import android.content.SharedPreferences

/**
 * Centralized API configuration for the LAW backend.
 * The base URL is stored in exactly ONE location as strictly required.
 */
object ApiConfig {
  const val DEFAULT_BASE_URL = "https://error-unnamable-borrower.ngrok-free.dev/api/v1/"

  private const val PREFS_NAME = "law_api_config"
  private const val KEY_BASE_URL = "custom_base_url"

  private var customBaseUrl: String? = null

  fun init(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    customBaseUrl = prefs.getString(KEY_BASE_URL, null)
  }

  fun getBaseUrl(): String {
    val url = customBaseUrl ?: DEFAULT_BASE_URL
    return if (url.endsWith("/")) url else "$url/"
  }

  fun setCustomBaseUrl(context: Context, url: String?) {
    customBaseUrl = url
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    if (url.isNullOrBlank()) {
      prefs.edit().remove(KEY_BASE_URL).apply()
    } else {
      prefs.edit().putString(KEY_BASE_URL, url).apply()
    }
  }
}
