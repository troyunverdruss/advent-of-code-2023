package day18

import day16.Direction
import org.testng.Assert.*
import org.testng.annotations.Test

class Day18Test {
    @Test
    fun `test part 1 example with drawing into grid`() {
        val lines = listOf(
            "R 6 (#70c710)",
            "D 5 (#0dc571)",
            "L 2 (#5713f0)",
            "D 2 (#d2c081)",
            "R 2 (#59c680)",
            "D 2 (#411b91)",
            "L 5 (#8ceee2)",
            "U 2 (#caa173)",
            "L 1 (#1b58a2)",
            "U 2 (#caa171)",
            "R 2 (#7807d2)",
            "U 3 (#a77fa3)",
            "L 2 (#015232)",
            "U 2 (#7a21e3)",
        )
        val day = Day18()
        assertEquals(day.solvePart1DrawAndFill(lines), 62)
    }

    @Test
    fun `test part 1 example with polygon area formula`() {
        val lines = listOf(
            "R 6 (#70c710)",
            "D 5 (#0dc571)",
            "L 2 (#5713f0)",
            "D 2 (#d2c081)",
            "R 2 (#59c680)",
            "D 2 (#411b91)",
            "L 5 (#8ceee2)",
            "U 2 (#caa173)",
            "L 1 (#1b58a2)",
            "U 2 (#caa171)",
            "R 2 (#7807d2)",
            "U 3 (#a77fa3)",
            "L 2 (#015232)",
            "U 2 (#7a21e3)",
        )
        val day = Day18()
        val instructions = lines.map { Instruction.parse(it) }.toList()
        assertEquals(day.calculatePolygonCoverage(instructions), 62)
    }

    @Test
    fun `verify basic polygon works OK`() {
        val lines = listOf(
            "U 8 (#70c710)",
            "R 8 (#0dc571)",
            "D 8 (#7a21e3)",
            "L 8 (#7a21e3)",
        )
        val day = Day18()
        val instructions = lines.map { Instruction.parse(it) }.toList()
        assertEquals(day.calculatePolygonCoverage(instructions), 81)
    }

    @Test
    fun `test part 2 example`() {
        val lines = listOf(
            "R 6 (#70c710)",
            "D 5 (#0dc571)",
            "L 2 (#5713f0)",
            "D 2 (#d2c081)",
            "R 2 (#59c680)",
            "D 2 (#411b91)",
            "L 5 (#8ceee2)",
            "U 2 (#caa173)",
            "L 1 (#1b58a2)",
            "U 2 (#caa171)",
            "R 2 (#7807d2)",
            "U 3 (#a77fa3)",
            "L 2 (#015232)",
            "U 2 (#7a21e3)",
        )
        val day = Day18()
        val instructions = lines.map { Instruction.parse(it).toTrueInstruction() }.toList()
        assertEquals(day.calculatePolygonCoverage(instructions), 952408144115)
        assertEquals(day.solvePart2(lines), 952408144115)
    }

    @Test
    fun `verify parsing to true instructions works`() {
        val right = Instruction(Direction.Left, 0, "70c710").toTrueInstruction()
        assertEquals(right.steps, 461937)
        assertEquals(right.direction, Direction.Right)

        val left = Instruction(Direction.Right, 0, "8ceee2").toTrueInstruction()
        assertEquals(left.steps, 577262)
        assertEquals(left.direction, Direction.Left)

        val up = Instruction(Direction.Down, 0, "caa173").toTrueInstruction()
        assertEquals(up.steps, 829975)
        assertEquals(up.direction, Direction.Up)

        val down = Instruction(Direction.Up, 0, "0dc571").toTrueInstruction()
        assertEquals(down.steps, 56407)
        assertEquals(down.direction, Direction.Down)

    }
}