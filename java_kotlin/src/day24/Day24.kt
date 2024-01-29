package day24

import java.io.File

class Day24 {
    fun part1(): Long {
        val lines = File("inputs/day24.txt").readLines()
        return solvePart1(lines, 200000000000000.0, 400000000000000.0)
    }

    fun solvePart1(lines: List<String>, min: Double, max: Double): Long {
        val equations = lines.map(Equation::parseLine)

        return equations.flatMapIndexed { index, eq1 ->
            equations.subList(index, equations.lastIndex).map { eq2 ->
                // Calculate x intersect point
                if (eq1.m != eq2.m) {
                    val xIntersect = (eq2.b - eq1.b) / (eq1.m - eq2.m)
                    val yIntersect = (eq1.m * xIntersect) + eq1.b
                    val yIntersect2 = (eq2.m * xIntersect) + eq2.b
//                    println("$yIntersect == $yIntersect2")
//                    assert(yIntersect == yIntersect2)

//                    println(eq1)
//                    println(eq2)
//                    println("$xIntersect, $yIntersect")

                    val intersectionInWindow = isIntersectionInWindow(xIntersect, yIntersect, min, max)
                    val intersectionInFuture = isIntersectionInFuture(xIntersect, yIntersect, eq1, eq2)

                    intersectionInWindow && intersectionInFuture
                } else {
//                    if (eq1.vec != eq2.vec) {
                        println("Opposites:")
                        println(eq1)
                        println(eq2)
                        val i = 0

                        if ((eq1.vec.x/eq1.vec.y) == (eq2.vec.x / eq2.vec.y)) {
                            // Will collide, but within the window?
                            val xIntersect = (eq2.b - eq1.b) / (eq1.m - eq2.m)
                            val yIntersect = (eq1.m * xIntersect) + eq1.b
                            val intersectionInWindow = isIntersectionInWindow(xIntersect, yIntersect, min, max)
                            val intersectionInFuture = isIntersectionInFuture(xIntersect, yIntersect, eq1, eq2)

                            intersectionInWindow && intersectionInFuture
                        } else {
                            false
//                        }
//                    } else {
                        false
                    }
                }
            }
        }.count { it }.toLong()
    }

    private fun isIntersectionInFuture(xIntersect: Double, yIntersect: Double, eq1: Equation, eq2: Equation): Boolean {
        // Is eq1 possible
        val isPossibleEq1x = if (xIntersect >= eq1.loc.x) {
            eq1.vec.x >= 0
        } else {
            eq1.vec.x < 0
        }
        val isPossibleEq2x = if (xIntersect >= eq2.loc.x) {
            eq2.vec.x >= 0
        } else {
            eq2.vec.x < 0
        }
        val isPossibleEq1y = if (yIntersect >= eq1.loc.y) {
            eq1.vec.y >= 0
        } else {
            eq1.vec.y < 0
        }
        val isPossibleEq2y = if (yIntersect >= eq2.loc.y) {
            eq2.vec.y >= 0
        } else {
            eq2.vec.y < 0
        }

        return isPossibleEq1x && isPossibleEq2x && isPossibleEq1y && isPossibleEq2y
    }

    private fun isIntersectionInWindow(xIntersect: Double, yIntersect: Double, min: Double, max: Double) =
        xIntersect in min..max && yIntersect in min..max
}

data class Equation(val m: Double, val b: Double, val loc: PointDouble, val vec: PointDouble) {
    companion object {
        fun parseLine(line: String): Equation {
            val parts = line.replace(" ", "").split("@")
            val locParts = parts[0].split(",")
            val vecParts = parts[1].split(",")

            val loc = PointDouble(locParts[0].toDouble(), locParts[1].toDouble(), locParts[2].toDouble())
            val vec = PointDouble(vecParts[0].toDouble(), vecParts[1].toDouble(), vecParts[2].toDouble())
            val m = normalizeSlopeData(vec)
            assert(m.x == 1.0)
            val b = loc.x * (-1F * m.y) + loc.y
            return Equation(m.y, b, loc, vec)
        }

        fun normalizeSlopeData(vec: PointDouble): PointDouble {
            var x = vec.x
            var y = vec.y
            if (x < 0) {
                x *= -1
                y *= -1
            }
            if (x > 1) {
                y /= x
                x /= x
            }
            return PointDouble(x, y)
        }
    }

    override fun toString(): String {
        return "$loc @ $vec"
    }
}

data class PointDouble(val x: Double, val y: Double, val z: Double = 0.0)