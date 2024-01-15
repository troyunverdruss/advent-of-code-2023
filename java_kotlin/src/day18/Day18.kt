package day18

import day16.Direction
import day16.Point
import java.io.File
import java.util.LinkedList

class Day18 {
    fun part1(): Long {
        val lines = File("inputs/day18.txt").readLines()
        return solvePart1(lines)
    }

    fun solvePart1(lines: List<String>): Long {
        val instructions = lines.map { Instruction.parse(it) }.toList()
        val grid = HashMap<Point, String>()
        val start = Point(0, 0)
        grid[start] = "#"

        var currentLoc = start
        instructions.forEach { instruction ->
            (0..<instruction.steps).forEach {
                currentLoc += instruction.direction.point
                grid[currentLoc] = "#"
            }
        }

        val minX = grid.minBy { e -> e.key.x }.key.x - 1
        val minY = grid.minBy { e -> e.key.y }.key.y - 1
        val maxX = grid.maxBy { e -> e.key.x }.key.x + 1
        val maxY = grid.maxBy { e -> e.key.y }.key.y + 1

        traceAndFillTheExterior(
            grid,
            Point(minX, minY),
            Point(maxX, maxY),
        )

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val currLoc = Point(x, y)
                val currVal = grid[currLoc]
                if (currVal == null) {
                    grid[currLoc] = "#"
                }
            }
        }

        return grid.filterValues { v -> v == "#" }.count().toLong()
    }

    private fun traceAndFillTheExterior(grid: java.util.HashMap<Point, String>, min: Point, max: Point) {
        val start = Point(min.x, min.y)

        if (grid.containsKey(start)) {
            return
        }

        val visited = HashSet<Point>()
        val toVisit = LinkedList<Point>()
        toVisit.add(start)

        while (toVisit.isNotEmpty()) {
            val currLoc = toVisit.pop()
            visited.add(currLoc)
            val currVal = grid.get(currLoc)
            if (currVal == "#") {
                continue
            }

            grid[currLoc] = "O"
            for (d in Direction.entries) {
                val nextLoc = currLoc + d.point
                val inBounds = min.x <= nextLoc.x && nextLoc.x <= max.x && min.y <= nextLoc.y && nextLoc.y <= max.y
                if (inBounds && !visited.contains(nextLoc) && !toVisit.contains(nextLoc)) {
                    toVisit.add(nextLoc)
                }
            }
        }
    }
}

data class Instruction(val direction: Direction, val steps: Int, val hexColor: String) {
    companion object {
        fun parse(line: String): Instruction {
            val parts = line
                .replace("(", "")
                .replace(")", "")
                .replace("#", "")
                .split(" ")
            val direction = when (parts[0]) {
                "U" -> Direction.Up
                "R" -> Direction.Right
                "D" -> Direction.Down
                "L" -> Direction.Left
                else -> throw RuntimeException("Unknown char")
            }
            return Instruction(
                direction,
                parts[1].toInt(),
                parts[2]
            )
        }
    }
}