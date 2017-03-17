// Vargulev A., BPI143.
// 16.03.2017

package ru.spbstu.terrai.generator

import java.io.File
import java.security.SecureRandom
import java.util.*

enum class LabType {
    SHARP,
    CAVE,
    MAZE
}

// creates random labyrinth dummy (for cellular automaton)
private fun initMap(width: Int, height: Int, aliveChance: Double): Array<BooleanArray> {
    val map = Array(width, { BooleanArray(height) })
    // false - free space
    // true - walls

    val random = SecureRandom()

    for (i in 0..width - 1)
        for (j in 0..height - 1)
            map[i][j] = (random.nextDouble() < aliveChance)

    return map
}

// checks if nearby cells 'live' (for cellular automaton)
private fun countAlive(map: Array<BooleanArray>, x: Int, y: Int): Int {
    var count = 0

    for (i in -1..1) {
        for (j in -1..1) {
            if (i != 0 || j != 0) {
                val tx = x + i
                val ty = y + j

                if (tx < 0 || tx >= map.size || ty < 0 || ty >= map[0].size || map[tx][ty])
                    count++
            }
        }
    }

    return count
}

// checks array equality
private fun arrEq(arr1: Array<BooleanArray>, arr2: Array<BooleanArray>): Boolean {
    if (arr1.size != arr2.size)
        return false

    for (i in 0..arr1.size - 1)
        for (j in 0..arr1[0].size - 1)
            if (arr1[i][j] != arr2[i][j])
                return false

    return true
}

// cellular automaton
private fun processMap(map: Array<BooleanArray>, birthLim: Int, deathLim: Int): Array<BooleanArray> {
    val oldMap = map
    val tempMap = Array(map.size, { BooleanArray(map[0].size) })
    var flag: Boolean = false

    while (!arrEq(tempMap, oldMap)) {
        if (flag)
            for (i in 0..oldMap.size - 1)
                for (j in 0..oldMap[0].size - 1)
                    oldMap[i][j] = tempMap[i][j]
        else
            flag = true

        for (i in 0..oldMap.size - 1)
            for (j in 0..oldMap[0].size - 1) {
                val count = countAlive(map, i, j)
                tempMap[i][j] = (oldMap[i][j] && count >= deathLim) || (!oldMap[i][j] && count > birthLim)
            }
    }

    for (i in 0..oldMap.size - 1) {
        tempMap[i][0] = true
        tempMap[i][oldMap[0].size - 1] = true
    }

    for (i in 0..oldMap[0].size - 1) {
        tempMap[0][i] = true
        tempMap[oldMap.size - 1][i] = true
    }

    return tempMap
}

// sets holes, start/end, treasure
fun setHoles(map: Array<BooleanArray>): Array<CharArray> {
    val size = (map.size - 2) * (map[0].size - 2)
    val random = SecureRandom()
    val number = random.nextInt(minOf(10, size))

    val res = Array(map.size, { CharArray(map[0].size) })

    for (i in 0..map.size - 1)
        for (j in 0..map[0].size - 1)
            if (map[i][j])
                res[i][j] = '#'
            else
                res[i][j] = ' '


    for (i in 0..number - 1) {
        val pair = getEmptyPlace(map)
        val x = pair.first
        val y = pair.second
        res[x][y] = i.toString()[0]
    }

    var pair = getEmptyPlace(map)
    val xs = pair.first
    val ys = pair.second
    res[xs][ys] = 'S'

    pair = getEmptyPlace(map)
    val xe = pair.first
    val ye = pair.second
    res[xe][ye] = 'E'

    pair = getEmptyPlace(map)
    val xt = pair.first
    val yt = pair.second
    res[xt][yt] = 'T'

    return res
}

// gets random empty cell
private fun getEmptyPlace(map: Array<BooleanArray>): Pair<Int, Int> {
    val random: Random = SecureRandom()
    var x = 0
    var y = 0

    while (map[x][y]) {
        x = random.nextInt(map.size - 3) + 1
        y = random.nextInt(map[0].size - 3) + 1
    }

    return Pair(x, y)
}

// prints the labyrinth to file (by File)
fun printMap(map: Array<CharArray>, file: File) {
    var str: String = ""
    for (i in 0..map.size - 1) {
        for (j in 0..map[0].size - 1)
            str += map[i][j]
        str += '\n'
    }
    str += '\n'
    file.writeText(str)
}

// prints the labyrinth to file (by file name)
fun printMap(map: Array<CharArray>, fileName: String) {
    printMap(map, File(fileName))
}

// user control
fun generateLab(width: Int, height: Int, type: LabType) = when (type) {
    LabType.SHARP -> setHoles(processMap(initMap(height, width, 0.4), 5, 2))
    LabType.CAVE -> setHoles(processMap(initMap(height, width, 0.4), 4, 3))
    LabType.MAZE -> setHoles(genMaze(width, height))
}

// generates maze dummy
private fun genMazeDummy(width: Int, height: Int): Array<BooleanArray> {
    val map = Array(height, { BooleanArray(width, { true }) })
    // false - free space
    // true - walls

    for (i in 1..height - 2)
        for (j in 1..width - 2)
            if (i % 2 == 1 && j % 2 == 1)
                map[i][j] = false

    return map
}

// generates maze walls
private fun genMaze(width: Int, height: Int): Array<BooleanArray> {
    val map = genMazeDummy(width, height)
    val random = SecureRandom()

    for (i in 0..(height * width / 3)) {
        var x = random.nextInt(height - 3) + 1
        val y = random.nextInt(width - 3) + 1

        if (x % 2 == 1 && y % 2 == 1)
            x++
        if (x % 2 == 0 && y % 2 == 0)
            x--
        if (x == height - 2 || y == width - 2)
            continue

        map[x][y] = false
    }

    return map
}

fun main(args: Array<String>) {
    //demo code
    printMap(generateLab(40, 25, LabType.SHARP), "sharp.txt")
    printMap(generateLab(40, 25, LabType.CAVE), "cave.txt")
    printMap(generateLab(40, 25, LabType.MAZE), "maze.txt")
}