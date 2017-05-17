package ru.spbstu.terrai.core

data class Condition(val items: List<Item>, val exitReached: Boolean, val exitFind: Boolean) {
    constructor(): this(false, false)

    constructor(exitReached: Boolean, exitFind: Boolean): this(emptyList(), exitReached, exitFind)

    val hasTreasure get() = Treasure in items


}