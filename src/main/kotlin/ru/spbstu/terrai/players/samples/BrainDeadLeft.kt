package ru.spbstu.terrai.players.samples

class BrainDeadLeft : BrainDead() {

    override fun getDirection() =
            if (lastSuccess) lastDirection
            else lastDirection.turnLeft()

}