package ru.spbstu.terrai.core

interface Player {
    // Called by master to set start player location and labyrinth size
    fun setStartLocationAndSize(location: Location, width: Int, height: Int)

    // Called by master to get next player's move
    fun getNextMove(): Move

    // Called by master to set last player's move result
    fun setMoveResult(result: MoveResult)
}