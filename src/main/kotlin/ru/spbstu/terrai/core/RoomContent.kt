package ru.spbstu.terrai.core

sealed class RoomContent

sealed class Item : RoomContent()

object Treasure : Item()