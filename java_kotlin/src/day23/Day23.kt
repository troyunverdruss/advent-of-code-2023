package day23

import day16.Day16
import day16.Direction
import day16.Point
import java.io.File

class Day23 {
    fun part1(): Long {
        val lines = File("inputs/day23.txt").readLines()
        return solver(lines, this::isAllowedPart1)
    }

    fun part2(): Long {
        val lines = File("inputs/day23.txt").readLines()
        return solver(lines, this::isAllowedPart2)
    }

    fun solver(lines: List<String>, isAllowed: (Point, Set<Point>, Direction, String) -> Boolean): Long {
        val grid = Day16.parseLinesToGrid(lines).toMutableMap()
        val start = grid
            .filter { it.key.y == 0L && it.value == "." }
            .map { it.key }
            .first()
        val end = grid
            .filter { it.key.y == grid.maxBy { it.key.y }.key.y && it.value == "." }
            .map { it.key }
            .first()

        return findLongestPath(
            isAllowed,
            grid,
            start,
            end,
            LinkedHashSet()
        )
    }

    fun findLongestPath(
        isAllowed: (Point, Set<Point>, Direction, String) -> Boolean,
        grid: Map<Point, String>,
        loc: Point,
        dest: Point,
        visited: LinkedHashSet<Point>
    ): Long {
        visited.addLast(loc)
//        if (loc == Point(13,19) && visited.size == 76) {
//            debugPrintPath(grid, visited, visited.size.toLong())
//            val i = 0
//        }
        if (loc == dest) {
            val length = visited.size.toLong() - 1 // Don't include the starting point
//            debugPrintPath(grid, visited, length)
            visited.removeLast()
            return length
        }

        val maxLength = Direction.entries.map { dir ->
            val testLoc = loc + dir.point
            val allowed = isAllowed(testLoc, visited, dir, grid[testLoc] ?: "#")
            if (allowed) {
                findLongestPath(isAllowed, grid, testLoc, dest, visited)
            } else 0L
        }.max()

        visited.removeLast()
        return maxLength
    }

    private fun debugPrintPath(
        grid: Map<Point, String>,
        visited: LinkedHashSet<Point>,
        length: Long
    ) {
        val myGrid = grid.toMutableMap()
        visited.forEach { myGrid[it] = "O" }
        myGrid[visited.last] = "X"
        println("Length: $length")
        Day16.debugPrintGrid(myGrid)
        println()
    }

    fun solvePart2(lines: List<String>): Long {
        return 0
    }

    fun isAllowedPart1(loc: Point, visited: Set<Point>, direction: Direction, value: String): Boolean {
        if (value == "#") {
            return false
        }

        if (visited.contains(loc)) {
            return false
        }

        return when (direction) {
            Direction.Up -> value != "v"
            Direction.Right -> value != "<"
            Direction.Down -> value != "^"
            Direction.Left -> value != ">"
        }
    }

    fun isAllowedPart2(loc: Point, visited: Set<Point>, direction: Direction, value: String): Boolean {
        if (value == "#") {
            return false
        }

        if (visited.contains(loc)) {
            return false
        }

        return true
    }
}