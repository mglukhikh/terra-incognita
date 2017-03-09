package ru.spbstu.terrai.lab

import ru.spbstu.terrai.core.*
import java.io.File

class Labyrinth private constructor(val width: Int, val height: Int, private val map: Map<Location, Room>) {

    companion object {
        fun createFromFile(fileName: String): Labyrinth? {
            val lines = File(fileName).readLines()
            val height = lines.size - 2
            val width = (lines.maxBy { it.length } ?: return null).length - 2
            val map = hashMapOf<Location, Room>()
            val wormholes = mutableMapOf<Int, Wormhole>()
            for ((y, line) in lines.drop(1).dropLast(1).withIndex()) {
                val trimmedLine = line.drop(1).dropLast(1)
                for ((x, char) in trimmedLine.withIndex()) {
                    map[Location(x, y)] = when (char) {
                        ' ' -> Empty
                        'S' -> Entrance
                        'E' -> Exit
                        '#' -> Wall
                        'T' -> WithContent(Treasure)
                        in '0'..'9' -> Wormhole(char).apply { wormholes[char - '0'] = this }
                        else -> throw UnsupportedOperationException("Unsupported map symbol: $char")
                    }
                }
                for (x in trimmedLine.length..width - 1) {
                    map[Location(x, y)] = Empty
                }
            }
            for ((wormholeId, wormhole) in wormholes) {
                wormhole.next = wormholes[wormholeId + 1]
                                ?: wormholes[0]
                                ?: error("No next wormhole found for $wormholeId")
            }
            return Labyrinth(width, height, map)
        }
    }

}