from unittest import TestCase

from day02 import parse_line_as_game, game_is_valid


class Test(TestCase):
    def test_game_1(self):
        game = parse_line_as_game("Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green")
        self.assertTrue(game_is_valid(game))

    def test_game_2(self):
        game = parse_line_as_game("Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue")
        self.assertTrue(game_is_valid(game))

    def test_game_3(self):
        game = parse_line_as_game("Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red")
        self.assertFalse(game_is_valid(game))

    def test_game_4(self):
        game = parse_line_as_game("Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red")
        self.assertFalse(game_is_valid(game))

    def test_game_5(self):
        game = parse_line_as_game("Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green")
        self.assertTrue(game_is_valid(game))
