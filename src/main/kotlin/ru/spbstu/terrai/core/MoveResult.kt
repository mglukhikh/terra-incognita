package ru.spbstu.terrai.core

class MoveResult(
        val room: Room,
        val condition: Condition,
        val successful: Boolean,
        val status: String
)