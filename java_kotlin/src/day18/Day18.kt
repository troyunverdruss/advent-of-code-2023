package day18

import day16.Direction
import day16.Point
import java.io.File
import java.util.*
import kotlin.math.absoluteValue

class Day18 {
    fun part1(): Long {
        val lines = File("inputs/day18.txt").readLines()
        return solvePart1DrawAndFill(lines)
    }

    fun part2(): Long {
        val lines = File("inputs/day18.txt").readLines()
        return solvePart2(lines)
    }

    fun solvePart1DrawAndFill(lines: List<String>): Long {
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

    // Using the "shoelace formula"
    // https://en.wikipedia.org/wiki/Shoelace_formula
    fun calculatePolygonCoverage(instructions: List<Instruction>): Long {
        val vertices = findVertices(instructions)
        // abs(
        // (x1*y2 - y1*x2) + (...) ... + (xn*y1 - yn*x1)
        // ) / 2
        val sum1 = vertices
            .windowed(2)
            .sumOf { points ->
                val p1 = points[0]
                val p2 = points[1]
                (p1.x * p2.y - p1.y * p2.x).toLong()
            }
        // Add wrap around
        val first = vertices.first()
        val last = vertices.last()
        val sum2 = sum1 + (last.x * first.y - last.y * first.x)

        val totalArea = sum2.absoluteValue / 2

        // Now add in the additional perimeter that was excluded before:
        val perimeter = instructions
            .filter { listOf(Direction.Up, Direction.Right).contains(it.direction) }
            .map { it.steps }
            .sum()

        return totalArea + perimeter + 1
    }

    fun findVertices(instructions: List<Instruction>): MutableList<Point> {
        val list = mutableListOf<Point>()

        var currentLoc = Point(0, 0)
//        list.add(currentLoc)
        instructions.forEach { instruction ->
            currentLoc += Point(
                instruction.steps * instruction.direction.point.x,
                instruction.steps * instruction.direction.point.y
            )
            list.add(currentLoc)
        }

        println("Debug coordinates")
        println("xs = ${list.map { it.x }}")
        println("ys = ${list.map { it.y }}")

        return list
    }

    fun solvePart2(lines: List<String>): Long {
        val instructions = lines.map { Instruction.parse(it).toTrueInstruction() }.toList()
        return calculatePolygonCoverage(instructions)
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
    fun toTrueInstruction(): Instruction {
        val steps = this.hexColor.substring(0..4).toInt(radix = 16)
        val direction = when (this.hexColor.substring(5..5)) {
            "0" -> Direction.Right
            "1" -> Direction.Down
            "2" -> Direction.Left
            "3" -> Direction.Up
            else -> throw RuntimeException("Unknown direction code")
        }

        return Instruction(direction, steps, "")
    }

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