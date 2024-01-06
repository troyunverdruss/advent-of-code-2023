package day09;

import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.testng.Assert.*;

public class Day09Test {

    @Test
    public void testFindNextDigitSequence1() {
        List<Long> input = Stream.of(0, 3, 6, 9, 12, 15).map(v -> (long) v).toList();
        assertEquals(Day09.findNextDigit(input), 18);
        assertEquals(Day09.findPreviousDigit(input), -3);
    }

    @Test
    public void testFindNextDigitSequence2() {
        List<Long> input = Stream.of(1, 3, 6, 10, 15, 21).map(v -> (long) v).toList();
        assertEquals(Day09.findNextDigit(input), 28);
        assertEquals(Day09.findPreviousDigit(input), 0);
    }

    @Test
    public void testFindNextDigitSequence3() {
        List<Long> input = Stream.of(10, 13, 16, 21, 30, 45).map(v -> (long) v).toList();
        assertEquals(Day09.findNextDigit(input), 68);
        assertEquals(Day09.findPreviousDigit(input), 5);
    }
}