package day21

import day16.Day16.Companion.parseLinesToGrid
import day16.Direction
import day16.Point
import java.io.File
import java.util.LinkedList
import kotlin.math.abs

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




        // The following code will totally work ... eventually ... but it's so darn slow
        // I only wanted to use it to double check that my theory about the grids worked
        // correctly:
//        var count = 0L
//        (start.y - targetSteps..start.y + targetSteps).forEach { y ->
//            (start.x - targetSteps..start.x + targetSteps).forEach { x ->
//                val dest = Point(x, y)
//                if (getFromRepeatingGrid(dest) != "#") {
//                    val steps = minStepsToPoint(start, dest)
//                    if (steps <= targetSteps && (targetSteps - steps) % 2 == 0L) {
//                        count += 1
//                    }
//                }
//            }
//        }
//        return count


        // And this code will literally only work for the smallest examples, it's
        // both too slow and doesn't really take into account the ever-expanding grid
//        return grid
//            .filter { it.value != "#" }
//            .map { minStepsToPoint(start, it.key) }
//            .filter { it <= targetSteps }
//            .map { (targetSteps - it) % 2 }
//            .count { it == 0L }
//            .toLong()

        return 0
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
                getFromRepeatingGrid(nextPoint).let {
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


    private fun find(targetSteps: Long, loc: Point, steps: Long): Map<Point, Long> {
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
            val nextPointLookup = Point(
                boundLookup(max.x, nextPoint.x),
                boundLookup(max.y, nextPoint.y)
            )

            getFromRepeatingGrid(nextPoint)
                .let { gridValue ->
                    if (gridValue != "#") {
                        listOf(find(targetSteps, nextPoint, steps + 1))
                    } else {
                        listOf(mapOf())
                    }
                }
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

        data class MemoKey(val targetSteps: Long, val loc: Point, val steps: Long)

        fun initGlobals(lines: List<String>) {
            grid = parseLinesToGrid(lines)
            max = Point(
                grid.maxBy { it.key.x }.key.x,
                grid.maxBy { it.key.y }.key.y
            )
        }

        fun getFromRepeatingGrid(loc: Point): String {
            val nextPointLookup = Point(
                boundLookup(max.x, loc.x),
                boundLookup(max.y, loc.y)
            )

            return grid[nextPointLookup]!!
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

