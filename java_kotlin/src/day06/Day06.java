package day06;


import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day06 {
    public int part1() {
        List<String> lines = read_input();
        List<Integer> times = splitLineToInts(lines.getFirst());
        List<Integer> distances = splitLineToInts(lines.get(1));

        List<GameConfig> gameConfigs = IntStream.range(0, times.size())
                .mapToObj(i -> new GameConfig(times.get(i), distances.get(i)))
                .toList();

        List<WinningDetails> winningDetails = gameConfigs.stream()
                .map(gc -> new WinningDetails(findFirstWinningIndex(gc), findLastWinningIndex(gc)))
                .toList();

        Integer product = winningDetails.stream()
                .map(WinningDetails::waysToWin)
                .reduce(1, (subtotal, element) -> subtotal * element);

        return product;
    }

    private @NotNull Integer findFirstWinningIndex(GameConfig p) {
        for (int i = 0; i < p.time; i++) {
            if (i * (p.time - i) > p.winningDistance) {
                return i;
            }
        }
        throw new RuntimeException("Couldn't find a lower winning index");
    }

    private @NotNull Integer findLastWinningIndex(GameConfig p) {
        for (int i = p.time; i >= 0; i--) {
            if (i * (p.time - i) > p.winningDistance) {
                return i;
            }
        }
        throw new RuntimeException("Couldn't find a lower winning index");
    }

    private List<Integer> splitLineToInts(String line) {
        List<String> items = Arrays.stream(line.split(" ")).filter(s -> !s.isBlank()).toList();
        return items.subList(1, items.size()).stream().map(Integer::parseInt).toList();
    }

    private List<String> read_input() {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("inputs/day6.txt"));

            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record GameConfig(int time, int winningDistance) {
    }

    private record WinningDetails(int lowestIndex, int highestIndex) {
        public int waysToWin() {
            return highestIndex - lowestIndex + 1;
        }
    }
}
