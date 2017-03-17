// Vargulev A., BPI143.
// 16.03.2017

package ru.spbstu.terrai.generator

import java.io.File
import java.security.SecureRandom
import java.util.*

enum class LabType
{
    SHARP,
    CAVE,
    MAZE
}

object MainClass
{
    // creates random labyrinth dummy (for cellular automaton)
    private fun initMap(width: Int, height: Int, aliveChance: Double) : Array<BooleanArray>
    {
        var map: Array<BooleanArray> = Array<BooleanArray>(width, {BooleanArray(height)})
        // false - free space
        // true - walls

        val random : Random = SecureRandom()

        for (i in 0..width-1)
            for (j in 0..height-1)
                map[i][j] = (random.nextDouble() < aliveChance)

        return map
    }

    // checks if nearby cells 'live' (for cellular automaton)
    private fun countAlive(map : Array<BooleanArray>, x : Int, y : Int) : Int
    {
        var count : Int = 0

        for (i in -1..1)
            for (j in -1..1)
            {
                if (!(i == 0 && j == 0))
                {
                    val tx : Int = x + i
                    val ty : Int = y + j

                    if (tx < 0 || tx >= map.size || ty < 0 || ty >= map[0].size || map[tx][ty])
                        count++
                }
            }

        return count
    }

    // checks array equality
    private fun arrEq(arr1 : Array<BooleanArray>, arr2 : Array<BooleanArray>) : Boolean
    {
        if (arr1.size != arr2.size)
            return false

        for (i in 0..arr1.size-1)
            for (j in 0..arr1[0].size-1)
                if (arr1[i][j] != arr2[i][j])
                    return false

        return true
    }

    // cellular automaton
    private fun processMap(map : Array<BooleanArray>, birthLim : Int, deathLim : Int) : Array<BooleanArray>
    {
        var oldMap = map
        var tempMap : Array<BooleanArray> = Array<BooleanArray>(map.size, {BooleanArray(map[0].size)})
        var flag : Boolean = false

        while (!arrEq(tempMap, oldMap))
        {
            if (flag)
                for (i in 0..oldMap.size-1)
                    for (j in 0..oldMap[0].size-1)
                        oldMap[i][j] = tempMap[i][j]
            else
                flag = true

            for (i in 0..oldMap.size-1)
                for (j in 0..oldMap[0].size-1)
                {
                    val count : Int = countAlive(map, i, j)
                    tempMap[i][j] = (oldMap[i][j] && (count >= deathLim)) || (!oldMap[i][j] && (count > birthLim))
                }
        }

        for (i in 0..oldMap.size-1)
        {
            tempMap[i][0] = true
            tempMap[i][oldMap[0].size-1] = true
        }

        for (i in 0..oldMap[0].size-1)
        {
            tempMap[0][i] = true
            tempMap[oldMap.size-1][i] = true
        }

        return tempMap
    }

    // for debugging
    fun printMap(map : Array<BooleanArray>)
    {
        for (i in map)
        {
            for (j in i)
            {
                if (j)
                    print('#')
                else
                    print(' ')
            }
            print('\n')
        }
    }

    // for debugging
    private fun getEmpty(map : Array<BooleanArray>) : Pair<Int, Int>
    {
        for (i in 0..map.size-1)
            for (j in 0..map[0].size-1)
                if (!map[i][j])
                    return Pair(i, j)
        return Pair(-1, -1)
    }

    // for debugging
    private fun fill(map: Array<BooleanArray>, x: Int, y: Int)
    {
        map[x][y] = true
        if (x > 0 && !map[x-1][y])
            fill(map, x-1, y)
        if (y > 0 && !map[x][y-1])
            fill(map, x, y-1)
        if (x < map.size-1 && !map[x+1][y])
            fill(map, x+1, y)
        if (y < map[0].size-1 && !map[x][y+1])
            fill(map, x, y+1)
    }

