package com.compostheap.diary.data.repository

import com.compostheap.diary.data.local.CompostPreferences
import com.compostheap.diary.data.local.DiaryCodec
import com.compostheap.diary.data.sample.SampleData
import com.compostheap.diary.domain.model.Batch
import com.compostheap.diary.domain.model.Bin
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.DiarySnapshot
import com.compostheap.diary.domain.model.Entry
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.model.GardenNote
import com.compostheap.diary.domain.model.TemperatureReading
import com.compostheap.diary.domain.repository.CompostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CompostRepositoryImpl(
    private val preferences: CompostPreferences
) : CompostRepository {

    private val _bins = MutableStateFlow<List<Bin>>(emptyList())
    override val bins: StateFlow<List<Bin>> = _bins.asStateFlow()

    private val _entries = MutableStateFlow<List<Entry>>(emptyList())
    override val entries: StateFlow<List<Entry>> = _entries.asStateFlow()

    private val _readings = MutableStateFlow<List<TemperatureReading>>(emptyList())
    override val readings: StateFlow<List<TemperatureReading>> = _readings.asStateFlow()

    private val _materials = MutableStateFlow<List<CompostMaterial>>(emptyList())
    override val materials: StateFlow<List<CompostMaterial>> = _materials.asStateFlow()

    private val _batches = MutableStateFlow<List<Batch>>(emptyList())
    override val batches: StateFlow<List<Batch>> = _batches.asStateFlow()

    private val _notes = MutableStateFlow<List<GardenNote>>(emptyList())
    override val notes: StateFlow<List<GardenNote>> = _notes.asStateFlow()

    init {
        val stored = preferences.load()
        val initial = stored ?: SampleData.snapshot(System.currentTimeMillis())
        applySnapshot(initial)
        if (stored == null) persist()
    }

    override fun addBin(name: String): Bin {
        val bin = Bin(
            id = "bin_" + UUID.randomUUID().toString(),
            name = name,
            startedAtMs = System.currentTimeMillis(),
            lastTurnedAtMs = null,
            resting = false
        )
        _bins.value = _bins.value + bin
        persist()
        return bin
    }

    override fun setResting(binId: String, resting: Boolean) {
        _bins.value = _bins.value.map { if (it.id == binId) it.copy(resting = resting) else it }
        persist()
    }

    override fun addEntry(entry: Entry) {
        _entries.value = _entries.value + entry
        if (entry.entryType == EntryType.TURN) {
            _bins.value = _bins.value.map {
                if (it.id == entry.binId) it.copy(lastTurnedAtMs = entry.timestampMs) else it
            }
        }
        persist()
    }

    override fun addReading(reading: TemperatureReading) {
        _readings.value = _readings.value + reading
        persist()
    }

    override fun addMaterial(material: CompostMaterial) {
        _materials.value = _materials.value + material
        persist()
    }

    override fun deleteMaterial(materialId: String) {
        _materials.value = _materials.value.filterNot { it.id == materialId && it.custom }
        persist()
    }

    override fun addNote(note: GardenNote) {
        _notes.value = listOf(note) + _notes.value
        persist()
    }

    override fun closeBatch(batch: Batch) {
        _batches.value = listOf(batch) + _batches.value
        _entries.value = _entries.value.filterNot { it.binId == batch.binId }
        _readings.value = _readings.value.filterNot { it.binId == batch.binId }
        _bins.value = _bins.value.map {
            if (it.id == batch.binId) {
                it.copy(startedAtMs = batch.harvestedAtMs, lastTurnedAtMs = null, resting = false)
            } else {
                it
            }
        }
        persist()
    }

    override fun snapshot(): DiarySnapshot = DiarySnapshot(
        bins = _bins.value,
        entries = _entries.value,
        readings = _readings.value,
        materials = _materials.value,
        batches = _batches.value,
        notes = _notes.value
    )

    override fun replaceAll(snapshot: DiarySnapshot) {
        applySnapshot(snapshot)
        persist()
    }

    override fun exportSnapshot(): String {
        val raw = DiaryCodec.encode(snapshot())
        preferences.saveExport(raw)
        return raw
    }

    override fun importSnapshot(raw: String): Boolean {
        val decoded = DiaryCodec.decode(raw) ?: return false
        replaceAll(decoded)
        return true
    }

    override fun lastExport(): String = preferences.lastExport()

    private fun applySnapshot(snapshot: DiarySnapshot) {
        _bins.value = snapshot.bins
        _entries.value = snapshot.entries
        _readings.value = snapshot.readings
        _materials.value = snapshot.materials
        _batches.value = snapshot.batches
        _notes.value = snapshot.notes
    }

    private fun persist() {
        preferences.save(snapshot())
    }
}
