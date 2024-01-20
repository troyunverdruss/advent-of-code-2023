package day21

import day16.Day16.Companion.parseLinesToGrid
import day16.Direction
import day16.Point
import java.io.File

class Day21 {
    fun part1() : Long {
        val lines = File("inputs/day21.txt").readLines()
        return solvePart1(lines, 64)
    }

    fun solvePart1(lines: List<String>, targetSteps: Long) : Long {
        grid = parseLinesToGrid(lines)
        val start = grid.filter { it.value == "S" }.map { it.key }.first()
        return find(targetSteps, start, 0).size.toLong()
    }

    fun find(targetSteps: Long, loc: Point, steps: Long): Set<Point> {
        val key = MemoKey(targetSteps, loc, steps)
        memoMap[key]?.let {
            return it
        }

        // We reached the target, return this point as a valid location
        if (targetSteps == steps) {
            val result = setOf(loc)
            memoMap[key] = result
            return result
        }

        val result = Direction.values().flatMap { dir ->
            val nextPoint = loc + dir.point
            grid[nextPoint]?.let { gridValue ->
                if (gridValue != "#") {
                    find(targetSteps, nextPoint, steps + 1)
                } else {
                    setOf()
                }
            } ?: setOf()
        }.toSet()

        memoMap[key] = result
        return result
    }

    companion object {
        var grid: Map<Point, String> = mapOf()
        val memoMap: MutableMap<MemoKey, Set<Point>> = mutableMapOf()

        data class MemoKey(val targetSteps: Long, val loc: Point, val steps: Long)
    }
}