    // for debugging
    fun checkCont(map : Array<BooleanArray>) : Boolean
    {
        var temp : Array<BooleanArray> = Array<BooleanArray>(map.size, {BooleanArray(map[0].size)})

        for (i in 0..map.size-1)
            for (j in 0..map[0].size-1)
                temp[i][j] = map[i][j]

        var pair = getEmpty(temp)

        if (pair.first < 0)
            return false

        fill(temp, pair.first, pair.second)

        pair = getEmpty(temp)

        if (pair.first >= 0)
            return false

        return true
    }

    // sets holes, start/end, treasure
    fun setHoles(map : Array<BooleanArray>) : Array<CharArray>
    {
        val size : Int = (map.size - 2) * (map[0].size - 2)
        val random : Random = SecureRandom()
        val number : Int = random.nextInt(minOf(10, size))

        var res : Array<CharArray> = Array<CharArray>(map.size, {CharArray(map[0].size)})

        for (i in 0..map.size-1)
            for (j in 0..map[0].size-1)
                if (map[i][j])
                    res[i][j] = '#'
                else
                    res[i][j] = ' '


        for (i in 0..number-1)
        {
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
    private fun getEmptyPlace(map: Array<BooleanArray>) : Pair<Int, Int>
    {
        val random : Random = SecureRandom()
        var x = 0
        var y = 0

        while (map[x][y])
        {
            x = random.nextInt(map.size - 3) + 1
            y = random.nextInt(map[0].size - 3) + 1
        }

        return Pair(x, y)
    }

    // for debugging
    fun printMap(map : Array<CharArray>)
    {
        for (i in 0..map.size-1)
        {
            for (j in 0..map[0].size-1)
                print(map[i][j])
            print('\n')
        }
        print('\n')
    }

    // prints the labyrinth to file (by File)
    fun printMap(map: Array<CharArray>, file : File)
    {
        var str : String = ""
        for (i in 0..map.size-1)
        {
            for (j in 0..map[0].size-1)
                str += map[i][j]
            str += '\n'
        }
        str += '\n'
        file.writeText(str)
    }

    // prints the labyrinth to file (by file name)
    fun printMap(map: Array<CharArray>, fileName : String)
    {
        printMap(map, File(fileName))
    }

    // user control
    fun generateLab(width: Int, height: Int, type: LabType) : Array<CharArray>
    {
        when (type)
        {
            LabType.SHARP -> return setHoles(processMap(initMap(height, width, 0.4), 5, 2))
            LabType.CAVE -> return setHoles(processMap(initMap(height, width, 0.4), 4, 3))
            LabType.MAZE -> return setHoles(genMaze(width, height))
            else -> return Array<CharArray>(height, {CharArray(width)})
        }
    }

    // generates maze dummy
    private fun genMazeDummy(width: Int, height: Int) : Array<BooleanArray>
    {
        var map: Array<BooleanArray> = Array<BooleanArray>(height, {BooleanArray(width, {true})})
        // false - free space
        // true - walls

        for (i in 1..height-2)
            for (j in 1..width-2)
                if (i % 2 == 1 && j % 2 == 1)
                    map[i][j] = false

        return map
    }

    // generates maze walls
    private fun genMaze(width: Int, height: Int) : Array<BooleanArray>
    {
        var map : Array<BooleanArray> = genMazeDummy(width, height)
        val random : Random = SecureRandom()

        for (i in 0..(height * width / 3))
        {
            var x = random.nextInt(height - 3) + 1
            var y = random.nextInt(width - 3) + 1

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
}

fun main(args : Array<String>)
{
    //demo code
    MainClass.printMap(MainClass.generateLab(40, 25, LabType.SHARP), "sharp.txt")
    MainClass.printMap(MainClass.generateLab(40, 25, LabType.CAVE), "cave.txt")
    MainClass.printMap(MainClass.generateLab(40, 25, LabType.MAZE), "maze.txt")
}