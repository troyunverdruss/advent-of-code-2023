package day24

import java.io.File

class Day24 {
    fun part1(): Long {
        val lines = File("inputs/day24.txt").readLines()
        return solvePart1(lines, 200000000000000.0, 400000000000000.0)
    }

    fun solvePart1(lines: List<String>, min: Double, max: Double): Long {
        val rays = lines.map { parseLine(it) }
        val intersections = rays.flatMapIndexed { index, r1 ->

            rays.subList(index + 1, rays.size).map { r2 ->
                val i = intersection(r1, r2)
//                    println("$r1")
//                    println("$r2")
//                    println("$i")
//                    println()
                i
            }
        }.filterNotNull()
            .filter {
                it.x in min..max && it.y in min..max
            }
        return intersections.size.toLong()
    }

    fun parseLine(line: String): Ray {
        val parts = line.replace(" ", "").split("@")
        val locParts = parts[0].split(",")
        val vecParts = parts[1].split(",")

        val loc = RayStart(locParts[0].toDouble(), locParts[1].toDouble(), locParts[2].toDouble())
        val vel = RayVelocity(vecParts[0].toDouble(), vecParts[1].toDouble(), vecParts[2].toDouble())

        return Ray(loc, vel)
    }

    fun intersection(r1: Ray, r2: Ray): PointDouble? {
        val dx = r2.loc.x - r1.loc.x
        val dy = r2.loc.y - r1.loc.y
        val det = r1.vel.y * r2.vel.x - r2.vel.y * r1.vel.x

        // what if det == 0 ?? lines overlap or are parallel
//        if (det == 0.0) {
//            println("parallel or same?")
//            println(r1)
//            println(r2)
//            val tx = (r2.loc.x - r1.loc.x) / (r1.vel.x - r2.vel.x)
//            val ty = (r2.loc.y - r1.loc.y) / (r1.vel.y - r2.vel.y)
//            println(tx)
//            println(ty)
//
//            val slope1 = calcSlope(r1)
//            val slope2 = calcSlope(r2)
//            println(slope1)
//            println(slope2)
//
//            val b1 = r1.loc.y - slope1 * r1.loc.x
//            val b2 = r2.loc.y - slope2 * r2.loc.x
//            println(b1)
//            println(b2)
//
//
//            val xxxx = 0
//            println()
//        }

        val u = (r2.vel.x * dy - r2.vel.y * dx) / det
        val v = (r1.vel.x * dy - r1.vel.y * dx) / det
        if (u < 0 || v < 0) {
            return null
        }

        return PointDouble(
            r1.loc.x + r1.vel.x * u,
            r1.loc.y + r1.vel.y * u
        )
    }

    private fun calcSlope(r1: Ray): Double {
        val p1x = r1.loc.x
        val p1y = r1.loc.y
        val p2x = r1.loc.x + r1.vel.x
        val p2y = r1.loc.y + r1.vel.y

        return (p2y - p1y) / (p2x - p1x)
    }

}

data class IntersectionResult(val intersect: Boolean, val intersection: PointDouble?)
data class Ray(val loc: RayStart, val vel: RayVelocity) {

}

data class RayStart(val x: Double, val y: Double, val z: Double = 0.0) {
    override fun toString(): String {
        return "RayOrigin(x=${x.toLong()}, y=${y.toLong()})"
    }
}

data class RayVelocity(val x: Double, val y: Double, val z: Double = 0.0)