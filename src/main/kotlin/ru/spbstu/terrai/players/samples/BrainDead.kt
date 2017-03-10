package ru.spbstu.terrai.players.samples

import ru.spbstu.terrai.core.*
import ru.spbstu.terrai.core.Move.Kind.WALK

class BrainDead : AbstractPlayer() {

    private var lastDirection = Direction.NORTH

    private var lastSuccess = true

    private fun getDirection() =
            if (lastSuccess) Direction.NORTH
            else lastDirection.turnRight()

    override fun getNextMove() = Move(WALK, getDirection().apply { lastDirection = this })

    override fun setMoveResult(result: MoveResult) {
        lastSuccess = result.successful
    }
}