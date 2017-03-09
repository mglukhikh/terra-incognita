package ru.spbstu.terrai.core

class Move(val kind: Kind, val direction: Direction) {

    enum class Kind {
        WALK,
        WAIT;
    }

    enum class Direction(val dx: Int, val dy: Int) {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        operator fun plus(location: Location) = Location(location.x + dx, location.y + dy)
    }
}