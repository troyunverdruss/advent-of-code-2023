package day21

import day16.Day16.Companion.parseLinesToGrid
import day16.Direction
import day16.Point
import java.io.File
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.min

class Day21 {
    fun part1(): Long {
        val lines = File("inputs/day21.txt").readLines()
        return solvePart1(lines, 64)
    }

    fun solvePart1(lines: List<String>, targetSteps: Long): Long {
        initGlobals(lines)
        val start = grid.filter { it.value == "S" }.map { it.key }.first()
        return find(targetSteps, start, 0).size.toLong()
    }

    fun solvePart2(lines: List<String>, targetSteps: Long): Long {
        initGlobals(lines)
        val start = grid.filter { it.value == "S" }.map { it.key }.first()

        // First explore the original range of the given map
        val ogGridWithDistances = grid.toMutableMap()
        grid.keys.forEach {
            if (grid[it] != "#") {
                val steps = minStepsToPoint(start, it)
                ogGridWithDistances[it] = "$steps"
            }
        }

        // This is how many ending points are in the original grid
        // and since they alternate, we'll need this going forward
        val ogGridValidEndingCount = ogGridWithDistances
            .filter { it.value != "#" }
            .map { (targetSteps - it.value.toLong()) % 2 }
            .count { it == 0L }
            .toLong()

        // This happens to be the grid adjacent leftwards but should theoretically
        // be the same for any neighboring grid
        // This is the other value to alternate with
        val distanceToFarLeftCenterEdge = ogGridWithDistances[Point(0, start.y)]!!.toLong()
        val distanceToFarRightCenterEdge = ogGridWithDistances[Point(max.x, start.y)]!!.toLong()
        val distanceToFarBottomCenterEdge = ogGridWithDistances[Point(start.x, max.y)]!!.toLong()
        val distanceToFarTopCenterEdge = ogGridWithDistances[Point(start.x, 0)]!!.toLong()

        val neighborGridValidEndingCount = countEndingPointsInArbitraryGrid(
            listOf(Point(-1, start.y)),
            listOf(distanceToFarLeftCenterEdge + 1),
            targetSteps,
            Point(-(max.x + 1), 0),
            Point(-1, max.y)
        )

        val leftOnlyTotalCount = count(
            Point(start.x - distanceToFarLeftCenterEdge - 1, start.y),
            distanceToFarLeftCenterEdge + 1,
            targetSteps,
            ogGridValidEndingCount,
            neighborGridValidEndingCount,
            Direction.Left,
            0,
            max.y
        )

        val rightOnlyTotalCount = count(
            Point(start.x + distanceToFarRightCenterEdge + 1, start.y),
            distanceToFarRightCenterEdge + 1,
            targetSteps,
            ogGridValidEndingCount,
            neighborGridValidEndingCount,
            Direction.Right,
            0,
            max.y
        )

        val downWithSideBranchesCount = count(
            Point(start.x, start.y + distanceToFarBottomCenterEdge + 1),
            distanceToFarBottomCenterEdge + 1,
            targetSteps,
            ogGridValidEndingCount,
            neighborGridValidEndingCount,
            Direction.Down,
            0,
            max.x
        )

        val upWithSideBranchesCount = count(
            Point(start.x, start.y - distanceToFarTopCenterEdge - 1),
            distanceToFarTopCenterEdge + 1,
            targetSteps,
            ogGridValidEndingCount,
            neighborGridValidEndingCount,
            Direction.Up,
            0,
            max.x
        )
        val resultSum = leftOnlyTotalCount +
                rightOnlyTotalCount +
                upWithSideBranchesCount +
                downWithSideBranchesCount +
                ogGridValidEndingCount

        return resultSum
    }

