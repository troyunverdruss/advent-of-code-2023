package day22

import org.testng.Assert.*
import org.testng.annotations.Test
import java.io.File

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
    fun `test settle bricks`() {
        val day = Day22()
        val bricks = day.parseBricks(lines)
        val settled = day.settleBricks(bricks)
        assertEquals(settled[0].moved, false)
        assertEquals(settled[1].moved, false)
        assertEquals(settled[2].moved, true)
        assertEquals(settled[3].moved, true)
        assertEquals(settled[4].moved, true)
        assertEquals(settled[5].moved, true)
        assertEquals(settled[6].moved, true)
    }

    @Test
    fun `verify example part 2`() {
        val day = Day22()
        val result = day.solvePart2(lines)
        assertEquals(result, 7)
    }

//    @Test
//    fun `profile example part 2`() {
//        val day = Day22()
//        val lines = File("inputs/day22.txt").readLines()
//        val result = day.solvePart2(lines)
//        assertEquals(result, 7)
//    }
}