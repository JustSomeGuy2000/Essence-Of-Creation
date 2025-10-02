package jehr.experiments.essenceOfCreation.utils

import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.Direction

enum class CombinedBoolDir(val bool: Boolean, val dir: Direction): StringIdentifiable {
    FALSE_NORTH(false, Direction.NORTH) {
        override fun asString() = "false_north"
    }, FALSE_EAST(false, Direction.EAST) {
        override fun asString() = "false_east"
    }, FALSE_SOUTH(false, Direction.SOUTH) {
        override fun asString() = "false_south"
    }, FALSE_WEST(false, Direction.WEST) {
        override fun asString() = "false_west"
    }, FALSE_UP(false, Direction.UP) {
        override fun asString() = "false_up"
    }, FALSE_DOWN(false, Direction.DOWN) {
        override fun asString() = "false_down"
    }, TRUE_NORTH(true, Direction.NORTH) {
        override fun asString() = "true_north"
    }, TRUE_EAST(true, Direction.EAST) {
        override fun asString() = "true_east"
    }, TRUE_SOUTH(true, Direction.SOUTH) {
        override fun asString() = "true_south"
    }, TRUE_WEST(true, Direction.WEST) {
        override fun asString() = "true_west"
    }, TRUE_UP(true, Direction.UP) {
        override fun asString() = "true_up"
    }, TRUE_DOWN(true, Direction.DOWN) {
        override fun asString() = "true_down"
    };

    companion object {
        fun of(bool: Boolean, dir: Direction) = when(bool) {
            true -> when(dir) {
                Direction.NORTH -> TRUE_NORTH
                Direction.EAST -> TRUE_EAST
                Direction.WEST -> TRUE_WEST
                Direction.SOUTH -> TRUE_SOUTH
                Direction.UP -> TRUE_UP
                Direction.DOWN -> TRUE_DOWN
            }
            false -> when(dir) {
                Direction.NORTH -> FALSE_NORTH
                Direction.EAST -> FALSE_EAST
                Direction.WEST -> FALSE_WEST
                Direction.SOUTH -> FALSE_SOUTH
                Direction.UP -> FALSE_UP
                Direction.DOWN -> FALSE_DOWN
            }
        }

        fun modBool(entry: CombinedBoolDir, mod: (Boolean) -> Boolean) = of(mod(entry.bool), entry.dir)
        fun modDir(entry: CombinedBoolDir, mod: (Direction) -> Direction) = of(entry.bool, mod(entry.dir))
    }
}