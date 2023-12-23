from dataclasses import dataclass


@dataclass
class Round:
    red: int
    green: int
    blue: int


@dataclass
class Game:
    id: int
    rounds: list[Round]


def read_lines() -> [str]:
    with open("../inputs/day2.txt") as f:
        return f.readlines()


def parse_rounds(line: str) -> [Round]:
    rounds = []

    for r in line.split(";"):
        red = 0
        green = 0
        blue = 0
        for type in r.split(","):
            (count, color) = type.strip().split(" ")
            if color == "red":
                red = int(count)
            elif color == "green":
                green = int(count)
            elif color == "blue":
                blue = int(count)
            else:
                raise f"Bad parsing of this line: {type}"
        rounds.append(Round(red, green, blue))

    return rounds


def parse_line_as_game(line: str) -> Game:
    # Game 90: 7 red; 5 blue, 11 red, 8 green; 8 red, 3 green, 2 blue
    (a, b) = line.split(":")
    game_id = int(a.split(" ")[1])
    game_rounds = parse_rounds(b)
    return Game(game_id, game_rounds)


def game_is_valid(game: Game) -> bool:
    part1_valid_round = Round(12, 13, 14)
    return all(
        map(
            lambda x: x.red <= part1_valid_round.red
                      and x.green <= part1_valid_round.green
                      and x.blue <= part1_valid_round.blue,
            game.rounds
        )
    )


def get_valid_game_ids(games) -> [str]:
    return list(map(lambda x: x.id, filter(lambda x: game_is_valid(x), games)))


def part1():
    games = list(map(lambda x: parse_line_as_game(x), read_lines()))
    return sum(get_valid_game_ids(games))


def compute_game_power(game: Game) -> int:
    max_red = max(map(lambda x: x.red, game.rounds))
    max_green = max(map(lambda x: x.green, game.rounds))
    max_blue = max(map(lambda x: x.blue, game.rounds))
    return max_red * max_green * max_blue


def part2():
    games = list(map(lambda x: parse_line_as_game(x), read_lines()))
    power_sum = sum(map(lambda x: compute_game_power(x), games))
    return power_sum


if __name__ == "__main__":
    print(part1())
    print(part2())
