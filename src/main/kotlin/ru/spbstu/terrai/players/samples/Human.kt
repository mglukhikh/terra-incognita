package ru.spbstu.terrai.players.samples

import ru.spbstu.terrai.core.*
import ru.spbstu.terrai.lab.Controller
import ru.spbstu.terrai.lab.Labyrinth

class Human : AbstractPlayer() {
    override fun getNextMove(): Move {
        println("Enter w (NORTH), d (EAST), x (SOUTH), a (WEST) or s (WAIT)")
        val answer = readLine()
        val move = when (answer) {
            "w" -> WalkMove(Direction.NORTH)
            "d" -> WalkMove(Direction.EAST)
            "x" -> WalkMove(Direction.SOUTH)
            "a" -> WalkMove(Direction.WEST)
            else -> WaitMove
        }
        return move
    }

    override fun setMoveResult(result: MoveResult) {
        println(result.status)
    }
}

fun main(args: Array<String>) {
    val lab = Labyrinth.createFromFile("labyrinths/lab3.txt")
    val player = Human()
    val controller = Controller(lab, player)
    val result = controller.makeMoves(1000)
    if (result.exitReached) {
        println("You won!")
    }
    else {
        println("You lose!")
    }
}