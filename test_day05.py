from unittest import TestCase

from day05 import ConversionRule, Converter, parse_input_to_seeds_converters, convert_seed_to_location

sample_input = """seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4"""


class TestConverter(TestCase):
    def test_seed_to_soil(self):
        cr1 = ConversionRule(50, 98, 2)
        cr2 = ConversionRule(52, 50, 48)
        converter = Converter([cr1, cr2])

        self.assertEqual(0, converter.convert(0))
        self.assertEqual(1, converter.convert(1))
        self.assertEqual(48, converter.convert(48))
        self.assertEqual(49, converter.convert(49))

        self.assertEqual(52, converter.convert(50))
        self.assertEqual(53, converter.convert(51))

        self.assertEqual(98, converter.convert(96))
        self.assertEqual(99, converter.convert(97))
        self.assertEqual(50, converter.convert(98))
        self.assertEqual(51, converter.convert(99))

        self.assertEqual(100, converter.convert(100))

        # original sample seed numbers
        self.assertEqual(81, converter.convert(79))
        self.assertEqual(14, converter.convert(14))
        self.assertEqual(57, converter.convert(55))
        self.assertEqual(13, converter.convert(13))

    def test_part1_conversions(self):
        (seeds, converters) = parse_input_to_seeds_converters(sample_input)
        self.assertEqual(82, convert_seed_to_location(converters, 79))
        self.assertEqual(43, convert_seed_to_location(converters, 14))
        self.assertEqual(86, convert_seed_to_location(converters, 55))
        self.assertEqual(35, convert_seed_to_location(converters, 13))
