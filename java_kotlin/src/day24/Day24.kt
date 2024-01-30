package day24

import io.ksmt.KContext
import io.ksmt.expr.rewrite.simplify.toRealValue
import io.ksmt.solver.KSolverStatus
import io.ksmt.solver.z3.KZ3Solver
import io.ksmt.utils.getValue
import it.unimi.dsi.fastutil.ints.IntSet
import java.io.File
import kotlin.time.Duration.Companion.seconds

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

    fun solvePart2(lines: List<String>): Long {
        val rays = lines.map { parseLine(it) }

        var ansX = 0L
        var ansY = 0L
        var ansZ = 0L

        val ctx = KContext()
        with(ctx) {
            KZ3Solver(this).use { solver ->

                // t must be positive
//                val t by intSort
//                val tPositiveConstraint = t gt 0.expr
//                solver.assert(tPositiveConstraint)

                // setup the perfect stone
                val sx by intSort
                val sy by intSort
                val sz by intSort
                val svx by intSort
                val svy by intSort
                val svz by intSort

//                val sxConstraint = sx + (t * svx)
//                val syConstraint = sy + (t * svy)
//                val szConstraint = sz + (t * svz)

                var rayId = 0
                for (ray in rays.subList(0, 3)) {
                    rayId += 1
                    println(rayId)
                    val t = mkConst("t$rayId", intSort)
//                    val t by intSort
//                    val t = mk()
                    val tPositiveConstraint = t ge 0.expr
                    solver.assert(tPositiveConstraint)

                    val sxConstraint = sx + (t * svx)
                    val syConstraint = sy + (t * svy)
                    val szConstraint = sz + (t * svz)

//                    val rt by intSort
                    val rxConstraint = ray.loc.x.toLong().expr + (t * ray.vel.x.toLong().expr)
                    val ryConstraint = ray.loc.y.toLong().expr + (t * ray.vel.y.toLong().expr)
                    val rzConstraint = ray.loc.z.toLong().expr + (t * ray.vel.z.toLong().expr)
                    solver.assertAndTrack(sxConstraint eq rxConstraint)
                    solver.assertAndTrack(syConstraint eq ryConstraint)
                    solver.assertAndTrack(szConstraint eq rzConstraint)

                    val satisfiability = solver.check()
                    println(satisfiability) // SAT
                }
                val model = solver.model()

                ansX = model.eval(sx).toRealValue()?.let {
                    it.numerator.toLong() / it.denominator.toLong()
                } ?: throw RuntimeException("no sx found")
                ansY = model.eval(sy).toRealValue()?.let {
                    it.numerator.toLong() / it.denominator.toLong()
                } ?: throw RuntimeException("no sy found")
                ansZ = model.eval(sz).toRealValue()?.let {
                    it.numerator.toLong() / it.denominator.toLong()
                } ?: throw RuntimeException("no sz found")
            }
        }

        // position x + y + z
        return ansX + ansY + ansZ
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

    fun part2(): Long {
        val lines = File("inputs/day24.txt").readLines()
        return solvePart2(lines)
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