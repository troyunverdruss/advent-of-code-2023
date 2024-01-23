package day21

import day16.Day16.Companion.debugPrintGrid
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

    fun solvePart1Take2(lines: List<String>, targetSteps: Long): Long {
        initGlobals(lines)
        val start = grid.filter { it.value == "S" }.map { it.key }.first()

        // First explore the original range of the given map
        val exploredGridWithDistances = grid.toMutableMap()
        grid.keys.forEach {
            if (grid[it] != "#") {
                val steps = minStepsToPoint(start, it)
                exploredGridWithDistances[it] = "$steps"
            }
        }

        // If the search space is small enough, just run the old original method
        // for now
//        if (targetSteps < 2 * max.x) {
//            return solvePart1(lines, targetSteps)
//        }


        val countValidEndingLocationsOriginalGrid = exploredGridWithDistances
            .filter { it.value != "#" }
            .map { (targetSteps - it.value.toLong()) % 2 }
            .count { it == 0L }
            .toLong()
        val distanceToFarLeftCenterEdge = exploredGridWithDistances[Point(0, start.y)]!!.toLong()
        val countValidEndingLocationsInLeftGrid = countEndingPointsInArbitraryGrid(
            listOf(Point(-1, start.y)),
            listOf(distanceToFarLeftCenterEdge + 1),
            targetSteps,
            Point(-(max.x + 1), 0),
            Point(-1, max.y)
        )
        val maxDistanceFromCenterRightEdge = countLargestDistanceInGridFromPoint(Point(max.x, start.y))
        var straightLeftSum = 0L
        var steps = 0
        var currMinSteps = distanceToFarLeftCenterEdge + 1
        val gridEndingCounts = listOf(countValidEndingLocationsOriginalGrid, countValidEndingLocationsInLeftGrid)

        while (currMinSteps + maxDistanceFromCenterRightEdge < targetSteps) {
            steps += 1
            straightLeftSum += gridEndingCounts[steps % 2]
            currMinSteps += (max.x + 1)
        }
//        steps -= 1
        val newStartPoint = Point(-((max.x + 1) * steps + 1), start.y)
        val newStartDist = distance(start, newStartPoint)
        val countArbitraryGrid = countEndingPointsInArbitraryGrid(
            listOf(newStartPoint),
            listOf(newStartDist),
            targetSteps,
            Point(newStartPoint.x - (targetSteps - newStartDist), 0),
//            Point(-(targetSteps - newStartDist) + 5 , 0),
            Point(newStartPoint.x, max.y)
        )
        val leftSumPlusOriginal = straightLeftSum + countValidEndingLocationsOriginalGrid + countArbitraryGrid
        return leftSumPlusOriginal


//        // Let's go left ...
//        // All leftmost edges from the original grid:
//        val originalLeft = exploredGridWithDistances.filter { it.key.x == 0L }
//        val leftPlusOne = originalLeft.keys
//            .map { Point(it.x - (max.x + 1), it.y) }
//            .map {
//                Pair(
//                    it,
//                    minStepsToPoint(start, it).toString()
//                )
//            }
//        val leftPlusTwo = originalLeft.keys
//            .map { Point(it.x - 2 * (max.x + 1), it.y) }
//            .map {
//                Pair(
//                    it,
//                    minStepsToPoint(start, it).toString()
//                )
//            }
//
//        val leftPlusThree = originalLeft.keys
//            .map { Point(it.x - 3 * (max.x + 1), it.y) }
//            .map {
//                Pair(
//                    it,
//                    minStepsToPoint(start, it).toString()
//                )
//            }
//        val leftPlusFour = originalLeft.keys
//            .map { Point(it.x - 4 * (max.x + 1), it.y) }
//            .map {
//                Pair(
//                    it,
//                    minStepsToPoint(start, it).toString()
//                )
//            }
//        debugPrintGrid(originalLeft)
//        debugPrintGrid(leftPlusOne.toMap())
//        debugPrintGrid(leftPlusTwo.toMap())
//        debugPrintGrid(leftPlusThree.toMap())
//        debugPrintGrid(leftPlusFour.toMap())
//
//        // Compute the deltas, top to bottom
//        val n0 = originalLeft.entries.sortedBy { it.key.y }.map { Pair(it.key, it.value) }
//        val n1 = leftPlusOne.sortedBy { it.first.y }
//        val n2 = leftPlusTwo.sortedBy { it.first.y }
//        val n3 = leftPlusThree.sortedBy { it.first.y }
//        val n4 = leftPlusFour.sortedBy { it.first.y }
//
//        val delta1 = n1.mapIndexed { index, pair ->
//            Pair(pair.first.y, n1[index].second.toLong() - n0[index].second.toLong())
//        }
//        val delta2 = n2.mapIndexed { index, pair ->
//            Pair(pair.first.y, n2[index].second.toLong() - n1[index].second.toLong())
//        }
//        val delta3 = n3.mapIndexed { index, pair ->
//            Pair(pair.first.y, n3[index].second.toLong() - n2[index].second.toLong())
//        }
//        val delta4 = n4.mapIndexed { index, pair ->
//            Pair(pair.first.y, n4[index].second.toLong() - n3[index].second.toLong())
//        }
//
//
//        val lowestLeft = exploredGridWithDistances
//            .filter { it.key.x == 0L }
//            .minBy { it.value.toLong() }
//        val countLeftRepeat = countEndingPointsInArbitraryGrid(
//            listOf(lowestLeft.key + Direction.Left.point),
//            listOf(lowestLeft.value.toLong() + 1),
//            targetSteps,
//            Point(-max.x - 1, 0),
//            Point(-1, max.y)
//        )
//
//        val i = 0
////        val countValidEndingLocations
//
//
//        // The following code will totally work ... eventually ... but it's so darn slow
//        // I only wanted to use it to double check that my theory about the grids worked
//        // correctly:
////        var count = 0L
////        (start.y - targetSteps..start.y + targetSteps).forEach { y ->
////            (start.x - targetSteps..start.x + targetSteps).forEach { x ->
////                val dest = Point(x, y)
////                if (getFromRepeatingGrid(dest) != "#") {
////                    val steps = minStepsToPoint(start, dest)
////                    if (steps <= targetSteps && (targetSteps - steps) % 2 == 0L) {
////                        count += 1
////                    }
////                }
////            }
////        }
////        return count
//
//
//        // And this code will literally only work for the smallest examples, it's
//        // both too slow and doesn't really take into account the ever-expanding grid
////        return grid
////            .filter { it.value != "#" }
////            .map { minStepsToPoint(start, it.key) }
////            .filter { it <= targetSteps }
////            .map { (targetSteps - it) % 2 }
////            .count { it == 0L }
////            .toLong()

        return 0
    }

    fun distance(p1: Point, p2: Point): Long {
        return abs(p2.x - p1.x) + abs(p2.y - p1.y)
    }

    fun countLargestDistanceInGridFromPoint(
        start: Point,
    ): Long {
        val gridValues = mutableMapOf<Point, Long>()
        grid.keys.forEach {
            if (grid[it] != "#") {
                val steps = minStepsToPoint(start, it)
                gridValues[it] = steps
            }
        }
        return gridValues.maxBy { it.value }.value
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
                val nextPointLookup = Point(
                    boundLookup(max.x, loc.x),
                    boundLookup(max.y, loc.y)
                )

                return grid[nextPointLookup]!!
            } else {
                return grid[loc]
            }
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

