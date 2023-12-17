from unittest import TestCase

import day01


class Test(TestCase):
    def test_first_and_last_as_num(self):
        self.assertEqual(
            12,
            day01.first_and_last_as_num("1abc2")
        )
        self.assertEqual(
            38,
            day01.first_and_last_as_num("pqr3stu8vwx")
        )
        self.assertEqual(
            15,
            day01.first_and_last_as_num("a1b2c3d4e5f")
        )
        self.assertEqual(
            77,
            day01.first_and_last_as_num("treb7uchet")
        )
