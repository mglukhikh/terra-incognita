package ru.spbstu.terrai.players.samples

import ru.spbstu.terrai.core.Direction

class BrainDeadLeft : BrainDead() {

    override fun getDirection() =
            if (lastSuccess) Direction.NORTH
            else lastDirection.turnLeft()

}