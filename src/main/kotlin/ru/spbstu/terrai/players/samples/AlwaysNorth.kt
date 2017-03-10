package ru.spbstu.terrai.players.samples

import ru.spbstu.terrai.core.*

class AlwaysNorth : AbstractPlayer() {
    override fun getNextMove() = WalkMove(Direction.NORTH)

    override fun setMoveResult(result: MoveResult) {}
}