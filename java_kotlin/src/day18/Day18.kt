package day18

import day16.Direction
import day16.Point
import java.io.File

class Day18 {
    fun part1() {
        val lines = File("inputs/day18.txt").readLines()

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

        return grid.size.toLong()
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