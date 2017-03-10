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

    private var playerCondition: Condition = Condition()

    var moves = 0

    fun makeMove() {
        if (playerCondition.exitReached) return
        val move = player.getNextMove()
        val moveResult = when (move.kind) {
            WAIT -> {
                MoveResult(lab[playerLocation], playerCondition, true, "Nothing changes")
            }
            WALK -> {
                var newLocation = move.direction + playerLocation
                val newRoom = lab[newLocation]
                val (movePossible, status) = when (newRoom) {
                    Empty, Entrance -> true to "Empty room appears"
                    Wall -> false to "Wall prevents from moving"
                    is WithContent -> {
                        when (newRoom.content) {
                            Treasure -> {
                                playerCondition = playerCondition.copy(items = playerCondition.items + Treasure)
                                newRoom.content = null
                                true to "Treasure found"
                            }
                            null -> true to "Empty room appears"
                            else -> throw UnsupportedOperationException("Unsupported content: ${newRoom.content}")
                        }
                    }
                    Exit -> {
                        if (playerCondition.hasTreasure) {
                            playerCondition = playerCondition.copy(exitReached = true)
                            true to "Exit reached, you won"
                        }
                        else {
                            true to "Exit reached but you do not have a treasure"
                        }
                    }
                    is Wormhole -> {
                        newLocation = lab.wormholeMap[newLocation]!!
                        true to "Fall into wormhole!"
                    }
                }
                playerLocation = newLocation
                MoveResult(newRoom, playerCondition, movePossible, status)
            }
        }
        player.setMoveResult(moveResult)
        moves++
    }

    companion object {
        val random = Random(Calendar.getInstance().timeInMillis)
    }
}