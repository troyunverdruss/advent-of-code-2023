from dataclasses import dataclass


@dataclass
class ConversionRule:
    destination_range_start: int
    source_range_start: int
    range_length: int


@dataclass
class Converter:
    conversion_rules: [ConversionRule]

    def convert(self, input_number: int) -> int:
        for rule in self.conversion_rules:
            if rule.source_range_start <= input_number < rule.source_range_start + rule.range_length:
                offset_from_start = input_number - rule.source_range_start
                return rule.destination_range_start + offset_from_start
        return input_number

    def reverse_convert(self, output_number: int) -> int:
        for rule in self.conversion_rules:
            if rule.destination_range_start <= output_number < rule.destination_range_start + rule.range_length:
                offset_from_start = output_number - rule.destination_range_start
                return rule.source_range_start + offset_from_start
        return output_number


def parse_input_to_seeds_converters(raw_input: str) -> ([int], [Converter]):
    input_sections = raw_input.split("\n\n")
    seeds = list(map(int, filter(lambda x: x != '', input_sections[0].split(":")[1].split(" "))))
    converters = []
    for section in input_sections[1:]:
        conversion_rules = []
        for line in section.split("\n"):
            if line == "" or "map" in line:
                pass
            else:
                conversion_rules.append(ConversionRule(*map(int, filter(lambda x: x != "", line.split(" ")))))
        converters.append(Converter(conversion_rules))

    return (seeds, converters)


def convert_seed_to_location(converters: [Converter], seed: int) -> int:
    # print(f"seed: {seed}", end="")
    computed_value = seed
    for converter in converters:
        computed_value = converter.convert(computed_value)
        # print(f" -> {computed_value}", end="")
    # print("")
    return computed_value


def brute_force_convert_seed_range_to_lowest_location(converters: [Converter], seed: int, range_length: int) -> int:
    lowest_value = convert_seed_to_location(converters, seed)
    for test_seed in range(seed, seed + range_length):
        test_location = convert_seed_to_location(converters, test_seed)
        if test_location < lowest_value:
            lowest_value = test_location
    # print(f"lowest so far: {lowest_value}")
    return lowest_value


def find_boundaries_in_terms_of_seed(converters: [Converter]) -> [int]:
    all_boundaries = []
    for i in range(len(converters)-1, 0, -1):
        for conversion_rule in converters[i].conversion_rules:
            boundary = convert_backwards_from_converter_index(converters, i, conversion_rule.source_range_start)
            all_boundaries.append(boundary)
    return all_boundaries


def convert_backwards_from_converter_index(converters: [Converter], starting_index: int, value: int) -> int:
    new_value = value
    for i in range(starting_index, 0, -1):
        new_value = converters[i-1].reverse_convert(new_value)
    return new_value


def convert_seed_range_to_lowest_location(converters: [Converter], seed: int, range_length: int) -> int:
    boundaries = find_boundaries_in_terms_of_seed(converters)

    lowest_value = float('inf')
    for boundary in boundaries:
        if boundary in range(seed, seed + range_length):
            test_value = convert_seed_to_location(converters, boundary)
            if test_value < lowest_value:
                lowest_value = test_value
    return lowest_value


def read_raw_file_data(path):
    with open(path) as f:
        return f.read()


def part1():
    raw_file_data = read_raw_file_data("inputs/day5.txt")
    (seeds, converters) = parse_input_to_seeds_converters(raw_file_data)
    return min(map(lambda x: convert_seed_to_location(converters, x), seeds))


def part2(raw_file_data):
    (seeds, converters) = parse_input_to_seeds_converters(raw_file_data)
    return min(
        map(
            lambda x: convert_seed_range_to_lowest_location(converters, seeds[x * 2], seeds[x * 2] + 1),
            range(int(len(seeds) / 2))
        )
    )


if __name__ == "__main__":
    print(part1())
    print(part2(read_raw_file_data("inputs/day5.txt")))
