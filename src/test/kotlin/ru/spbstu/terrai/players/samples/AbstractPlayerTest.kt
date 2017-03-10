package ru.spbstu.terrai.players.samples

import org.junit.Assert.assertEquals
import ru.spbstu.terrai.core.Player
import ru.spbstu.terrai.lab.Controller
import ru.spbstu.terrai.lab.Labyrinth

abstract class AbstractPlayerTest {

    abstract fun createPlayer(): Player

    fun doTestLab(fileName: String, expectedResult: Controller.GameResult) {
        val lab = Labyrinth.createFromFile(fileName)
        val player = createPlayer()
        val controller = Controller(lab, player)
        val actualResult = controller.makeMoves(500)
        assertEquals(controller.playerPath.toString(), expectedResult.exitReached, actualResult.exitReached)
        if (expectedResult.exitReached && actualResult.exitReached) {
            assertEquals(controller.playerPath.toString(), expectedResult.moves, actualResult.moves)
        }
    }

}