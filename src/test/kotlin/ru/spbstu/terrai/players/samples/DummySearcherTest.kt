package ru.spbstu.terrai.players.samples

import org.junit.Test
import ru.spbstu.terrai.lab.Controller

class DummySearcherTest : AbstractPlayerTest() {

    override fun createPlayer() = DummySearcher()

    @Test
    fun testLab1() {
        doTestLab("labyrinths/lab1.txt", Controller.GameResult(4, exitReached = true))
    }

    @Test
    fun testLab2() {
        doTestLab("labyrinths/lab2.txt", Controller.GameResult(48, exitReached = true))
    }

    @Test
    fun testLab3() {
        doTestLab("labyrinths/lab3.txt", Controller.GameResult(9, exitReached = true))
    }

    @Test
    fun testLab4() {
        doTestLab("labyrinths/lab4.txt", Controller.GameResult(43, exitReached = true))
    }

    @Test
    fun testLab5() {
        doTestLab("labyrinths/lab5.txt", Controller.GameResult(100, exitReached = false))
    }

    @Test
    fun testLab6() {
        doTestLab("labyrinths/lab6.txt", Controller.GameResult(100, exitReached = false))
    }

    @Test
    fun testLab7() {
        doTestLab("labyrinths/lab7.txt", Controller.GameResult(100, exitReached = false))
    }
}