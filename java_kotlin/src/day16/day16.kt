package day16

import java.io.File


class Day16 {

    fun part1() : Long {
        val lines = File("inputs/day16.txt").readLines()
        val grid = parseLinesToGrid(lines)

        return solvePart1(grid)
    }

    fun solvePart1(grid: Map<Point, String>): Long {
        val energizerData  = HashMap<Point, EnergizerData>()
        traceLight(
            Point(0,0),
            Direction.Right,
            grid,
            energizerData
        )

        return energizerData.size.toLong()
    }

    fun parseLinesToGrid(lines: List<String>): Map<Point, String> {
        val grid = HashMap<Point, String>()
        var y = 0
        lines.forEach { line ->
            var x = 0
            line.toCharArray().forEach { c ->
//            line.chars().forEach { c: Char ->
                grid[Point(x, y)] = c.toString()
                x += 1
            }
            y += 1
        }

        return grid
    }

    fun traceLight(
        start: Point,
        direction: Direction,
        grid: Map<Point, String>,
        energizerData: HashMap<Point, EnergizerData>
    ) {
        var next = start
        var nextDirection = direction
        while (grid.containsKey(next)) {
            val data = energizerData.getOrDefault(next, EnergizerData(HashSet()))
            if (data.directions.contains(nextDirection)) {
                return
            }
            data.directions.add(nextDirection)
            energizerData[next] = data
            val value = grid[next] ?: throw RuntimeException("point not found in the grid???!!")
            when (value) {
                "." -> next += nextDirection.point
                "\\" -> {
                    nextDirection = when (nextDirection) {
                        Direction.Up -> Direction.Left
                        Direction.Right -> Direction.Down
                        Direction.Down -> Direction.Right
                        Direction.Left -> Direction.Up
                    }
                    next += nextDirection.point
                }

                "/" -> {
                    nextDirection = when (nextDirection) {
                        Direction.Up -> Direction.Right
                        Direction.Right -> Direction.Up
                        Direction.Down -> Direction.Left
                        Direction.Left -> Direction.Down
                    }
                    next += nextDirection.point
                }

                "-" -> {
                    when (nextDirection) {
                        Direction.Right, Direction.Left -> next += nextDirection.point
                        Direction.Up, Direction.Down -> {
                            traceLight(next, Direction.Left, grid, energizerData)
                            traceLight(next, Direction.Right, grid, energizerData)
                        }
                    }
                }

                "|"               -> {
                    when (nextDirection) {
                        Direction.Up, Direction.Down -> next += nextDirection.point
                        Direction.Right, Direction.Left -> {
                            traceLight(next, Direction.Up, grid, energizerData)
                            traceLight(next, Direction.Down, grid, energizerData)
                        }
                    }
                }
                else -> throw RuntimeException("Unknown grid char")
            }
        }
    }
}

data class Point(val x: Int, val y: Int) {
    operator fun plus(point: Point): Point {
        return Point(this.x + point.x, this.y + point.y)
    }
}

data class EnergizerData(val directions: HashSet<Direction>)

enum class Direction(val point: Point) {
    Up(Point(0, -1)),
    Right(Point(1, 0)),
    Down(Point(0, 1)),
    Left(Point(-1, 0)),
}