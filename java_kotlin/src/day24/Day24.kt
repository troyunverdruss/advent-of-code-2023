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
            if (index + 1 < rays.lastIndex) {
                rays.subList(index + 1, rays.lastIndex).map { r2 ->
                    val i = intersection(r1, r2)
                    println("$r1")
                    println("$r2")
                    println("$i")
                    println()
                    i
                }
            } else listOf()
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

        val loc = RayOrigin(locParts[0].toDouble(), locParts[1].toDouble(), locParts[2].toDouble())
        val vel = RayVector(vecParts[0].toDouble(), vecParts[1].toDouble(), vecParts[2].toDouble())

        return Ray(loc, vel)
    }

    fun intersection(r1: Ray, r2: Ray): PointDouble? {
        val dx = r2.loc.x - r1.loc.x
        val dy = r2.loc.y - r1.loc.y
        val det = r1.vel.y * r2.vel.x - r2.vel.y * r1.vel.x

        // what if det == 0 ?? lines overlap or are parallel

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

}

data class IntersectionResult(val intersect: Boolean, val intersection: PointDouble?)
data class Ray(val loc: RayOrigin, val vel: RayVector)
data class RayOrigin(val x: Double, val y: Double, val z: Double = 0.0)
data class RayVector(val x: Double, val y: Double, val z: Double = 0.0)