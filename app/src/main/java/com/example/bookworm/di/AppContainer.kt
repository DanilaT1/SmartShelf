package com.example.bookworm.di

import android.content.Context
import com.example.bookworm.data.local.BookWormDatabase
import com.example.bookworm.data.preferences.ActiveBookPreferences
import com.example.bookworm.data.preferences.AppearancePreferences
import com.example.bookworm.data.repository.BookWormRepository

class AppContainer(context: Context) {
    private val database = BookWormDatabase.getInstance(context)
    private val activeBookPreferences = ActiveBookPreferences(context)
    private val appearancePreferences = AppearancePreferences(context)
    val repository = BookWormRepository(database.dao(), activeBookPreferences, appearancePreferences)
}