    private fun count(
        start: Point,
        stepsToStart: Long,
        targetSteps: Long,
        ogCountA: Long,
        countB: Long,
        dir: Direction,
        windowMin: Long,
        windowMax: Long
    ): Long {
        val maxDistStart = when (dir) {
            Direction.Left -> Point(max.x, start.y)
            Direction.Right -> Point(0, start.y)
            Direction.Up -> Point(start.x, max.y)
            Direction.Down -> Point(start.x, 0)
        }
        val maxPossibleDist = countLargestDistanceInGridFromPoint(boundLookup(maxDistStart))

        var steps = 0L
        var gridSteps = 0

        var currMinSteps = stepsToStart
        val gridEndingCounts = listOf(ogCountA, countB)

        while (currMinSteps + maxPossibleDist < targetSteps) {

            gridSteps += 1
            val newSteps = gridEndingCounts[gridSteps % 2]
            currMinSteps += when (dir) {
                Direction.Left, Direction.Right -> (max.x + 1)
                Direction.Up, Direction.Down -> (max.y + 1)
            }

            // Each step of the way we need to branch off and count
            if (dir == Direction.Up || dir == Direction.Down) {

                val branchStart = when (dir) {
                    Direction.Up -> Point(start.x, start.y - ((max.y + 1) * (gridSteps - 1)))
                    Direction.Down -> Point(start.x, start.y + ((max.y + 1) * (gridSteps - 1)))
                    else -> throw NotImplementedError()
                }
                val branchSteps = addBranches(
                    dir,
                    gridSteps,
                    branchStart,
                    windowMin,
                    targetSteps,
                    gridEndingCounts,
                    windowMax,
                    stepsToStart + distance(start, branchStart)
                )
                steps += branchSteps
            }

            steps += newSteps
        }

        val newStartPoint = computeNewStartPoint(dir, gridSteps, start)
        val newStartDist = distance(start, newStartPoint) + stepsToStart

        val finalGridMin = when (dir) {
            Direction.Left -> Point(newStartPoint.x - (targetSteps - newStartDist), windowMin)
            Direction.Right -> Point(newStartPoint.x, windowMin)
            Direction.Up -> Point(windowMin, newStartPoint.y - (targetSteps - newStartDist))
            Direction.Down -> Point(windowMin, newStartPoint.y)
        }
        val finalGridMax = when (dir) {
            Direction.Left -> Point(newStartPoint.x, windowMax)
            Direction.Right -> Point(newStartPoint.x + (targetSteps - newStartDist), windowMax)
            Direction.Up -> Point(windowMax, newStartPoint.y)
            Direction.Down -> Point(windowMax, newStartPoint.y + (targetSteps - newStartDist))
        }

        val remainderCount = countEndingPointsInArbitraryGrid(
            listOf(newStartPoint),
            listOf(newStartDist),
            targetSteps,
            finalGridMin,
            finalGridMax
        )

        // Even at the far end of our reach we need to still branch sideways since
        // there might be a few stragglers
        if (dir == Direction.Up || dir == Direction.Down) {
            val branchStart = when (dir) {
                Direction.Up -> Point(start.x, start.y - ((max.y + 1) * (gridSteps)))
                Direction.Down -> Point(start.x, start.y + ((max.y + 1) * (gridSteps)))
                else -> throw NotImplementedError()
            }
            val remainderBranchesCount = addBranches(
                dir,
                gridSteps+1,
                branchStart,
                windowMin,
                targetSteps,
                gridEndingCounts,
                windowMax,
                stepsToStart + distance(start, branchStart)
            )
            steps += remainderBranchesCount
        }

        val totalCount = steps + remainderCount

        return totalCount
    }

    private fun addBranches(
        dir: Direction,
        gridSteps: Int,
        start: Point,
        windowMin: Long,
        targetSteps: Long,
        gridEndingCounts: List<Long>,
        windowMax: Long,
        stepsToStart: Long
    ): Long {
        var leftSteps = 0L
        var rightSteps = 0L
        if (dir == Direction.Up || dir == Direction.Down) {
            val newMin = when (dir) {
                Direction.Up -> start.y - max.y
                Direction.Down -> start.y
                else -> throw NotImplementedError()
            }
            val newMax = when (dir) {
                Direction.Up -> start.y
                Direction.Down -> start.y + max.y
                else -> throw NotImplementedError()
            }

            val newCenterStartPoint = computeNewStartPoint(dir, gridSteps - 1, start)

            val newLeftStart = Point(windowMin - 1, newCenterStartPoint.y)
            val distToLeftStart = distance(start, newLeftStart) + stepsToStart
            leftSteps = count(
                newLeftStart,
                distToLeftStart,
                targetSteps,
                gridEndingCounts[gridSteps % 2],
                gridEndingCounts[(gridSteps + 1) % 2],
                Direction.Left,
                newMin,
                newMax

            )
            val newRightStart = Point(windowMax + 1, newCenterStartPoint.y)
            val distToRightStart = distance(start, newRightStart) + stepsToStart
            rightSteps = count(
                newRightStart,
                distToRightStart,
                targetSteps,
                gridEndingCounts[gridSteps % 2],
                gridEndingCounts[(gridSteps + 1) % 2],
                Direction.Right,
                newMin,
                newMax
            )
        }
        return leftSteps + rightSteps
    }

    private fun computeNewStartPoint(dir: Direction, gridSteps: Int, start: Point) = when (dir) {
        Direction.Left -> Point(-((max.x + 1) * gridSteps + 1), start.y)
        Direction.Right -> Point(((max.x + 1) * (gridSteps + 1)), start.y)
        Direction.Up -> Point(start.x, -((max.y + 1) * gridSteps + 1))
        Direction.Down -> Point(start.x, ((max.y + 1) * (gridSteps + 1)))
    }

    fun distance(p1: Point, p2: Point): Long {
        return abs(p2.x - p1.x) + abs(p2.y - p1.y)
    }

