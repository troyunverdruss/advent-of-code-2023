integers_as_str = list(map(lambda x: str(x), [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]))


def read_lines() -> [str]:
    with open("inputs/day1.txt") as f:
        return f.readlines()


def first_and_last_as_num(line: str) -> int:
    digits = []
    for c in line:
        if c in integers_as_str:
            digits.append(int(c))

    return digits[0] * 10 + digits[-1]


def part1():
    calibrations = list(map(lambda x: first_and_last_as_num(x), read_lines()))
    sum_calibrations = sum(calibrations)
    return sum_calibrations


if __name__ == "__main__":
    print(part1())
