package day21

import day16.Day16.Companion.parseLinesToGrid
import day16.Direction
import day16.Point
import java.io.File
import kotlin.math.abs

class Day21 {
    fun part1(): Long {
        val lines = File("inputs/day21.txt").readLines()
        return solvePart1(lines, 64)
    }

    fun solvePart1(lines: List<String>, targetSteps: Long): Long {
        grid = parseLinesToGrid(lines)
        max = Point(
            grid.maxBy { it.key.x }.key.x,
            grid.maxBy { it.key.y }.key.y
        )
        val start = grid.filter { it.value == "S" }.map { it.key }.first()
        return find(targetSteps, start, 0).size.toLong()
    }

    fun find(targetSteps: Long, loc: Point, steps: Long): Map<Point, Long> {
        val key = MemoKey(targetSteps, loc, steps)
        memoMap[key]?.let {
            return it
        }

        // We reached the target, return this point as a valid location
        if (targetSteps == steps) {
            val result = mapOf(Pair(loc, 1L))
            memoMap[key] = result
            return result
        }

        val result = Direction.values().flatMap { dir ->
            val nextPoint = loc + dir.point
            val nextPointLookup = Point(
                boundLookup(max.x, nextPoint.x),
                boundLookup(max.y, nextPoint.y)
            )

            grid[nextPointLookup]?.let { gridValue ->
                if (gridValue != "#") {
                    listOf(find(targetSteps, nextPoint, steps + 1))
                } else {
                    listOf(mapOf())
                }
            }
                ?: throw RuntimeException()
        }

        val resultMap = mutableMapOf<Point, Long>()
        result.forEach { m ->
            m.forEach { e ->
                val currVal = resultMap[e.key] ?: 0
                resultMap[e.key] = currVal + e.value
            }
        }


        memoMap[key] = resultMap
        return resultMap
    }

    private fun boundLookup(maxValue: Long, value: Long): Long {
        return if (value < 0) {
            val offset = abs((value % (maxValue + 1)))
            if (offset == 0L) {
                offset
            } else {
                maxValue + 1 - offset
            }

        } else if (value > maxValue) {
            (value % (maxValue + 1))
        } else {
            value
        }
    }

    companion object {
        var grid: Map<Point, String> = mapOf()
        var max: Point = Point(0, 0)
        val memoMap: MutableMap<MemoKey, Map<Point, Long>> = mutableMapOf()

        data class MemoKey(val targetSteps: Long, val loc: Point, val steps: Long)
    }
}

