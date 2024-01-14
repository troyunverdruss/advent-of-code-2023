package day17

import day16.Day16
import day16.Direction
import day16.Point
import java.io.File
import java.util.*
import kotlin.math.min

class Day17 {

    fun part1(): Long {
        val lines = File("inputs/day17.txt").readLines()
        val rawGrid = Day16.parseLinesToGrid(lines)
        val grid = stringGridToDigits(rawGrid)
        return solvePart1(grid)
    }

    fun part2(): Long {
        val lines = File("inputs/day17.txt").readLines()
        val rawGrid = Day16.parseLinesToGrid(lines)
        val grid = stringGridToDigits(rawGrid)
        return solvePart2(grid)
    }

    fun solvePart1(grid: Map<Point, Int>): Long {
        val path1 = findLowestHeatLossPath(
            grid,
            State(Point(1, 0), 1, Direction.Right, 0),
            3,
            0
        )
        val path2 = findLowestHeatLossPath(
            grid,
            State(Point(0, 1), 1, Direction.Down, 0),
            3,
            0
        )

        return min(path1, path2).toLong()
    }

    fun solvePart2(grid: Map<Point, Int>): Long {
        val path1 = findLowestHeatLossPath(
            grid,
            State(Point(1, 0), 1, Direction.Right, 0),
            10,
            4
        )
        val path2 = findLowestHeatLossPath(
            grid,
            State(Point(0, 1), 1, Direction.Down, 0),
            10,
            4
        )

        return min(path1, path2).toLong()
    }

    private fun findLowestHeatLossPath(
        grid: Map<Point, Int>,
        state: State,
        maxFwdSteps: Int,
        minFwdSteps: Int
    ): Int {
        val maxX = grid.maxBy { e -> e.key.x }.key.x
        val maxY = grid.maxBy { e -> e.key.y }.key.y

        val dest = Point(maxX, maxY)

        val visited = HashSet<VisitedKey>()
        val toVisit = PriorityQueue<State>(compareBy { v -> v.heatLoss })
        val toVisitSet = HashSet<State>()
        toVisit.add(state)
        toVisitSet.add(state)
        var lowestHeatLoss = Int.MAX_VALUE

        while (toVisit.isNotEmpty()) {
            val currState = toVisit.poll()
            toVisitSet.remove(currState)

            val heatLoss = grid.get(currState.loc) ?: throw RuntimeException("outside of grid?")
            val totalHeatLoss = currState.heatLoss + heatLoss
            visited.add(VisitedKey(currState.loc, currState.fwdSteps, currState.dir))

            if (currState.loc == dest) {
                lowestHeatLoss = min(lowestHeatLoss, totalHeatLoss)
                continue
            }

            val fwdSteps = currState.fwdSteps + 1
            val possibleNextStates = findNextSteps(
                fwdSteps,
                currState,
                totalHeatLoss,
                maxFwdSteps,
                minFwdSteps
            )

            possibleNextStates.forEach { ps ->
                val visitedKey = ps.toVisitedKey()
                if (
                    grid.containsKey(ps.loc)
                    && (!visited.contains(visitedKey))
                    && !toVisitSet.contains(ps)
                ) {
                    toVisit.add(ps)
                    toVisitSet.add(ps)
                }
            }
        }

        return lowestHeatLoss
    }

    private fun findNextSteps(
        fwdSteps: Int,
        currState: State,
        totalHeatLoss: Int,
        maxFwdSteps: Int,
        minFwdSteps: Int
    ): MutableList<State> {
        val possibleNextStates = mutableListOf<State>()
        if (fwdSteps <= maxFwdSteps) {
            possibleNextStates.add(
                State(
                    currState.loc + currState.dir.point,
                    fwdSteps,
                    currState.dir,
                    totalHeatLoss
                )
            )
        }
        if (fwdSteps > minFwdSteps) {
            val turns = getTurns(currState.dir)
            turns.forEach { turn ->
                possibleNextStates.add(
                    State(
                        currState.loc + turn.point,
                        1,
                        turn,
                        totalHeatLoss
                    )
                )
            }
        }

        return possibleNextStates
    }

    private fun getTurns(dir: Direction): List<Direction> {
        return when (dir) {
            Direction.Up -> listOf(Direction.Left, Direction.Right)
            Direction.Right -> listOf(Direction.Up, Direction.Down)
            Direction.Down -> listOf(Direction.Left, Direction.Right)
            Direction.Left -> listOf(Direction.Up, Direction.Down)
        }
    }

    data class State(val loc: Point, val fwdSteps: Int, val dir: Direction, val heatLoss: Int) {
        fun toVisitedKey(): VisitedKey {
            return VisitedKey(this.loc, this.fwdSteps, this.dir)
        }
    }

    data class VisitedKey(val loc: Point, val fwdSteps: Int, val dir: Direction)

    companion object {

        fun stringGridToDigits(grid: Map<Point, String>): Map<Point, Int> {
            return grid.map { e ->
                Pair(e.key, e.value.toInt())
            }.toMap()
        }
    }
}