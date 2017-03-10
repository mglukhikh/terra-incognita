package ru.spbstu.terrai.players.samples

import org.junit.Assert.*
import org.junit.Test
import ru.spbstu.terrai.lab.Controller
import ru.spbstu.terrai.lab.Labyrinth

class BrainDeadTest {

    @Test
    fun testLab1() {
        val lab = Labyrinth.createFromFile("labyrinths/lab1.txt")
        val player = BrainDead()
        val controller = Controller(lab, player)
        assertEquals(Controller.GameResult(4, exitReached = true), controller.makeMoves(100))
    }

}