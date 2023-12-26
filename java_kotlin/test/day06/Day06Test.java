package day06;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Day06Test {

    @Test
    public void testFindingFirstWinningDigit() {
        Day06.GameConfig gameConfig = new Day06.GameConfig(7L, 9L);
        assertEquals(
                Day06.findFirstWinningIndex(gameConfig),
                2
        );
    }

    @Test
    public void testFindingLastWinningDigit() {
        Day06.GameConfig gameConfig = new Day06.GameConfig(7L, 9L);
        assertEquals(
                Day06.findLastWinningIndex(gameConfig),
                5
        );
    }

    @Test
    public void testCountingWinningCombinations() {
        Day06.GameConfig gameConfig = new Day06.GameConfig(7L, 9L);
        Day06.WinningDetails winningDetails = new Day06.WinningDetails(
                Day06.findFirstWinningIndex(gameConfig),
                Day06.findLastWinningIndex(gameConfig)
        );
        assertEquals(winningDetails.waysToWin(), 4);
    }

}