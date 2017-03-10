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

    internal val playerPath = mutableMapOf<Int, Location>(0 to playerLocation)

    data class GameResult(val moves: Int, val exitReached: Boolean)

    fun makeMoves(moveLimit: Int): GameResult {
        while (moves < moveLimit) {
            val moveResult = makeMove()
            playerPath[moves] = playerLocation
            if (moveResult.exitReached) return moveResult
        }
        return GameResult(moves, exitReached = false)
    }

    fun makeMove(): GameResult {
        if (playerCondition.exitReached) return GameResult(moves, exitReached = true)
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
                    Wall -> {
                        newLocation = playerLocation
                        false to "Wall prevents from moving"
                    }
                    is WithContent -> {
                        val content = newRoom.content
                        when (content) {
                            is Item -> {
                                playerCondition = playerCondition.copy(items = playerCondition.items + content)
                                newRoom.content = null
                                true to "Treasure found"
                            }
                            null -> true to "Empty room appears"
                            else -> throw UnsupportedOperationException("Unsupported content: $content")
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
        if (moveResult.successful) {
            moves++
        }
        return GameResult(moves, playerCondition.exitReached)
    }

    companion object {
        val random = Random(Calendar.getInstance().timeInMillis)
    }
}