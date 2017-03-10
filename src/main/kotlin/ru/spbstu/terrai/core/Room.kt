package ru.spbstu.terrai.core

sealed class Room(open val content: RoomContent? = null)

object Empty : Room()

data class WithContent(override var content: RoomContent?) : Room()

object Wall : Room()

object Entrance : Room()

object Exit : Room()

data class Wormhole(private val id: Int) : Room() {
    private var nextStored: Wormhole? = null

    var next: Wormhole
        get() = nextStored ?: error("No next wormhole was set for $id")
        set(value) {
            if (nextStored != null) {
                error("Next wormhole is already initialized for $id")
            }
            nextStored = value
        }

    constructor(id: Char): this(id - '0')
}

