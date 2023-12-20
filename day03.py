import math
from dataclasses import dataclass
from typing import Dict

numbers = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']


@dataclass(frozen=True)
class Point:
    x: int
    y: int

    def __add__(self, other):
        return Point(self.x + other.x, self.y + other.y)


adjacency_vectors = [
    Point(-1, -1), Point(0, -1), Point(1, -1),
    Point(-1, 0), Point(1, 0),
    Point(-1, 1), Point(0, 1), Point(1, 1),
]


def read_lines() -> [str]:
    with open("inputs/day3.txt") as f:
        return map(lambda x: x.strip(), f.readlines())


def read_grid(lines) -> Dict[Point, str]:
    grid = {}
    for (y_idx, row) in enumerate(lines):
        for (x_idx, c) in enumerate(row):
            grid[Point(x_idx, y_idx)] = c
    return grid


def find_symbol_locations(grid: Dict[Point, str]) -> [Point]:
    not_symbols = ['.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9']
    return list(filter(lambda x: grid[x] not in not_symbols, grid.keys()))


def find_gear_locations(grid: Dict[Point, str]) -> [Point]:
    return list(filter(lambda x: grid[x] == "*", grid.keys()))


# locations can refer to the same number!!
def find_number_locs(grid):
    symbol_locs = find_symbol_locations(grid)
    numbers_locs = []
    for sl in symbol_locs:
        for vec in adjacency_vectors:
            if grid[sl + vec] in numbers:
                numbers_locs.append(sl + vec)
    return numbers_locs


def find_numbers_adjacent_to_gears_locs(grid):
    gear_locs = find_gear_locations(grid)
    numbers_locs = []
    for gl in gear_locs:
        for vec in adjacency_vectors:
            if grid[gl + vec] in numbers:
                numbers_locs.append(gl + vec)
    return numbers_locs


def number_loc_to_part_number_loc(grid, num_loc):
    # find the start
    start = num_loc
    while True:
        test_point = start + Point(-1, 0)
        if test_point in grid.keys() and grid[test_point] in numbers:
            start = test_point
        else:
            break
    return start


def part_number_loc_to_part_number(grid, part_number_loc):
    position = part_number_loc
    digits = [grid[part_number_loc]]
    while (position + Point(1, 0)) in grid.keys() and grid[position + Point(1, 0)] in numbers:
        digits.append(grid[position + Point(1, 0)])
        position = position + Point(1, 0)
    return int(''.join(digits))


def find_part_numbers(grid: Dict[Point, str]) -> [int]:
    number_locs = find_number_locs(grid)
    part_number_locs = set()
    for num_loc in number_locs:
        part_number_loc = number_loc_to_part_number_loc(grid, num_loc)
        part_number_locs.add(part_number_loc)
    part_numbers = []
    for part_number_loc in part_number_locs:
        part_number = part_number_loc_to_part_number(grid, part_number_loc)
        part_numbers.append(part_number)
    return part_numbers


def part1():
    grid = read_grid(read_lines())
    part_numbers = find_part_numbers(grid)
    return sum(part_numbers)


def find_gear_ratios(grid):
    gear_locs = find_gear_locations(grid)

    gear_ratios = []
    for gl in gear_locs:
        numbers_locs = []
        for vec in adjacency_vectors:
            if grid[gl + vec] in numbers:
                numbers_locs.append(gl + vec)
        part_number_locs = set()
        for num_loc in numbers_locs:
            part_number_loc = number_loc_to_part_number_loc(grid, num_loc)
            part_number_locs.add(part_number_loc)
        if len(part_number_locs) == 2:
            gear_ratio = math.prod(
                map(
                    lambda x: part_number_loc_to_part_number(grid, x),
                    part_number_locs
                )
            )
            gear_ratios.append(gear_ratio)

    return gear_ratios


def part2():
    grid = read_grid(read_lines())
    ratios = find_gear_ratios(grid)
    return sum(ratios)


if __name__ == "__main__":
    print(part1())
    print(part2())
