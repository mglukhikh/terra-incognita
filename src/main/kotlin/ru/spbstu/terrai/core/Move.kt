package ru.spbstu.terrai.core

sealed class Move

object WaitMove : Move()

class WalkMove(val direction: Direction) : Move()