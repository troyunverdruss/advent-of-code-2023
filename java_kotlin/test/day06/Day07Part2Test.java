package day06;

import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static day06.Day07Part2.calculateValue;
import static org.testng.Assert.*;

public class Day07Part2Test {

    @Test
    public void testSortingCards() {
        List<Day07Part2.Hand> cards = Stream.of(
                        "32T3K 765",
                        "T55J5 684",
                        "KK677 28",
                        "KTJJT 220",
                        "QQQJA 483"
                )
                .map(Day07Part2.Hand::create).toList();
        List<Day07Part2.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(
                calculateValue(sortedHands),
                5905
        );
    }

    @Test
    public void testSortingCardsWithCuratedInput() {
        List<Day07Part2.Hand> cards = Stream.of(
                        "2345A 1",
                        "Q2KJJ 13",
                        "Q2Q2Q 19",
                        "T3T3J 17",
                        "T3Q33 11",
                        "2345J 3",
                        "J345A 2",
                        "32T3K 5",
                        "T55J5 29",
                        "KK677 7",
                        "KTJJT 34",
                        "QQQJA 31",
                        "JJJJJ 37",
                        "JAAAA 43",
                        "AAAAJ 59",
                        "AAAAA 61",
                        "2AAAA 23",
                        "2JJJJ 53",
                        "JJJJ2 41"
                )
                .map(Day07Part2.Hand::create).toList();
        List<Day07Part2.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(
                calculateValue(sortedHands),
                6839
        );
    }

    @Test
    public void testJackToPair() {
        Day07Part2.Hand hand = Day07Part2.Hand.create("J345A 123");
        assertEquals(hand.type(), Day07Part2.HandType.ONE_PAIR);
    }

    @Test
    public void testSortingCardsWithCur() {
        List<Day07Part2.Hand> cards = Stream.of(
                        "2345A 1",
//                        "2345J 3",
                        "J345A 2"
                )
                .map(Day07Part2.Hand::create).toList();
        List<Day07Part2.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(cards.get(0).type(), Day07Part2.HandType.HIGH_CARD);
        assertEquals(cards.get(1).type(), Day07Part2.HandType.ONE_PAIR);

        assertEquals(sortedHands.get(0).bid(), 1);
        assertEquals(sortedHands.get(1).bid(), 2);
//        assertEquals(sortedHands.get(2).bid(), 3);
    }

    @Test
    public void testSortingRanks() {
//        Day07Part2.Hand.CardRank rankJ = new Day07Part2.Hand.CardRank("J");
//        Day07Part2.Hand.CardRank rank2  = new Day07Part2.Hand.CardRank("2");
//
//        List<Day07Part2.Hand.CardRank> list = Stream.of(rankJ, rank2).sorted().toList();
//        assertEquals(list.get(0), rank2);
//        assertEquals(list.get(1), rankJ);

        List<Day07Part2.Hand> cards = Stream.of(
                        "2333J 3",
                        "J333A 2"
                )
                .map(Day07Part2.Hand::create).toList();
        List<Day07Part2.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(sortedHands.get(0).type(), sortedHands.get(1).type());

        assertEquals(sortedHands.get(0).bid(), 2);
        assertEquals(sortedHands.get(1).bid(), 3);
    }


    @Test
    public void testSortingCards2() {
        List<Day07Part2.Hand> cards = Stream.of(
                        "JJ33K 1",
                        "2222Q 10"
                )
                .map(Day07Part2.Hand::create).toList();
        List<Day07Part2.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(
                calculateValue(sortedHands),
                21
        );
    }

    @Test
    public void testSortingCards3() {
        List<Day07Part2.Hand> cards = Stream.of(
                        "JAAKK 1",
                        "JJJAK 2"
                )
                .map(Day07Part2.Hand::create).toList();
        List<Day07Part2.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(
                calculateValue(sortedHands),
                5
        );
    }

