from dataclasses import dataclass


def read_raw_file_data(path):
    with open(path) as f:
        return f.read()


def parse_input(raw_file_data):
    data = []
    for line in raw_file_data.split("\n"):
        if line == '':
            continue
        (a, b) = line.split(' ')
        data.append((a, list(map(int, b.split(",")))))
    return data


def recurse(field, checks):
    result = 0
    if len(checks) == 0:
        field_has_required = '#' in field
        if field_has_required:
            return 0
        else:
            return 1

    current_check, remaining_checks = checks[0], checks[1:]
    for i in range(
            len(field) - sum(remaining_checks) - len(remaining_checks) - current_check + 1
    ):
        if '#' in field[:i]:
            break
        nxt = i + current_check
        if nxt <= len(field):
            if '.' not in field[i:nxt]:
                if field[nxt:nxt + 1] != '#':
                    result += recurse(field[nxt + 1:], remaining_checks)
    return result


def part1():
    raw_file_data = read_raw_file_data("../inputs/day12.txt")
    data = parse_input(raw_file_data)

    total = 0
    for (g_field, g_checks) in data:
        # print(field, checks)
        total += recurse(g_field, g_checks)
    return total


if __name__ == "__main__":
    print(part1())
