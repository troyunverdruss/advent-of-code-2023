import math
from dataclasses import dataclass

from day03 import read_lines


@dataclass(frozen=True)
class Card:
    id: int
    winning_numbers: set[int]
    given_numbers: set[int]

    def count_winning_numbers(self):
        return len(self.given_numbers.intersection(self.winning_numbers))

    def card_points(self):
        count = self.count_winning_numbers()
        if count == 0:
            return 0
        else:
            return int(math.pow(2, count - 1))


def parse_cards(lines: [str]) -> [Card]:
    cards = []
    for line in lines:
        game_id = int(line.split(":")[0].split(" ")[-1])
        winning_numbers = set(map(int, filter(lambda x: x != "", line.split(":")[1].split("|")[0].split(" "))))
        given_numbers = set(map(int, filter(lambda x: x != "", line.split(":")[1].split("|")[1].split(" "))))
        cards.append(Card(game_id, winning_numbers, given_numbers))
    return cards


def part1():
    lines = read_lines("inputs/day4.txt")
    cards = parse_cards(lines)
    return sum(map(lambda x: x.card_points(), cards))


if __name__ == "__main__":
    print(part1())
