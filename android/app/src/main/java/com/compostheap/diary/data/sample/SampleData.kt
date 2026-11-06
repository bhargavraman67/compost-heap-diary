package com.compostheap.diary.data.sample

import com.compostheap.diary.domain.model.Bin
import com.compostheap.diary.domain.model.CompostMaterial
import com.compostheap.diary.domain.model.DiarySnapshot
import com.compostheap.diary.domain.model.Entry
import com.compostheap.diary.domain.model.EntryType
import com.compostheap.diary.domain.model.GardenNote
import com.compostheap.diary.domain.model.MaterialType
import com.compostheap.diary.domain.model.TemperatureReading

object SampleData {

    private const val DAY = 86_400_000L

    val materials: List<CompostMaterial> = listOf(
        CompostMaterial("m_grass", "Grass Clippings", MaterialType.GREEN, false),
        CompostMaterial("m_veg", "Vegetable Peelings", MaterialType.GREEN, false),
        CompostMaterial("m_coffee", "Coffee Grounds", MaterialType.GREEN, false),
        CompostMaterial("m_comfrey", "Comfrey Leaves", MaterialType.GREEN, false),
        CompostMaterial("m_weeds", "Annual Weeds", MaterialType.GREEN, false),
        CompostMaterial("m_fruit", "Fruit Trimmings", MaterialType.GREEN, false),
        CompostMaterial("m_manure", "Well Rotted Manure", MaterialType.GREEN, false),
        CompostMaterial("m_leaves", "Dry Autumn Leaves", MaterialType.BROWN, false),
        CompostMaterial("m_card", "Torn Cardboard", MaterialType.BROWN, false),
        CompostMaterial("m_straw", "Barley Straw", MaterialType.BROWN, false),
        CompostMaterial("m_wood", "Wood Shavings", MaterialType.BROWN, false),
        CompostMaterial("m_twigs", "Shredded Twigs", MaterialType.BROWN, false),
        CompostMaterial("m_paper", "Shredded Paper", MaterialType.BROWN, false),
        CompostMaterial("m_husks", "Nut Husks", MaterialType.BROWN, false)
    )

    fun snapshot(nowMs: Long): DiarySnapshot {
        val kitchen = Bin(
            id = "bin_kitchen",
            name = "Kitchen Heap",
            startedAtMs = nowMs - 24 * DAY,
            lastTurnedAtMs = nowMs - 3 * DAY,
            resting = false
        )
        val leafMould = Bin(
            id = "bin_leaf",
            name = "Leaf Mould Bay",
            startedAtMs = nowMs - 61 * DAY,
            lastTurnedAtMs = nowMs - 11 * DAY,
            resting = false
        )

        val entries = listOf(
            addition("e01", kitchen.id, "m_veg", 6f, nowMs - 23 * DAY),
            addition("e02", kitchen.id, "m_card", 5f, nowMs - 23 * DAY),
            addition("e03", kitchen.id, "m_coffee", 2f, nowMs - 21 * DAY),
            addition("e04", kitchen.id, "m_leaves", 8f, nowMs - 20 * DAY),
            marker("e05", kitchen.id, EntryType.WATER, nowMs - 19 * DAY),
            addition("e06", kitchen.id, "m_grass", 9f, nowMs - 17 * DAY),
            addition("e07", kitchen.id, "m_straw", 7f, nowMs - 16 * DAY),
            marker("e08", kitchen.id, EntryType.TURN, nowMs - 14 * DAY),
            addition("e09", kitchen.id, "m_veg", 4f, nowMs - 12 * DAY),
            addition("e10", kitchen.id, "m_paper", 3f, nowMs - 11 * DAY),
            addition("e11", kitchen.id, "m_fruit", 3f, nowMs - 8 * DAY),
            marker("e12", kitchen.id, EntryType.WATER, nowMs - 7 * DAY),
            addition("e13", kitchen.id, "m_twigs", 5f, nowMs - 6 * DAY),
            marker("e14", kitchen.id, EntryType.TURN, nowMs - 3 * DAY),
            addition("e15", kitchen.id, "m_coffee", 2f, nowMs - 2 * DAY),

            addition("e20", leafMould.id, "m_leaves", 22f, nowMs - 60 * DAY),
            addition("e21", leafMould.id, "m_twigs", 6f, nowMs - 58 * DAY),
            marker("e22", leafMould.id, EntryType.WATER, nowMs - 54 * DAY),
            addition("e23", leafMould.id, "m_grass", 5f, nowMs - 48 * DAY),
            addition("e24", leafMould.id, "m_leaves", 14f, nowMs - 40 * DAY),
            marker("e25", leafMould.id, EntryType.TURN, nowMs - 32 * DAY),
            addition("e26", leafMould.id, "m_comfrey", 4f, nowMs - 25 * DAY),
            addition("e27", leafMould.id, "m_husks", 6f, nowMs - 18 * DAY),
            marker("e28", leafMould.id, EntryType.TURN, nowMs - 11 * DAY)
        )

        val readings = listOf(
            TemperatureReading("r01", kitchen.id, 28f, nowMs - 22 * DAY),
            TemperatureReading("r02", kitchen.id, 41f, nowMs - 18 * DAY),
            TemperatureReading("r03", kitchen.id, 57f, nowMs - 15 * DAY),
            TemperatureReading("r04", kitchen.id, 64f, nowMs - 13 * DAY),
            TemperatureReading("r05", kitchen.id, 49f, nowMs - 8 * DAY),
            TemperatureReading("r06", kitchen.id, 38f, nowMs - 2 * DAY),
            TemperatureReading("r07", leafMould.id, 24f, nowMs - 50 * DAY),
            TemperatureReading("r08", leafMould.id, 31f, nowMs - 30 * DAY),
            TemperatureReading("r09", leafMould.id, 26f, nowMs - 10 * DAY)
        )

        val notes = listOf(
            GardenNote(
                id = "n01",
                batchId = null,
                bedName = "Raised Bed by the Shed",
                text = "Two barrows of last winter compost dug in before the broad beans went out.",
                createdAtMs = nowMs - 34 * DAY
            ),
            GardenNote(
                id = "n02",
                batchId = null,
                bedName = "Rhubarb Corner",
                text = "Thick mulch on top of the crowns, left rough so the worms can pull it down.",
                createdAtMs = nowMs - 9 * DAY
            )
        )

        return DiarySnapshot(
            bins = listOf(kitchen, leafMould),
            entries = entries,
            readings = readings,
            materials = materials,
            batches = emptyList(),
            notes = notes
        )
    }

    private fun addition(id: String, binId: String, materialId: String, amount: Float, at: Long) =
        Entry(id, binId, EntryType.ADDITION, materialId, amount, at)

    private fun marker(id: String, binId: String, type: EntryType, at: Long) =
        Entry(id, binId, type, null, 0f, at)
}
