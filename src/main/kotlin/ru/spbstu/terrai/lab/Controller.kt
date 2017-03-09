package ru.spbstu.terrai.lab

import ru.spbstu.terrai.core.*
import ru.spbstu.terrai.core.Move.Kind.WAIT
import ru.spbstu.terrai.core.Move.Kind.WALK
import java.util.*

class Controller(private val lab: Labyrinth, private val player: Player) {

    private var playerLocation = lab.entrances.let {
        it[random.nextInt(it.size)]
    }.apply {
        player.setStartLocationAndSize(this, lab.width, lab.height)
    }

    private var hasTreasure = false

    private var exitReached = false

    var moves = 0

    fun makeMove() {
        if (exitReached) return
        val move = player.getNextMove()
        val moveResult = when (move.kind) {
            WAIT -> {
                MoveResult(lab[playerLocation], true)
            }
            WALK -> {
                var newLocation = move.direction + playerLocation
                val newRoom = lab[newLocation]
                val movePossible = when (newRoom) {
                    Empty, Entrance -> true
                    Wall -> false
                    is WithContent -> {
                        if (newRoom.content == Treasure) {
                            hasTreasure = true
                            newRoom.content = null
                        }
                        true
                    }
                    Exit -> {
                        if (hasTreasure) exitReached = true
                        true
                    }
                    is Wormhole -> {
                        newLocation = lab.wormholeMap[newLocation]!!
                        true
                    }
                }
                playerLocation = newLocation
                MoveResult(newRoom, movePossible)
            }
        }
        player.setMoveResult(moveResult)
        moves++
    }

    companion object {
        val random = Random(Calendar.getInstance().timeInMillis)
    }
}