from unittest import TestCase
from ddt import ddt, data, unpack

import day01


@ddt
class Test(TestCase):

    def test_first_and_last_as_num(self):
        self.assertEqual(
            12,
            day01.first_and_last_numeric_digits_as_num("1abc2")
        )
        self.assertEqual(
            38,
            day01.first_and_last_numeric_digits_as_num("pqr3stu8vwx")
        )
        self.assertEqual(
            15,
            day01.first_and_last_numeric_digits_as_num("a1b2c3d4e5f")
        )
        self.assertEqual(
            77,
            day01.first_and_last_numeric_digits_as_num("treb7uchet")
        )

    @data(
        [29, "two1nine"],
        [83, "eightwothree"],
        [13, "abcone2threexyz"],
        [24, "xtwone3four"],
        [42, "4nineeightseven2"],
        [14, "zoneight234"],
        [76, "7pqrstsixteen"],
        [18, "oneight"]
    )
    @unpack
    def test_first_and_last_as_num_part2(self, expected_output, input):
        self.assertEqual(
            expected_output,
            day01.first_and_last_digits_as_num(input)
        )

    def test_part_2(self):
        input = [
            "two1nine",
            "eightwothree",
            "abcone2threexyz",
            "xtwone3four",
            "4nineeightseven2",
            "zoneight234",
            "7pqrstsixteen",
        ]
        self.assertEqual(
            281,
            day01.part2(input)
        )
