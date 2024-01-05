package day08;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

import java.util.List;

public class Day08Test {
    @Test
    public void testSimpleExamplePart1() {
        List<String> lines = List.of(
                "AAA = (BBB, CCC)",
                "BBB = (DDD, EEE)",
                "CCC = (ZZZ, GGG)",
                "DDD = (DDD, DDD)",
                "EEE = (EEE, EEE)",
                "GGG = (GGG, GGG)",
                "ZZZ = (ZZZ, ZZZ)"
        );
        long steps = Day08.solvePart1("RL", Day08.parseNodes(lines));
        assertEquals(steps, 2);
    }

    @Test
    public void testLongerExamplePart1() {
        List<String> lines = List.of(
                "AAA = (BBB, BBB)",
                "BBB = (AAA, ZZZ)",
                "ZZZ = (ZZZ, ZZZ)"
        );
        long steps = Day08.solvePart1("LLR", Day08.parseNodes(lines));
        assertEquals(steps, 6);
    }

    @Test
    public void testExamplePart2BruteForce() {
        List<String> lines = List.of(
                "11A = (11B, XXX)",
                "11B = (XXX, 11Z)",
                "11Z = (11B, XXX)",
                "22A = (22B, XXX)",
                "22B = (22C, 22C)",
                "22C = (22Z, 22Z)",
                "22Z = (22B, 22B)",
                "XXX = (XXX, XXX)"
        );
        long steps = Day08.solvePart2BruteForce("LR", Day08.parseNodes(lines));
        assertEquals(steps, 6);
    }
    @Test
    public void testExamplePart2() {
        List<String> lines = List.of(
                "11A = (11B, XXX)",
                "11B = (XXX, 11Z)",
                "11Z = (11B, XXX)",
                "22A = (22B, XXX)",
                "22B = (22C, 22C)",
                "22C = (22Z, 22Z)",
                "22Z = (22B, 22B)",
                "XXX = (XXX, XXX)"
        );
        long steps = Day08.solvePart2("LR", Day08.parseNodes(lines));
        assertEquals(steps, 6);
    }

}