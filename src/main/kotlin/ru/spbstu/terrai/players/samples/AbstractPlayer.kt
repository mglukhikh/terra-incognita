package ru.spbstu.terrai.players.samples

import ru.spbstu.terrai.core.Location
import ru.spbstu.terrai.core.Player

abstract class AbstractPlayer : Player {

    override fun setStartLocationAndSize(location: Location, width: Int, height: Int) {
        startLocation = location
        this.width = width
        this.height = height
    }

    lateinit var startLocation: Location

    var width = 0

    var height = 0
}