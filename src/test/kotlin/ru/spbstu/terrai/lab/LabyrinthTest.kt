package ru.spbstu.terrai.lab

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class LabyrinthTest {
    @Test
    fun testLabyrinths() {
        val dir = File("labyrinths")
        assertTrue(dir.isDirectory)
        for (file in dir.listFiles()) {
            val lab = Labyrinth.createFromFile(file)
            assertTrue(lab.isValid())
        }
    }
}