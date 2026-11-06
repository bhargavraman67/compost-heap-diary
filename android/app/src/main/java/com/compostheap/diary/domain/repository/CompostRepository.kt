package com.compostheap.diary.domain.repository

import com.compostheap.diary.domain.model.Batch
import com.compostheap.diary.domain.model.Bin
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.DiarySnapshot
import com.compostheap.diary.domain.model.Entry
import com.compostheap.diary.domain.model.GardenNote
import com.compostheap.diary.domain.model.TemperatureReading
import kotlinx.coroutines.flow.StateFlow

interface CompostRepository {

    val bins: StateFlow<List<Bin>>

    val entries: StateFlow<List<Entry>>

    val readings: StateFlow<List<TemperatureReading>>

    val materials: StateFlow<List<CompostMaterial>>

    val batches: StateFlow<List<Batch>>

    val notes: StateFlow<List<GardenNote>>

    fun addBin(name: String): Bin

    fun setResting(binId: String, resting: Boolean)

    fun addEntry(entry: Entry)

    fun addReading(reading: TemperatureReading)

    fun addMaterial(material: CompostMaterial)

    fun deleteMaterial(materialId: String)

    fun addNote(note: GardenNote)

    fun closeBatch(batch: Batch)

    fun snapshot(): DiarySnapshot

    fun replaceAll(snapshot: DiarySnapshot)

    fun exportSnapshot(): String

    fun importSnapshot(raw: String): Boolean

    fun lastExport(): String
}
