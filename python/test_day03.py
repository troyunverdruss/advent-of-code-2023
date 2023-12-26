from unittest import TestCase

from day03 import read_grid, find_symbol_locations, Point, find_part_numbers

test_input = [
    '467..114..',
    '...*......',
    '..35..633.',
    '......#...',
    '617*......',
    '.....+.58.',
    '..592.....',
    '......755.',
    '...$.*....',
    '.664.598..',
]


class TestDay03(TestCase):
    def test_find_part_numbers(self):
        grid = read_grid(test_input)
        symbol_locs = find_symbol_locations(grid)
        self.assertEqual(
            [Point(3, 1), Point(6, 3), Point(3, 4),
             Point(5, 5), Point(3, 8), Point(5, 8)],
            symbol_locs
        )

    def test_find_part_numbers(self):
        grid = read_grid(test_input)
        part_numbers = find_part_numbers(grid)
        self.assertEqual(
            {467, 35, 633, 617, 592, 755, 664, 598},
            set(part_numbers)
        )
        self.assertEqual(
            4361,
            sum(part_numbers)
        )
