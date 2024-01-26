package day22

import org.testng.Assert.*
import org.testng.annotations.Test

class Day22Test {
    val lines = listOf(
        "1,0,1~1,2,1",
        "0,0,2~2,0,2",
        "0,2,3~2,2,3",
        "0,0,4~0,2,4",
        "2,0,5~2,2,5",
        "0,1,6~2,1,6",
        "1,1,8~1,1,9",
    )
    @Test
    fun `verify example 1`() {
        val day = Day22()
        val result = day.solvePart1(lines)
        assertEquals(result, 5)
    }

    @Test
    fun `verify example part 2`() {
        val day = Day22()
        val result = day.solvePart2(lines)
        assertEquals(result, 7)
    }
}