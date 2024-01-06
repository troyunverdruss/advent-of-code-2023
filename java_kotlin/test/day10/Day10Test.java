package day10;

import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class Day10Test {


    @Test
    public void testSimpleExample1() {
        List<String> sample = List.of(
                ".....",
                ".S-7.",
                ".|.|.",
                ".L-J.",
                "....."
        );
        Map<Day10.Point, String> grid = Day10.parseGrid(sample);
        assertEquals(Day10.solvePart2(grid), 4);
    }

    @Test
    public void testSimpleExampleWithSurroundingJunk1() {
        List<String> sample = List.of(
                "-L|F7",
                "7S-7|",
                "L|7||",
                "-L-J|",
                "L|-JF"
        );
        Map<Day10.Point, String> grid = Day10.parseGrid(sample);
        assertEquals(Day10.solvePart1(grid), 4);
    }

    @Test
    public void testSimpleExample2() {
        List<String> sample = List.of(
                "..F7.",
                ".FJ|.",
                "SJ.L7",
                "|F--J",
                "LJ..."
        );
        Map<Day10.Point, String> grid = Day10.parseGrid(sample);
        assertEquals(Day10.solvePart1(grid), 8);
    }


    @Test
    public void testSimpleExampleWithSurroundingJunk2() {
        List<String> sample = List.of(
                "7-F7-",
                ".FJ|7",
                "SJLL7",
                "|F--J",
                "LJ.LJ"
        );
        Map<Day10.Point, String> grid = Day10.parseGrid(sample);
        assertEquals(Day10.solvePart1(grid), 8);
    }


}