package com.example.bookworm.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.activeBookDataStore by preferencesDataStore(name = "active_book")

class ActiveBookPreferences(context: Context) {
    private val dataStore = context.applicationContext.activeBookDataStore

    val activeBookId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[ACTIVE_BOOK_ID]?.takeIf { it > 0L }
    }

    suspend fun setActiveBookId(bookId: Long?) {
        dataStore.edit { preferences ->
            if (bookId == null) preferences.remove(ACTIVE_BOOK_ID) else preferences[ACTIVE_BOOK_ID] = bookId
        }
    }

    companion object {
        private val ACTIVE_BOOK_ID = longPreferencesKey("active_book_id")
    }
}
