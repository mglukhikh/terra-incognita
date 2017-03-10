package ru.spbstu.terrai.core

enum class Direction(val dx: Int, val dy: Int) {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    operator fun plus(location: Location) = Location(location.x + dx, location.y + dy)

    fun turnRight() = Direction.values()[if (ordinal < Direction.values().size - 1) ordinal + 1 else 0]

    fun turnLeft() = Direction.values()[if (ordinal > 0) ordinal - 1 else Direction.values().size - 1]

    fun turnBack() = Direction.values()[(ordinal + 2) % Direction.values().size]
}