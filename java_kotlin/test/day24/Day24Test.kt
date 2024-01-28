package day24

import org.testng.Assert.*
import org.testng.annotations.Test

class Day24Test {

    val lines = listOf(
        "19, 13, 30 @ -2,  1, -2",
        "18, 19, 22 @ -1, -1, -2",
        "20, 25, 34 @ -2, -2, -4",
        "12, 31, 28 @ -1, -2, -1",
        "20, 19, 15 @  1, -5, -3",
    )
    @Test
    fun `part 1 example`() {
        val day = Day24()
        assertEquals(day.solvePart1(lines, 7L, 27L), 2)
    }
}