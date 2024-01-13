package day17

import day16.Day16
import day16.Direction
import day16.Point
import java.io.File
import java.util.LinkedList
import java.util.PriorityQueue
import java.util.SortedSet
import kotlin.math.min

class Day17 {

    fun part1(): Long {
        val lines = File("inputs/day17.txt").readLines()
        val rawGrid = Day16.parseLinesToGrid(lines)
        val grid = stringGridToDigits(rawGrid)
        return solvePart1(grid)
    }

    fun solvePart1(grid: Map<Point, Int>): Long {
        val path1 = findLowestHeatLossPath(
            grid,
            State(Point(1, 0), 1, Direction.Right, 0)
        )
        val path2 = findLowestHeatLossPath(
            grid,
            State(Point(0, 1), 1, Direction.Down, 0)
        )

        return min(path1, path2).toLong()
    }

    fun findLowestHeatLossPath(grid: Map<Point, Int>, state: State): Int {
        val maxX = grid.maxBy { e -> e.key.x }.key.x
        val maxY = grid.maxBy { e -> e.key.y }.key.y

        // TODO (debug!!)
//        val dest = Point(5,0)
        val dest = Point(maxX, maxY)

        val visited = HashMap<VisitedKey, Int>()
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
            visited[VisitedKey(currState.loc, currState.fwdSteps, currState.dir)] = totalHeatLoss

            if (totalHeatLoss >= lowestHeatLoss) {
                continue
            }

            if (currState.loc == dest) {
                lowestHeatLoss = min(lowestHeatLoss, totalHeatLoss)
                continue
            }

            val fwdSteps = currState.fwdSteps + 1
            val possibleNextStates = findNextSteps(fwdSteps, currState, totalHeatLoss)

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
        totalHeatLoss: Int
    ): MutableList<State> {
        val possibleNextStates = mutableListOf<State>()
        if (fwdSteps <= 3) {
            possibleNextStates.add(
                State(
                    currState.loc + currState.dir.point,
                    fwdSteps,
                    currState.dir,
                    totalHeatLoss
                )
            )
        }
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