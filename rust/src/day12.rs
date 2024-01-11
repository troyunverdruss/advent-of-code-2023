use std::cmp::{max, min};
use itertools::Itertools;
use memoize::memoize;

use crate::read_file_to_lines;

pub fn part1() -> i64 {
    let lines = read_file_to_lines("inputs/day12.txt")
        .iter()
        .filter(|l| !l.is_empty())
        .map(|l| l.clone())
        .collect();
    solve_part1(lines)
}

pub fn part2() -> i64 {
    let lines = read_file_to_lines("inputs/day12.txt")
        .iter()
        .filter(|l| !l.is_empty())
        .map(|l| l.clone())
        .collect();
    solve_part2(lines)
}

fn solve_part1(lines: Vec<String>) -> i64 {
    let condition_records: Vec<ConditionRecord> = lines
        .iter()
        .map(|l| ConditionRecord::parse_line_to_record(l))
        .collect();

    condition_records
        .iter()
        .map(|cr| compute_arrangements(cr.clone(), 0))
        .sum()
}

fn solve_part2(lines: Vec<String>) -> i64 {
    let condition_records: Vec<ConditionRecord> = lines
        .iter()
        .map(|l| ConditionRecord::parse_line_to_record(l))
        .collect();

    condition_records
        .iter()
        .map(|cr| compute_arrangements(cr.expand_for_part2(), 0))
        .sum()
}

#[memoize]
fn compute_arrangements(condition_record: ConditionRecord, depth: i64) -> i64 {
    let mut result = 0;
    if condition_record.check_data.len() == 0 {
        let field_has_required = condition_record.field.contains(&'#');
        return if field_has_required {
            0
        } else {
            1
        };
    }
    let check_number = *condition_record.check_data.first().unwrap();
    let remaining_check_data = clone_sublist(&condition_record.check_data, 1, 1_000_000);

    let range_end: i64 = condition_record.field.len() as i64
        - remaining_check_data.iter().sum::<i64>()
        - remaining_check_data.len() as i64
        - check_number
        + 1;

    let range_end = max(0, range_end) as usize;
    for i in 0..range_end {
        let slice = &condition_record.field[0..i];
        if slice.contains(&'#') {
            break;
        }
        let nxt = i + check_number as usize;
        if nxt <= condition_record.field.len() {
            if !&condition_record.field[i..nxt].contains(&'.') {
                let is_at_end = nxt + 1 > condition_record.field.len();
                if is_at_end || !&condition_record.field[nxt..nxt + 1].contains(&'#') {
                    result += compute_arrangements(
                        ConditionRecord {
                            field: clone_sublist(&condition_record.field, nxt + 1, 1_000_000),
                            check_data: remaining_check_data.clone(),
                        },
                        depth + 1,
                    );
                }
            }
        }
    }

    result
}


fn clone_sublist<T: Clone>(list: &Vec<T>, start: usize, end_exclusive: usize) -> Vec<T> {
    let actual_end = min(end_exclusive, list.len());
    if start >= actual_end {
        vec![]
    } else {
        list[start..actual_end]
            .iter()
            .map(|v: &T| v.clone())
            .collect()
    }
}

fn get_or_default<T: Clone>(list: &Vec<T>, index: usize, default: T) -> T {
    let opt = list.get(index);
    match opt {
        None => { default }
        Some(v) => { v.clone() }
    }
}

#[derive(Debug, Eq, PartialEq, Hash, Clone)]
struct ConditionRecord {
    field: Vec<char>,
    check_data: Vec<i64>,
}

impl ConditionRecord {
    fn parse_line_to_record(line: &str) -> ConditionRecord {
        let parts: Vec<&str> = line.split(" ").collect();
        assert_eq!(parts.len(), 2);

        let field: Vec<char> = parts
            .get(0).unwrap()
            .chars()
            .collect();
        let check_data: Vec<i64> = parts
            .get(1).unwrap()
            .split(",")
            .map(|i| i.parse::<i64>().unwrap())
            .collect();

        ConditionRecord { field, check_data }
    }

    fn expand_for_part2(&self) -> ConditionRecord {
        let field_str = self.field.iter().map(|c| c.to_string()).join("");
        let check_data_str = self.check_data.iter().join(",").to_string();

        let long_field_str = format!("{}?{}?{}?{}?{}", field_str, field_str, field_str, field_str, field_str);
        let long_check_data_str = format!("{},{},{},{},{}", check_data_str, check_data_str, check_data_str, check_data_str, check_data_str);

        ConditionRecord {
            field: long_field_str.chars().collect(),
            check_data: long_check_data_str.split(",").map(|i| i.parse::<i64>().unwrap()).collect(),
        }
    }
}

#[cfg(test)]
mod tests {
    use itertools::enumerate;
    use crate::day12::{compute_arrangements, ConditionRecord, solve_part1, solve_part2};

