package ru.spbstu.terrai.core

data class Condition(val items: List<Item>, val exitReached: Boolean) {
    constructor(): this(false)

    constructor(exitReached: Boolean): this(emptyList(), exitReached)

    val hasTreasure get() = Treasure in items
}