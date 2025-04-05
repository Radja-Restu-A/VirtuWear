package com.example.virtuwear.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("onboarding_prefs")

class OnboardingManager(private val context: Context) {
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return context.dataStore.data
            .map { it[ONBOARDING_COMPLETED] ?: false }
            .first()
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit {
            it[ONBOARDING_COMPLETED] = true
        }
    }
}
