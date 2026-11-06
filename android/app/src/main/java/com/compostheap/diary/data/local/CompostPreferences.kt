package com.compostheap.diary.data.local

import android.content.Context
import com.compostheap.diary.domain.model.DiarySnapshot

class CompostPreferences(context: Context) {

    private val preferences = context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun load(): DiarySnapshot? {
        val raw = preferences.getString(KEY_DIARY, null) ?: return null
        return DiaryCodec.decode(raw)
    }

    fun save(snapshot: DiarySnapshot) {
        preferences.edit().putString(KEY_DIARY, DiaryCodec.encode(snapshot)).apply()
    }

    fun saveExport(raw: String) {
        preferences.edit().putString(KEY_EXPORT, raw).apply()
    }

    fun lastExport(): String = preferences.getString(KEY_EXPORT, "").orEmpty()

    private companion object {
        const val STORE = "compost_diary_store"
        const val KEY_DIARY = "diary"
        const val KEY_EXPORT = "export_snapshot"
    }
}
