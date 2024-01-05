package day07;

import day07.Day07;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Stream;

import static day07.Day07.calculateValue;
import static org.testng.Assert.*;

public class Day07Test {

    @Test
    public void testParsingCard1() {
        Day07.Hand hand = Day07.Hand.create("32T3K 765");
        assertEquals(hand.cards(), "32T3K");
        assertEquals(hand.bid(), 765);
        assertEquals(hand.type(), Day07.HandType.ONE_PAIR);
    }

    @Test
    public void testParsingCard2() {
        Day07.Hand hand = Day07.Hand.create("T55J5 684");
        assertEquals(hand.cards(), "T55J5");
        assertEquals(hand.bid(), 684);
        assertEquals(hand.type(), Day07.HandType.THREE_OF_A_KIND);
    }

    @Test
    public void testParsingCard3() {
        Day07.Hand hand = Day07.Hand.create("KK677 28");
        assertEquals(hand.cards(), "KK677");
        assertEquals(hand.bid(), 28);
        assertEquals(hand.type(), Day07.HandType.TWO_PAIR);
    }

    @Test
    public void testParsingCard4() {
        Day07.Hand hand = Day07.Hand.create("KTJJT 220");
        assertEquals(hand.cards(), "KTJJT");
        assertEquals(hand.bid(), 220);
        assertEquals(hand.type(), Day07.HandType.TWO_PAIR);
    }


    @Test
    public void testParsingCard5() {
        Day07.Hand hand = Day07.Hand.create("QQQJA 483");
        assertEquals(hand.cards(), "QQQJA");
        assertEquals(hand.bid(), 483);
        assertEquals(hand.type(), Day07.HandType.THREE_OF_A_KIND);
    }

    @Test
    public void testSortingCards() {
        List<Day07.Hand> cards = Stream.of(
                        "32T3K 765",
                        "T55J5 684",
                        "KK677 28",
                        "KTJJT 220",
                        "QQQJA 483"
                )
                .map(Day07.Hand::create).toList();
        List<Day07.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(
                calculateValue(sortedHands),
                6440
        );
    }

    @Test
    public void testRankSorting() {
        List<Day07.Hand> cards = Stream.of(
                        "AAAAA 765",
                        "AA8AA 684",
                        "23332 28",
                        "TTT98 220",
                        "A23A4 483",
                        "23432 483",
                        "23456 483"
                )
                .map(Day07.Hand::create).toList();
        List<Day07.Hand> sortedHands = cards.stream().sorted().toList().reversed();

        assertEquals(sortedHands.get(0).cards(), "23456");
        assertEquals(sortedHands.get(1).cards(), "A23A4");
        assertEquals(sortedHands.get(2).cards(), "23432");
        assertEquals(sortedHands.get(3).cards(), "TTT98");
        assertEquals(sortedHands.get(4).cards(), "23332");
        assertEquals(sortedHands.get(5).cards(), "AA8AA");
        assertEquals(sortedHands.get(6).cards(), "AAAAA");
    }

    @Test
    public void testFiveOfAKind() {
        Day07.Hand hand = Day07.Hand.create("AAAAA 123");
        assertEquals(hand.type(), Day07.HandType.FIVE_OF_A_KIND);
    }

    @Test
    public void testFourOfAKind() {
        Day07.Hand hand = Day07.Hand.create("AA8AA 123");
        assertEquals(hand.type(), Day07.HandType.FOUR_OF_A_KIND);
    }

    @Test
    public void testFullHouse() {
        Day07.Hand hand = Day07.Hand.create("23332 123");
        assertEquals(hand.type(), Day07.HandType.FULL_HOUSE);
    }

    @Test
    public void testThreeOfAKind() {
        Day07.Hand hand = Day07.Hand.create("TTT98 123");
        assertEquals(hand.type(), Day07.HandType.THREE_OF_A_KIND);
    }

    @Test
    public void testTwoPair() {
        Day07.Hand hand = Day07.Hand.create("23432 123");
        assertEquals(hand.type(), Day07.HandType.TWO_PAIR);
    }

    @Test
    public void testOnePair() {
        Day07.Hand hand = Day07.Hand.create("A23A4 123");
        assertEquals(hand.type(), Day07.HandType.ONE_PAIR);
    }

    @Test
    public void testHighCard() {
        Day07.Hand hand = Day07.Hand.create("23456 123");
        assertEquals(hand.type(), Day07.HandType.HIGH_CARD);
    }

    @Test
    public void testCorrectSortingByRank() {
        Day07.Hand hand1 = Day07.Hand.create("AAKQJ 123");
        Day07.Hand hand2 = Day07.Hand.create("AAJQK 123");
        assertTrue(hand1.compareTo(hand2) < 0);
    }
}