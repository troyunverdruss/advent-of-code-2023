package day22

import day16.Point
import java.io.File

class Day22 {
    fun part1(): Int {
        val lines = File("inputs/day22.txt").readLines()
        val result = solvePart1(lines)
        return result
    }

    fun solvePart1(lines: List<String>): Int {
        val bricks = parseBricks(lines)
        val maxX = bricks.maxBy { it.end.x }.end.x
        val maxY = bricks.maxBy { it.end.y }.end.y
        val ground = Brick(Point(0, 0, 0), Point(maxX, maxY, 0))

        val settledBricks = mutableListOf(ground)
        bricks
            .sortedBy { it.start.z }
            .forEach {
                var testBrick = it
                var keepTesting = true

                while (keepTesting) {
                    val tmpTestBrick = testBrick.dropDownBy(1)

                    val supportingBricks = intersectsAny(settledBricks, tmpTestBrick)
                    if (supportingBricks.isNotEmpty()) {
                        keepTesting = false
                        testBrick.supportingBricks = supportingBricks
                        settledBricks.add(testBrick)
                    }

                    testBrick = tmpTestBrick
                }
            }
        settledBricks.remove(ground)
        val finalSettledBricks = settledBricks.toList()

        // Find the blocks with more than 1 right below it
        var supported = mutableSetOf<Brick>()
        val allBricksSupportingOthers = finalSettledBricks.flatMap { it.supportingBricks ?: listOf() }.toSet()

        for (b in finalSettledBricks) {
            val supportingBricks = b.supportingBricks
            if (supportingBricks != null && supportingBricks.size > 1) {
                supported.addAll(supportingBricks)
            }
            if (!allBricksSupportingOthers.contains(b)) {
                supported.add(b)
            }
        }

        val bricksThatMustStay = finalSettledBricks
            .filter { it.supportingBricks?.size == 1 }
            .flatMap { it.supportingBricks ?: listOf() }
            .toSet()

        return (supported - bricksThatMustStay).size
    }

    private fun intersectsAny(bricks: List<Brick>, test: Brick): List<Brick> {
        val supportingBricks = mutableListOf<Brick>()
        for (b in bricks.reversed()) {
            if (b.intersects(test)) {
                supportingBricks.add(b)
            }
        }
        return supportingBricks
    }

    private fun parseBricks(lines: List<String>): List<Brick> {
        return lines
            .map { it.split("~", limit = 2) }
            .map { it.map { it.split(",", limit = 3) } }
            .map {
                Brick(
                    Point(it[0][0].toLong(), it[0][1].toLong(), it[0][2].toLong()),
                    Point(it[1][0].toLong(), it[1][1].toLong(), it[1][2].toLong()),
                )
            }
    }

    fun solvePart2(lines: List<String>): Int {
        TODO("Not yet implemented")
    }
}

data class Brick(val start: Point, val end: Point) {
    var supportingBricks: List<Brick>? = null
    fun intersects(other: Brick): Boolean {
        val zIntersects = (this.start.z..this.end.z).intersect(other.start.z..other.end.z).isNotEmpty()
        if (!zIntersects) return false

        val yIntersects = (this.start.y..this.end.y).intersect(other.start.y..other.end.y).isNotEmpty()
        if (!yIntersects) return false

        val xIntersects = (this.start.x..this.end.x).intersect(other.start.x..other.end.x).isNotEmpty()
        if (!xIntersects) return false

        return true
    }

    fun dropDownBy(n: Long): Brick {
        return copy(
            start = start.copy(z = start.z - n),
            end = end.copy(z = end.z - n),
        )
    }
}
