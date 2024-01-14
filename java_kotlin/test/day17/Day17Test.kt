package day17

import day16.Day16
import org.testng.Assert.*
import org.testng.annotations.Test

class Day17Test {
    @Test
    fun `test example 1`() {
        val lines = listOf(
            "2413432311323",
            "3215453535623",
            "3255245654254",
            "3446585845452",
            "4546657867536",
            "1438598798454",
            "4457876987766",
            "3637877979653",
            "4654967986887",
            "4564679986453",
            "1224686865563",
            "2546548887735",
            "4322674655533",
        )
        val grid = Day17.stringGridToDigits(Day16.parseLinesToGrid(lines))
        val day = Day17()
        assertEquals(day.solvePart1(grid), 102)
    }

    @Test
    fun `test example 2`() {
        val lines = listOf(
            "2413432311323",
            "3215453535623",
            "3255245654254",
            "3446585845452",
            "4546657867536",
            "1438598798454",
            "4457876987766",
            "3637877979653",
            "4654967986887",
            "4564679986453",
            "1224686865563",
            "2546548887735",
            "4322674655533",
        )
        val grid = Day17.stringGridToDigits(Day16.parseLinesToGrid(lines))
        val day = Day17()
        assertEquals(day.solvePart2(grid), 94)
    }

    @Test
    fun `test example 2 - simpler`() {
        val lines = listOf(
            "111111111111",
            "999999999991",
            "999999999991",
            "999999999991",
            "999999999991",
        )
        val grid = Day17.stringGridToDigits(Day16.parseLinesToGrid(lines))
        val day = Day17()
        assertEquals(day.solvePart2(grid), 71)
    }
}