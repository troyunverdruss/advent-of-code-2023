package day06;


import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day06 {
    public Long part1() {
        List<String> lines = read_input("inputs/day6.txt");
        List<Long> times = splitLineToNumbers(lines.getFirst());
        List<Long> distances = splitLineToNumbers(lines.get(1));

        List<GameConfig> gameConfigs = IntStream.range(0, times.size())
                .mapToObj(i -> new GameConfig(times.get(i), distances.get(i)))
                .toList();

        List<WinningDetails> winningDetails = gameConfigs.stream()
                .map(gc -> new WinningDetails(findFirstWinningIndex(gc), findLastWinningIndex(gc)))
                .toList();

        return winningDetails.stream()
                .map(WinningDetails::waysToWin)
                .reduce(1L, (subtotal, element) -> subtotal * element);
    }

    public Long part2() {
        List<String> lines = read_input("inputs/day6.txt");
        Long time = Long.parseLong(lines.get(0).replace(" ", "").split(":")[1]);
        Long distance = Long.parseLong(lines.get(1).replace(" ", "").split(":")[1]);
        GameConfig gameConfig = new GameConfig(time, distance);
        return new WinningDetails(findFirstWinningIndex(gameConfig), findLastWinningIndex(gameConfig)).waysToWin();
    }

    static @NotNull Long findFirstWinningIndex(GameConfig gameConfig) {
        for (long i = 0; i < gameConfig.time; i++) {
            if (i * (gameConfig.time - i) > gameConfig.winningDistance) {
                return i;
            }
        }
        throw new RuntimeException("Couldn't find a lower winning index");
    }

    static @NotNull Long findLastWinningIndex(GameConfig gameConfig) {
        for (long i = gameConfig.time; i >= 0; i--) {
            if (i * (gameConfig.time - i) > gameConfig.winningDistance) {
                return i;
            }
        }
        throw new RuntimeException("Couldn't find an upper winning index");
    }

    private List<Long> splitLineToNumbers(String line) {
        List<String> items = Arrays.stream(line.split(" ")).filter(s -> !s.isBlank()).toList();
        return items.subList(1, items.size()).stream().map(Long::parseLong).toList();
    }

    public static List<String> read_input(String path) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

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

    record GameConfig(Long time, Long winningDistance) {
    }

    record WinningDetails(Long lowestIndex, Long highestIndex) {
        public Long waysToWin() {
            return highestIndex - lowestIndex + 1;
        }
    }
}
