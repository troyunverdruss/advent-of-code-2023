package day16

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

class Day16Test {
    @Test
    fun `example 1, find energized`() {
        val input = """
                .|...\....
                |.-.\.....
                .....|-...
                ........|.
                ..........
                .........\
                ..../.\\..
                .-.-/..|..
                .|....-|.\
                ..//.|....
                """.trimIndent()
        val day16 = Day16()
        val lines = input.trim().split("\n")
        val grid = day16.parseLinesToGrid(lines)
        assertEquals(day16.solvePart1(grid), 46)
    }

    @Test
    fun `example 2, find max energized`() {
        val input = """
                .|...\....
                |.-.\.....
                .....|-...
                ........|.
                ..........
                .........\
                ..../.\\..
                .-.-/..|..
                .|....-|.\
                ..//.|....
                """.trimIndent()
        val day16 = Day16()
        val lines = input.trim().split("\n")
        val grid = day16.parseLinesToGrid(lines)
        assertEquals(day16.solvePart2(grid), 51)
    }
}