    @Test
    public void testCorrectSortingByRank() {
        Day07Part2.Hand hand1 = Day07Part2.Hand.create("QQQQ2 123");
        Day07Part2.Hand hand2 = Day07Part2.Hand.create("JKKK2 123");
        assertEquals(hand1.type(), Day07Part2.HandType.FOUR_OF_A_KIND);
        assertEquals(hand2.type(), Day07Part2.HandType.FOUR_OF_A_KIND);
        assertTrue(hand1.compareTo(hand2) < 0);
    }

    @Test
    public void verifyCorrectHandTypes() {
        Day07Part2.Hand hand1 = Day07Part2.Hand.create("32T3K 765");
        assertEquals(hand1.type(), Day07Part2.HandType.ONE_PAIR);

        hand1 = Day07Part2.Hand.create("KK677 28");
        assertEquals(hand1.type(), Day07Part2.HandType.TWO_PAIR);

        hand1 = Day07Part2.Hand.create("T55J5 684");
        assertEquals(hand1.type(), Day07Part2.HandType.FOUR_OF_A_KIND);

        hand1 = Day07Part2.Hand.create("KTJJT 220");
        assertEquals(hand1.type(), Day07Part2.HandType.FOUR_OF_A_KIND);

        hand1 = Day07Part2.Hand.create("QQQJA 483");
        assertEquals(hand1.type(), Day07Part2.HandType.FOUR_OF_A_KIND);
    }

    @Test
    public void verify5OfAKind() {
        Day07Part2.Hand hand1 = Day07Part2.Hand.create("JKKKK 123");
        assertEquals(hand1.type(), Day07Part2.HandType.FIVE_OF_A_KIND);

        Day07Part2.Hand hand2 = Day07Part2.Hand.create("KKKKK 28");
        assertEquals(hand2.type(), Day07Part2.HandType.FIVE_OF_A_KIND);
    }

    @Test
    public void verifyAllJWorks() {
        Day07Part2.Hand hand1 = Day07Part2.Hand.create("22322 123");
        Day07Part2.Hand hand2 = Day07Part2.Hand.create("JJ3JJ 28");
        Day07Part2.Hand hand3 = Day07Part2.Hand.create("33333 28");

        List<Day07Part2.Hand> list = Stream.of(hand1, hand2, hand3).sorted().toList();
        assertEquals(hand1, list.get(2));
        assertEquals(hand2, list.get(1));
        assertEquals(hand3, list.get(0));
    }

    @Test
    public void verifyAll() {
        Day07Part2.Hand hand1 = Day07Part2.Hand.create("44342 123");
        Day07Part2.Hand hand2 = Day07Part2.Hand.create("JJ3J2 28");
        Day07Part2.Hand hand3 = Day07Part2.Hand.create("33332 28");

        List<Day07Part2.Hand> list = Stream.of(hand1, hand2, hand3).sorted().toList();
        assertEquals(hand1, list.get(2));
        assertEquals(hand2, list.get(1));
        assertEquals(hand3, list.get(0));
    }

    @Test
    public void verifyAl1l() {
        Day07Part2.Hand hand1 = Day07Part2.Hand.create("JJJJJ 123");
        Day07Part2.Hand hand2 = Day07Part2.Hand.create("22222 28");
        Day07Part2.Hand hand3 = Day07Part2.Hand.create("AAAAA 28");

        List<Day07Part2.Hand> list = Stream.of(hand1, hand2, hand3).sorted().toList();
        assertEquals(hand1, list.get(2));
        assertEquals(hand2, list.get(1));
        assertEquals(hand3, list.get(0));
    }

    @Test
    public void verifyAl12() {
        Day07Part2.Hand hand1 = Day07Part2.Hand.create("J2233 123");
        Day07Part2.Hand hand2 = Day07Part2.Hand.create("J2234 28");

        List<Day07Part2.Hand> list = Stream.of(hand1, hand2).sorted().toList();
        assertEquals(hand2, list.get(1));
        assertEquals(hand1, list.get(0));
    }
}
