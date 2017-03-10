package ru.spbstu.terrai.core

class Move(val kind: Kind, val direction: Direction) {

    enum class Kind {
        WALK,
        WAIT;
    }
}