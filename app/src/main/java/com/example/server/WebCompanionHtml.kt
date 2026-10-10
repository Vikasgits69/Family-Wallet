package com.example.server

import android.content.Context

object WebCompanionHtml {

    private var cachedHtml: String? = null

    fun getHtml(context: Context): String {
        cachedHtml?.let { return it }
        return try {
            context.assets.open("companion.html").bufferedReader().use { it.readText() }.also {
                cachedHtml = it
            }
        } catch (e: Exception) {
            "<!DOCTYPE html><html><body><h1>Error loading Web Companion</h1><p>${e.message}</p></body></html>"
        }
    }
}
