package com.example.bookworm.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bookworm.model.AppAppearance
import com.example.bookworm.model.AppColorChoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appearanceDataStore by preferencesDataStore(name = "appearance")

class AppearancePreferences(context: Context) {
    private val dataStore = context.applicationContext.appearanceDataStore

    val appearance: Flow<AppAppearance> = dataStore.data.map { preferences ->
        AppAppearance(
            backgroundColor = AppColorChoice.fromStorageKey(preferences[BACKGROUND_COLOR]),
            fontColor = AppColorChoice.fromStorageKey(preferences[FONT_COLOR]),
        )
    }

    suspend fun setAppearance(appearance: AppAppearance) {
        dataStore.edit { preferences ->
            preferences[BACKGROUND_COLOR] = appearance.backgroundColor.storageKey
            preferences[FONT_COLOR] = appearance.fontColor.storageKey
        }
    }

    companion object {
        private val BACKGROUND_COLOR = stringPreferencesKey("background_color")
        private val FONT_COLOR = stringPreferencesKey("font_color")
    }
}
