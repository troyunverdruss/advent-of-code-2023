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
    val lines2 = listOf(
        "...........",
        ".....###.#.",
        ".###.##..#.",
        "..#.#...#..",
        "....#.#....",
        ".....S.....",
        ".##..#...#.",
        ".......##..",
        ".##.#.####.",
        ".##..##.##.",
        "...........",
    )
    val linesForGoingLeft = listOf(
        "..................................................................",
        ".....###.#......###.#......###.#......###.#......###.#......###.#.",
        ".###.##..#..###.##..#..###.##..#..###.##..#..###.##..#..###.##..#.",
        "..#.#...#....#.#...#....#.#...#....#.#...#....#.#...#....#.#...#..",
        "....#.#........#.#........#.#........#.#........#.#........#.#....",
        ".....s.....x..........x..........x..........x..........x....S.....",
        ".##..#...#..##..#...#..##..#...#..##..#...#..##..#...#..##..#...#.",
        ".......##.........##.........##.........##.........##.........##..",
        ".##.#.####..##.#.####..##.#.####..##.#.####..##.#.####..##.#.####.",
        ".##..##.##..##..##.##..##..##.##..##..##.##..##..##.##..##..##.##.",
        "..................................................................",
    )
val linesForGoingRight = listOf(
        "..................................................................",
        ".....###.#......###.#......###.#......###.#......###.#......###.#.",
        ".###.##..#..###.##..#..###.##..#..###.##..#..###.##..#..###.##..#.",
        "..#.#...#....#.#...#....#.#...#....#.#...#....#.#...#....#.#...#..",
        "....#.#........#.#........#.#........#.#........#.#........#.#....",
        ".....S.....x..........x..........x..........x..........x....s.....",
        ".##..#...#..##..#...#..##..#...#..##..#...#..##..#...#..##..#...#.",
        ".......##.........##.........##.........##.........##.........##..",
        ".##.#.####..##.#.####..##.#.####..##.#.####..##.#.####..##.#.####.",
        ".##..##.##..##..##.##..##..##.##..##..##.##..##..##.##..##..##.##.",
        "..................................................................",
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
        assertEquals(day.solvePart1(lines, 6), 16)
        assertEquals(day.solvePart1Take2(lines, 6), 16)

        Day21.initGlobals(lines)
        val count = day.countEndingPointsInArbitraryGrid(
            listOf(Point(5, 5)),
            listOf(0),
            6,
            Point(0, 0),
            Day21.max
        )
        assertEquals(count, 16)
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
        assertEquals(day.minStepsToPoint(start, Point(-1, 4)), 7)

    }

    @Test
    fun `print out grid with shortest distance marked on all positions xxxxx`() {
        val day = Day21()
        val extraLength = lines2.map { it + it + it + it + it + it }
        val extraLines = extraLength
        Day21.initGlobals(linesForGoingLeft)
        val start = Day21.grid
            .filter { it.value == "S" }
            .map { it.key }
            .sortedBy { it.x }[0]
//            .subList(9,12)
//            .sortedBy { it.y }[0]


        val myGrid = Day21.grid.toMutableMap()
        Day21.grid.keys.forEach {
            if (Day21.grid[it] != "#") {

                val steps = day.minStepsToPoint(start, it)
                myGrid[it] = "$steps"
                if (steps <= 55 && (55 - steps) % 2 == 0L) {
                    myGrid[it] = "x"
                }
            }
        }
        Day16.debugPrintGrid(myGrid)
    }

    @Test
    fun `counting leftwards works correctly`() {
        val day = Day21()

        Day21.initGlobals(linesForGoingLeft)
        val start = Day21.grid
            .filter { it.value == "S" }
            .map { it.key }
            .sortedBy { it.x }[0]

        Day21.searchInfiniteGrid = false
        val bruteForceCount = day.find(55, start, 0).size.toLong()
        Day21.searchInfiniteGrid = true
        val smartCount = day.solvePart1Take2(lines2, 55)
        assertEquals(smartCount, bruteForceCount)
        println(smartCount)
    }

    @Test
    fun `counting rightwards works correctly`() {
        val day = Day21()

        Day21.initGlobals(linesForGoingRight)
        val start = Day21.grid
            .filter { it.value == "S" }
            .map { it.key }
            .sortedBy { it.x }[0]

        Day21.searchInfiniteGrid = false
        val bruteForceCount = day.find(55, start, 0).size.toLong()
        Day21.searchInfiniteGrid = true
        val smartCount = day.solvePart1Take2(lines2, 55)
        assertEquals(smartCount, bruteForceCount)
        println(smartCount)
    }
}