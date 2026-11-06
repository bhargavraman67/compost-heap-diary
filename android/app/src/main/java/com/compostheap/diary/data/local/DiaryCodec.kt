package com.compostheap.diary.data.local

import com.compostheap.diary.domain.model.Batch
import com.compostheap.diary.domain.model.Bin
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.DiarySnapshot
import com.compostheap.diary.domain.model.Entry
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.model.GardenNote
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.domain.model.TemperatureReading
import org.json.JSONArray
import org.json.JSONObject

object DiaryCodec {

    fun encode(snapshot: DiarySnapshot): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("bins", JSONArray().also { array ->
            snapshot.bins.forEach { bin ->
                array.put(
                    JSONObject()
                        .put("id", bin.id)
                        .put("name", bin.name)
                        .put("startedAtMs", bin.startedAtMs)
                        .put("lastTurnedAtMs", bin.lastTurnedAtMs ?: -1L)
                        .put("resting", bin.resting)
                )
            }
        })
        root.put("entries", JSONArray().also { array ->
            snapshot.entries.forEach { entry ->
                array.put(
                    JSONObject()
                        .put("id", entry.id)
                        .put("binId", entry.binId)
                        .put("entryType", entry.entryType.name)
                        .put("materialId", entry.materialId ?: "")
                        .put("amount", entry.amount.toDouble())
                        .put("timestampMs", entry.timestampMs)
                )
            }
        })
        root.put("readings", JSONArray().also { array ->
            snapshot.readings.forEach { reading ->
                array.put(
                    JSONObject()
                        .put("id", reading.id)
                        .put("binId", reading.binId)
                        .put("celsius", reading.celsius.toDouble())
                        .put("timestampMs", reading.timestampMs)
                )
            }
        })
        root.put("materials", JSONArray().also { array ->
            snapshot.materials.forEach { material ->
                array.put(
                    JSONObject()
                        .put("id", material.id)
                        .put("name", material.name)
                        .put("type", material.type.name)
                        .put("custom", material.custom)
                )
            }
        })
        root.put("batches", JSONArray().also { array ->
            snapshot.batches.forEach { batch ->
                array.put(
                    JSONObject()
                        .put("id", batch.id)
                        .put("binId", batch.binId)
                        .put("binName", batch.binName)
                        .put("startedAtMs", batch.startedAtMs)
                        .put("harvestedAtMs", batch.harvestedAtMs)
                        .put("yieldLitres", batch.yieldLitres.toDouble())
                        .put("greenRatio", batch.greenRatio.toDouble())
                        .put("peakCelsius", (batch.peakCelsius ?: -1f).toDouble())
                        .put("note", batch.note)
                )
            }
        })
        root.put("notes", JSONArray().also { array ->
            snapshot.notes.forEach { note ->
                array.put(
                    JSONObject()
                        .put("id", note.id)
                        .put("batchId", note.batchId ?: "")
                        .put("bedName", note.bedName)
                        .put("text", note.text)
                        .put("createdAtMs", note.createdAtMs)
                )
            }
        })
        return root.toString()
    }

    fun decode(raw: String): DiarySnapshot? {
        return try {
            val root = JSONObject(raw)
            val bins = mutableListOf<Bin>()
            val binsArray = root.optJSONArray("bins") ?: JSONArray()
            for (i in 0 until binsArray.length()) {
                val item = binsArray.getJSONObject(i)
                val turned = item.optLong("lastTurnedAtMs", -1L)
                bins.add(
                    Bin(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        startedAtMs = item.getLong("startedAtMs"),
                        lastTurnedAtMs = if (turned <= 0L) null else turned,
                        resting = item.optBoolean("resting", false)
                    )
                )
            }
            val entries = mutableListOf<Entry>()
            val entriesArray = root.optJSONArray("entries") ?: JSONArray()
            for (i in 0 until entriesArray.length()) {
                val item = entriesArray.getJSONObject(i)
                val materialId = item.optString("materialId", "")
                entries.add(
                    Entry(
                        id = item.getString("id"),
                        binId = item.getString("binId"),
                        entryType = parseEntryType(item.optString("entryType", "ADDITION")),
                        materialId = if (materialId.isEmpty()) null else materialId,
                        amount = item.optDouble("amount", 0.0).toFloat(),
                        timestampMs = item.getLong("timestampMs")
                    )
                )
            }
            val readings = mutableListOf<TemperatureReading>()
            val readingsArray = root.optJSONArray("readings") ?: JSONArray()
            for (i in 0 until readingsArray.length()) {
                val item = readingsArray.getJSONObject(i)
                readings.add(
                    TemperatureReading(
                        id = item.getString("id"),
                        binId = item.getString("binId"),
                        celsius = item.optDouble("celsius", 0.0).toFloat(),
                        timestampMs = item.getLong("timestampMs")
                    )
                )
            }
            val materials = mutableListOf<CompostMaterial>()
            val materialsArray = root.optJSONArray("materials") ?: JSONArray()
            for (i in 0 until materialsArray.length()) {
                val item = materialsArray.getJSONObject(i)
                materials.add(
                    CompostMaterial(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        type = parseMaterialType(item.optString("type", "GREEN")),
                        custom = item.optBoolean("custom", false)
                    )
                )
            }
            val batches = mutableListOf<Batch>()
            val batchesArray = root.optJSONArray("batches") ?: JSONArray()
            for (i in 0 until batchesArray.length()) {
                val item = batchesArray.getJSONObject(i)
                val peak = item.optDouble("peakCelsius", -1.0).toFloat()
                batches.add(
                    Batch(
                        id = item.getString("id"),
                        binId = item.getString("binId"),
                        binName = item.getString("binName"),
                        startedAtMs = item.getLong("startedAtMs"),
                        harvestedAtMs = item.getLong("harvestedAtMs"),
                        yieldLitres = item.optDouble("yieldLitres", 0.0).toFloat(),
                        greenRatio = item.optDouble("greenRatio", 0.5).toFloat(),
                        peakCelsius = if (peak < 0f) null else peak,
                        note = item.optString("note", "")
                    )
                )
            }
            val notes = mutableListOf<GardenNote>()
            val notesArray = root.optJSONArray("notes") ?: JSONArray()
            for (i in 0 until notesArray.length()) {
                val item = notesArray.getJSONObject(i)
                val batchId = item.optString("batchId", "")
                notes.add(
                    GardenNote(
                        id = item.getString("id"),
                        batchId = if (batchId.isEmpty()) null else batchId,
                        bedName = item.getString("bedName"),
                        text = item.optString("text", ""),
                        createdAtMs = item.getLong("createdAtMs")
                    )
                )
            }
            if (materials.isEmpty()) return null
            DiarySnapshot(bins, entries, readings, materials, batches, notes)
        } catch (error: Exception) {
            null
        }
    }

    private fun parseEntryType(value: String): EntryType = when (value) {
        "TURN" -> EntryType.TURN
        "WATER" -> EntryType.WATER
        "READING" -> EntryType.READING
        else -> EntryType.ADDITION
    }

    private fun parseMaterialType(value: String): MaterialType =
        if (value == "BROWN") MaterialType.BROWN else MaterialType.GREEN
}
