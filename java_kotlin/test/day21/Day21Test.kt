package day21

import day16.Day16
import day16.Direction
import day16.Point
import org.testng.Assert.*
import org.testng.annotations.Test

class Day21Test {

    val lines = listOf(
        "...........",
        ".....###.#.",
        ".###.##..#.",
        "..#.#...#..",
        "....#.#....",
        ".##..S####.",
        ".##..#...#.",
        ".......##..",
        ".##.#.####.",
        ".##..##.##.",
        "...........",
    )

    @Test
    fun `verify example 1, 1 step`() {
        val day = Day21()
        assertEquals(day.solvePart1(lines, 1), 2)
        assertEquals(day.solvePart1Take2(lines, 1), 2)
    }

    @Test
    fun `verify example 1, 2 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1(lines, 2), 4)
        assertEquals(day.solvePart1Take2(lines, 2), 4)
    }

    @Test
    fun `verify example 1, 3 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1(lines, 3), 6)
        assertEquals(day.solvePart1Take2(lines, 3), 6)
    }

    @Test
    fun `verify example 1, 6 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1(lines, 6), 16)
        assertEquals(day.solvePart1Take2(lines, 6), 16)
    }

    @Test
    fun `verify example 2, 6 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1Take2(lines, 6), 16)
    }

    @Test
    fun `verify example 2, 10 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1Take2(lines, 10), 50)
    }

    @Test
    fun `verify example 2, 50 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1Take2(lines, 50), 1594)
    }

    @Test
    fun `verify example 2, 100 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1Take2(lines, 100), 6536)
    }

    @Test
    fun `verify minStepsToPointWorks`() {
        val day = Day21()
        Day21.initGlobals(lines)
        val start = Day21.grid.filter { it.value == "S" }.map { it.key }.first()
        assertEquals(day.minStepsToPoint(start, start), 0)
        assertEquals(day.minStepsToPoint(start, start + Direction.Up.point), 1)
        assertEquals(day.minStepsToPoint(start, start + Direction.Left.point), 1)

        assertEquals(day.minStepsToPoint(start, start + Direction.Left.point + Direction.Left.point), 2)
        assertEquals(
            day.minStepsToPoint(
                start,
                start + Direction.Left.point + Direction.Left.point + Direction.Up.point
            ),
            3
        )
    }

    @Test
    fun `count steps to a point off the original grid`() {
        val day = Day21()
        Day21.initGlobals(lines)
        val start = Day21.grid.filter { it.value == "S" }.map { it.key }.first()
        assertEquals(day.minStepsToPoint(start, Point(-1,4)), 7)

    }
}