    fun countLargestDistanceInGridFromPoint(
        start: Point,
    ): Long {
        if (largestDistanceInGridMemo.containsKey(start)) {
            return largestDistanceInGridMemo[start]!!
        }

        val gridValues = mutableMapOf<Point, Long>()
        grid.keys.forEach {
            if (grid[it] != "#") {
                val steps = minStepsToPoint(start, it)
                gridValues[it] = steps
            }
        }
        val result = gridValues.maxBy { it.value }.value
        largestDistanceInGridMemo[start] = result
        return result
    }

    fun countEndingPointsInArbitraryGrid(
        starts: List<Point>,
        startingStepCounts: List<Long>,
        targetSteps: Long,
        min: Point,
        max: Point
    ): Long {
        assert(starts.size == startingStepCounts.size)
        val gridToExplore = mutableMapOf<Point, String>()
        val gridValues = mutableMapOf<Point, Long>()

        (min.x..max.x).forEach { x ->
            (min.y..max.y).forEach { y ->
                gridToExplore[Point(x, y)] =
                    getFromRepeatingGrid(Point(x, y)) ?: throw RuntimeException("Need to search infinite grid")
            }
        }

        starts.forEachIndexed { index, start ->
            gridToExplore.keys.forEach {
                if (gridToExplore[it] != "#") {
                    val steps = minStepsToPoint(start, it)
                    val currVal = gridValues[it] ?: Long.MAX_VALUE
                    gridValues[it] = min(steps + startingStepCounts[index], currVal)
                } else {
//                    gridValues[it] = -1L
                }
            }
        }


//        debugPrintGrid(gridToExplore)
//        debugPrintGrid(
//            gridValues
//
//                .map { Pair(it.key, it.value.toString()) }.toMap()
//        )


        val stepsInRange = gridValues
            .filter { it.value <= targetSteps }
        val stepsThatShouldBeViable = stepsInRange
            .filter { (targetSteps - it.value) % 2 == 0L }
        return stepsInRange
            .map { (targetSteps - it.value) % 2 }
            .count { it == 0L }
            .toLong()
    }


    fun minStepsToPoint(start: Point, dest: Point): Long {
        if (start == dest) {
            return 0
        }
        val visited = HashSet<Node>()
        val toVisit = LinkedList<Node>()
        val toVisitSet = HashSet<Node>()
        toVisit.add(Node(start))
        toVisitSet.add(Node(start))
        while (toVisit.isNotEmpty()) {
            val currNode = toVisit.poll()!!
            toVisitSet.remove(currNode)

            if (currNode.loc == dest) {
                var steps = 0
                var prevNode: Node? = currNode.prevNode
                while (prevNode != null) {
                    steps += 1
                    prevNode = prevNode.prevNode
                }
                return steps.toLong()
            }
            visited.add(currNode)
            Direction.entries.forEach { dir ->
                val nextPoint = currNode.loc + dir.point
                getFromRepeatingGrid(nextPoint)?.let {
                    if (it != "#") {
                        val toVisitNode = Node(nextPoint)
                        toVisitNode.prevNode = currNode
                        if (!toVisitSet.contains(toVisitNode)) {
                            toVisit.add(toVisitNode)
                            toVisitSet.add(toVisitNode)
                        }
                    }
                }
            }
        }
        throw RuntimeException("Could not find path to node")
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

        val result = Direction.entries.flatMap { dir ->
            val nextPoint = loc + dir.point

//            if (grid.keys.contains(nextPoint)) {
            getFromRepeatingGrid(nextPoint).let { gridValue ->
//                if (gridValue == null ) {
//                    throw RuntimeException("Require inifinite grid")
//                }
                if (gridValue != "#" && gridValue != null) {
                    listOf(find(targetSteps, nextPoint, steps + 1))
                } else {
                    listOf(mapOf())
                }
            }
//            } else {
//                listOf(mapOf())
//            }


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


    companion object {
        var grid: Map<Point, String> = mapOf()
        var max: Point = Point(0, 0)
        val memoMap: MutableMap<MemoKey, Map<Point, Long>> = mutableMapOf()
        var searchInfiniteGrid = true
        val largestDistanceInGridMemo = mutableMapOf<Point, Long>()

        data class MemoKey(val targetSteps: Long, val loc: Point, val steps: Long)

        fun initGlobals(lines: List<String>) {
            memoMap.clear()
            grid = parseLinesToGrid(lines)
            max = Point(
                grid.maxBy { it.key.x }.key.x,
                grid.maxBy { it.key.y }.key.y
            )
        }

        fun getFromRepeatingGrid(loc: Point): String? {
            return if (searchInfiniteGrid) {
                val nextPointLookup = boundLookup(loc)

                return grid[nextPointLookup]!!
            } else {
                return grid[loc]
            }
        }

        private fun boundLookup(loc: Point): Point {
            return Point(
                boundLookup(max.x, loc.x),
                boundLookup(max.y, loc.y)
            )
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
    }
}

data class Node(val loc: Point) {
    var prevNode: Node? = null
}

