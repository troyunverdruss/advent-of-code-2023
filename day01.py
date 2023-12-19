integers_as_str = list(map(lambda x: str(x), [1, 2, 3, 4, 5, 6, 7, 8, 9]))


def read_lines() -> [str]:
    with open("inputs/day1.txt") as f:
        return filter(lambda x: x != "", map(lambda x: x.strip(), f.readlines()))


def first_and_last_numeric_digits_as_num(line: str) -> int:
    digits = []
    for c in line:
        if c in integers_as_str:
            digits.append(int(c))

    return digits[0] * 10 + digits[-1]


def first_and_last_digits_as_num(line: str) -> int:
    first_digit = find_digit(line, True)
    last_digit = find_digit(line, False)

    return first_digit * 10 + last_digit


def find_digit(line, forward):
    replacements = {
        "one": "1", "two": "2", "three": "3",
        "four": "4", "five": "5", "six": "6",
        "seven": "7", "eight": "8", "nine": "9"
    }

    if forward:
        incr = 1
        i = 0
    else:
        incr = -1
        i = len(line) - 1

    found = False
    found_digit = -1
    while not found:
        if line[i] in integers_as_str:
            found_digit = int(line[i])
            found = True
        else:
            for k in replacements.keys():
                if line[i:i + len(k)] == k:
                    found_digit = int(replacements.get(k))
                    found = True
                    break
        i += incr
    return found_digit


def part1():
    calibrations = list(map(lambda x: first_and_last_numeric_digits_as_num(x.strip()), read_lines()))
    sum_calibrations = sum(calibrations)
    return sum_calibrations


def part2(lines: [str]):
    calibrations = list(map(lambda x: first_and_last_digits_as_num(x), lines))
    sum_calibrations = sum(calibrations)
    return sum_calibrations
    # 56884, 53066, 53519 too high


if __name__ == "__main__":
    print(part1())
    print(part2(read_lines()))