    #[test]
    fn test_parse() {
        let line = ".#?? 1,1";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(record.field, vec!['.', '#', '?', '?']);
        assert_eq!(record.check_data, vec![1, 1]);
    }

    #[test]
    fn test_base() {
        let line = "# 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 1)
    }

    #[test]
    fn test_base_2() {
        let line = "## 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 0)
    }

    #[test]
    fn test_base_2_with_1_wild() {
        let line = "#? 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 1)
    }

    #[test]
    fn test_base_2_with_2_wild() {
        let line = "?? 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 2)
    }

    #[test]
    fn test_base_3_will_fail() {
        let line = "### 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 0)
    }

    #[test]
    fn test_base_3_will_pass() {
        let line = "??? 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 3)
    }

    #[test]
    fn test_recurse_1_no_fit() {
        let line = "#.## 1,1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 0)
    }

    #[test]
    fn test_simple_compute_arrangement_2() {
        let line = "??? 1,1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 1)
    }

    #[test]
    fn test_simple_compute_arrangement_2_with_fixed_front() {
        let line = "##.?? 2,1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 2)
    }

    #[test]
    fn test_simple_compute_arrangement_2_with_fixed_end() {
        let line = "??.## 1,2";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 2)
    }

    #[test]
    fn test_all_wild() {
        // #.##.
        // #..##
        // .#.##
        let line = "????? 1,2";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 3)
    }

    #[test]
    fn test_all_wild_longer() {
        // #.##.###..
        // #.##..###.
        // #.##...###
        // #..##.###.
        // #..##..###
        // #...##.###
        // .#.##.###.
        // .#.##..###
        // .#..##.###
        // ..#.##.###
        let line = "?????????? 1,2,3";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(record, 0);
        assert_eq!(arrangements, 10)
    }

    #[test]
    fn test_part1_example_records() {
        let lines = vec![
            "???.### 1,1,3",
            ".??..??...?##. 1,1,3",
            // "?#?#?#?#?#?#?#? 1,3,1,6",
            "????.#...#... 4,1,1",
            "????.######..#####. 1,6,5",
            // "?###???????? 3,2,1",
        ];
        let expected_results = vec![
            1,
            4,
            // 1,
            1,
            4,
            // 10,
        ];
        for (idx, line) in enumerate(lines) {
            println!("Testing index: {}", idx);
            let record = ConditionRecord::parse_line_to_record(line);
            assert_eq!(compute_arrangements(record, 0), *expected_results.get(idx).unwrap())
        }
    }

    #[test]
    fn test_solve_part1() {
        let lines = vec![
            "???.### 1,1,3".to_string(),
            ".??..??...?##. 1,1,3".to_string(),
            "?#?#?#?#?#?#?#? 1,3,1,6".to_string(),
            "????.#...#... 4,1,1".to_string(),
            "????.######..#####. 1,6,5".to_string(),
            "?###???????? 3,2,1".to_string(),
        ];
        assert_eq!(solve_part1(lines), 21);
    }

    #[test]
    fn test_solve_part2() {
        let lines = vec![
            "???.### 1,1,3".to_string(),
            ".??..??...?##. 1,1,3".to_string(),
            "?#?#?#?#?#?#?#? 1,3,1,6".to_string(),
            "????.#...#... 4,1,1".to_string(),
            "????.######..#####. 1,6,5".to_string(),
            "?###???????? 3,2,1".to_string(),
        ];
        assert_eq!(solve_part2(lines), 525152);
    }

    #[test]
    fn test_part1_example_problem_record1() {
        let line = "?#?#?#?#?#?#?#? 1,3,1,6";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(compute_arrangements(record, 0), 1)
    }

    #[test]
    fn test_part1_example_problem_record2() {
        let line = "?###???????? 3,2,1";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(compute_arrangements(record, 0), 10)
    }

    #[test]
    fn test_more() {
        // .??????.?##.
        // .#......###.
        // ..#.....###.
        // ...#....###.
        // ....#...###.
        // .....#..###.
        // ......#.###.
        let line = ".??????.?##. 1,3";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(compute_arrangements(record, 0), 6)
    }

    #[test]
    fn test_more2() {
        // .??????.?#.?##. 1,2,3
        // .#......##.###.
        // ..#.....##.###.
        // ...#....##.###.
        // ....#...##.###.
        // .....#..##.###.
        // ......#.##.###.
        let line = ".??????.?#.?##. 1,2,3";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(compute_arrangements(record, 0), 6)
    }

    #[test]
    fn test_more3() {
        // .?????.#. 1,1,1
        // .#.#...#.
        // .#..#..#.
        // .#...#.#.
        // ..#.#..#.
        // ..#..#.#.
        // ...#.#.#.
        let line = ".?????.?#. 1,1,2";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(compute_arrangements(record, 0), 6)
    }
}