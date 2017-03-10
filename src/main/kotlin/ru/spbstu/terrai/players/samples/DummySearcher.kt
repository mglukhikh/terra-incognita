package ru.spbstu.terrai.players.samples

import ru.spbstu.terrai.core.*

class DummySearcher : AbstractPlayer() {

    private lateinit var currentLocation: Location

    private val roomMap = mutableMapOf<Location, Room>()

    override fun setStartLocationAndSize(location: Location, width: Int, height: Int) {
        super.setStartLocationAndSize(location, width, height)
        currentLocation = location
        roomMap[currentLocation] = Entrance
    }

    private var lastMove: Move = WaitMove

    private val decisions = mutableListOf<Direction>()

    private var wormholes = 0

    private var treasureFound = false

    override fun getNextMove(): Move {
        val toUnknownDirection = Direction.values().firstOrNull { it + currentLocation !in roomMap }
        lastMove = if (toUnknownDirection != null) {
            decisions += toUnknownDirection
            WalkMove(toUnknownDirection)
        }
        else {
            val lastDecision = decisions.lastOrNull()
            if (lastDecision != null) {
                decisions.removeAt(decisions.size - 1)
                val backDirection = lastDecision.turnBack()
                WalkMove(backDirection)
            }
            else {
                WaitMove
            }
        }
        return lastMove
    }

    override fun setMoveResult(result: MoveResult) {
        val newLocation = (lastMove as? WalkMove)?.let { it.direction + currentLocation } ?: currentLocation
        val room = result.room
        roomMap[newLocation] = room
        if (result.successful) {
            when(room) {
                is Wormhole -> {
                    decisions.clear()
                    wormholes++
                    currentLocation = Location(wormholes * 1000, wormholes * 1000)
                }
                is WithContent -> {
                    if (!treasureFound && result.condition.hasTreasure) {
                        decisions.clear()
                        roomMap.clear()
                        treasureFound = true
                    }
                }
                else -> currentLocation = newLocation
            }
        }
        else {
            decisions.removeAt(decisions.size - 1)
        }
    }
}