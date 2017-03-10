package ru.spbstu.terrai.core

class MoveResult(
        // Which room was at target location
        val room: Room,
        // Condition after move (contains list of items and exit reaching flag)
        val condition: Condition,
        // true if move was successful
        val successful: Boolean,
        // status string (normally should not be analyzed)
        val status: String
)