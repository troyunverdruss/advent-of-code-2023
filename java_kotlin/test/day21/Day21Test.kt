package day21

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
    }

    @Test
    fun `verify example 1, 2 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1(lines, 2), 4)
    }

    @Test
    fun `verify example 1, 3 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1(lines, 3), 6)
    }

    @Test
    fun `verify example 1, 6 steps`() {
        val day = Day21()
        assertEquals(day.solvePart1(lines, 6), 16)
    }
}