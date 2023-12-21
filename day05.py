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
    computed_value = seed
    for converter in converters:
        computed_value = converter.convert(computed_value)
    return computed_value


def read_raw_file_data(path):
    with open(path) as f:
        return f.read()
def part1():
    raw_file_data = read_raw_file_data("inputs/day5.txt")
    (seeds, converters) = parse_input_to_seeds_converters(raw_file_data)
    return min(map(lambda x: convert_seed_to_location(converters, x), seeds))


if __name__ == "__main__":
    print(part1())
