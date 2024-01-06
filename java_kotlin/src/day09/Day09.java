package day09;

import day06.Day06;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day09 {

    public long part1() {
        List<String> lines = Day06.read_input("inputs/day9.txt");
        List<List<Long>> sequences = parseLines(lines);

        return sequences.stream()
                .map(Day09::findNextDigit)
                .reduce(0L, Long::sum);
    }

    public long part2() {
        List<String> lines = Day06.read_input("inputs/day9.txt");
        List<List<Long>> sequences = parseLines(lines);

        return sequences.stream()
                .map(Day09::findPreviousDigit)
                .reduce(0L, Long::sum);
    }

    private List<List<Long>> parseLines(List<String> lines) {
        return lines.stream()
                .map(line -> Arrays
                        .stream(line.split(" "))
                        .filter(v -> !v.isBlank())
                        .map(Long::parseLong)
                        .toList())
                .toList();
    }

    static long findNextDigit(List<Long> sequence) {
        List<Long> subList = windowBy2(sequence)
                .map(listPair -> listPair.getLast() - listPair.getFirst())
                .toList();
        if (subList.stream().allMatch(v -> v == 0)) {
            return sequence.getLast();
        }
        return sequence.getLast() + findNextDigit(subList);
    }

    static long findPreviousDigit(List<Long> sequence) {
        List<Long> subList = windowBy2(sequence)
                .map(listPair -> listPair.getLast() - listPair.getFirst())
                .toList();
        if (subList.stream().allMatch(v -> v == 0)) {
            return sequence.getFirst();
        }
        return sequence.getFirst() - findPreviousDigit(subList);
    }

    static <T> Stream<List<T>> windowBy2(List<T> sourceList) {
        int windowSize = 2;
        if (windowSize > sourceList.size()) {
            throw new RuntimeException("Provided window size must be larger than the source list");
        }
        return IntStream.range(0, sourceList.size() - windowSize + 1)
                .mapToObj(i -> sourceList.subList(i, i + windowSize));
    }
}
