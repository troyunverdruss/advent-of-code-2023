package day06;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day07 {
    static final Map<String, Integer> RANK_LOOKUP = new HashMap<>();

    static {
        RANK_LOOKUP.put("A", 1);
        RANK_LOOKUP.put("K", 2);
        RANK_LOOKUP.put("Q", 3);
        RANK_LOOKUP.put("J", 4);
        RANK_LOOKUP.put("T", 5);
        RANK_LOOKUP.put("9", 6);
        RANK_LOOKUP.put("8", 7);
        RANK_LOOKUP.put("7", 8);
        RANK_LOOKUP.put("6", 9);
        RANK_LOOKUP.put("5", 10);
        RANK_LOOKUP.put("4", 11);
        RANK_LOOKUP.put("3", 12);
        RANK_LOOKUP.put("2", 13);
    }

    public int part1() {
        List<String> lines = Day06.read_input("inputs/day7.txt");
        List<Hand> hands = lines.stream()
                .map(Hand::create)
                .toList();

        List<Hand> sortedHands = hands.stream().sorted().toList().reversed();

        return calculateValue(sortedHands);
    }

    static int calculateValue(List<Hand> sortedHands) {
        return IntStream.range(0, sortedHands.size())
                .map(i -> (i + 1) * sortedHands.get(i).bid())
                .reduce(0, Integer::sum);
    }

    public int part2() {
        throw new RuntimeException("part2");
    }

    record Hand(String cards, int bid) implements Comparable<Hand> {
        static Hand create(String line) {
            List<String> parts = Arrays.stream(line.split(" ")).toList();
            return new Hand(parts.getFirst(), Integer.parseInt(parts.get(1)));
        }


        @Override
        public int compareTo(@NotNull Hand other) {
            if (this.equals(other)) {
                return 0;
            } else if (this.type().rank < other.type().rank) {
                return -1;
            } else if (this.type().rank > other.type().rank) {
                return 1;
            } else {
                for (int i = 0; i < 5; i++) {
                    CardRank thisRank = new CardRank(this.cards.substring(i, i + 1));
                    CardRank otherRank = new CardRank(other.cards.substring(i, i + 1));

                    int compared = thisRank.compareTo(otherRank);
                    if (compared == 0) {
                        continue;
                    }
                    return compared;
                }
            }
            throw new RuntimeException("Comparison didn't work");
        }

        record CardRank(String val) implements Comparable<CardRank> {
            @Override
            public int compareTo(@NotNull CardRank other) {
                return RANK_LOOKUP.get(this.val) - RANK_LOOKUP.get(other.val);
            }
        }


        HandType type() {
            Map<String, Long> characterCount = this.cards.codePoints()
                    .mapToObj(Character::toString)
                    .collect(Collectors.groupingBy(a -> a, Collectors.counting()));

            if (characterCount.containsValue(5L)) {
                return HandType.FIVE_OF_A_KIND;
            } else if (characterCount.containsValue(4L)) {
                return HandType.FOUR_OF_A_KIND;
            } else if (characterCount.containsValue(3L) && characterCount.containsValue(2L)) {
                return HandType.FULL_HOUSE;
            } else if (characterCount.containsValue(3L)) {
                return HandType.THREE_OF_A_KIND;
            } else {
                long value = characterCount.values().stream().reduce(1L, (sub, elem) -> sub * elem);
                if (value == 4) {
                    return HandType.TWO_PAIR;
                } else if (value == 2) {
                    return HandType.ONE_PAIR;
                } else {
                    return HandType.HIGH_CARD;
                }
            }
        }
    }

    enum HandType {
        FIVE_OF_A_KIND(1),
        FOUR_OF_A_KIND(2),
        FULL_HOUSE(3),
        THREE_OF_A_KIND(4),
        TWO_PAIR(5),
        ONE_PAIR(6),
        HIGH_CARD(7);

        private final int rank;

        HandType(int rank) {
            this.rank = rank;
        }
    }
}
