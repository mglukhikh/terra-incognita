package ru.spbstu.terrai.players.samples

import ru.spbstu.terrai.core.*

open class BrainDead : AbstractPlayer() {

    protected var lastDirection = Direction.NORTH

    protected var lastSuccess = true

    protected open fun getDirection() =
            if (lastSuccess) lastDirection
            else lastDirection.turnRight()

    override fun getNextMove() = WalkMove(getDirection().apply { lastDirection = this })

    override fun setMoveResult(result: MoveResult) {
        lastSuccess = result.successful
    }
}