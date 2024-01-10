use std::cmp::min;
use crate::read_file_to_lines;

pub fn part1() -> i64 {
    let lines = read_file_to_lines("inputs/day12.txt");
    solve_part1(lines)
}

pub fn part2() -> i64 {
    todo!("part 2 not done")
}

fn solve_part1(lines: Vec<String>) -> i64 {
    let condition_records: Vec<ConditionRecord> = lines
        .iter()
        .map(|l| ConditionRecord::parse_line_to_record(l))
        .collect();

    condition_records
        .iter()
        .map(|cr| compute_arrangements(cr, 0))
        .sum()
}

fn compute_arrangements(condition_record: &ConditionRecord, depth: i64) -> i64 {
    dbg!(depth);
    if condition_record.check_data.len() == 0
        && condition_record.field.contains(&'#') {
        panic!("got here 1");
        return 0;
    }
    if condition_record.check_data.len() > 0
        && condition_record.field.len() == 0 {
        return 0;
    }

    let check_number = *condition_record.check_data.first().unwrap() as usize;
    let remaining_check_data = clone_sublist(&condition_record.check_data, 1, condition_record.check_data.len());

    if check_number > condition_record.field.len() {
        return 0;
    }

    let mut arrangements = 0;
    let mut possible_arrangements = 0;
    let mut found_mandatory_start_pos = false;
    let mut found_any_start = false;
    for i in 0..=condition_record.field.len() - check_number {
        println!("depth: {}, index: {}", depth, i);
        let slice = &condition_record.field[i..i + check_number];
        if found_mandatory_start_pos {
            break;
        }

        if slice.contains(&'.') && found_any_start {
            break;
        }

        if slice.contains(&'.') {
            continue;
        }

        if slice.get(0).unwrap() == &'#' {
            found_mandatory_start_pos = true;
        }

        found_any_start = true;
        if remaining_check_data.len() == 0 {
            let slice_remainder = &condition_record.field[i + check_number..condition_record.field.len()];

            if !slice_remainder.contains(&'#') {
                arrangements += 1;
            }
        } else {
            // recurse

            let sub_arrangements = compute_arrangements(&ConditionRecord {
                field: clone_sublist(&condition_record.field, 1 + i + check_number, condition_record.field.len()),
                check_data: remaining_check_data.clone(),
            }, depth + 1);
            if sub_arrangements > 0 {
                possible_arrangements += 1;
                arrangements += sub_arrangements;
            }
        }
    }
    arrangements
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

fn compute_arrangements1(condition_record: &ConditionRecord, depth: i64) -> i64 {
    dbg!("depth: ", depth);
    dbg!(condition_record);
    let mut total_arrangements = 0;
    // // Are there any more check numbers? If not we're definitely done
    //
    // if condition_record.check_data.is_empty() {
    //     return total_arrangements;
    // }

    // Get the next check data number
    let check_number = condition_record.check_data.get(0).unwrap();

    let check_data_new: Vec<i64> = clone_sublist(&condition_record.check_data, 1, condition_record.check_data.len());

    // condition_record
    // .check_data[1..condition_record.check_data.len()]
    // .iter()
    // .map(|v| *v)
    // .collect();


    // Find/go through all the field positions to figure out where it can go
    let mut found_possible_loc = false;
    for i in 0..=(condition_record.field.len() - *check_number as usize) {
        let slice = &condition_record.field[i..(i + *check_number as usize)];
        // Here we're "fast forwarding" to get to a possible starting location
        // because we haven't found a possible place for this check number yet
        if slice.contains(&'.') && !found_possible_loc {
            continue;
        }
        // Here we've already found a possible location and so we need to
        // stop going entirely
        if slice.contains(&'.') && found_possible_loc {
            break;
        }

        // Logically we should be at least at a starting location now
        // But only if the next field position is # or ? OR we're at the end

        let next_field_position = 1 + i + *check_number as usize;
        let next_field_value = condition_record.field.get(next_field_position);
        if next_field_position >= condition_record.field.len()
            || next_field_value.unwrap() == &'#'
            || next_field_value.unwrap() == &'?' {
            dbg!("Incrementing, field pos: ", next_field_position);
            found_possible_loc = true;
            total_arrangements += 1;


            // For each field position it can *START*, pass the remaining sublist
            //   to this function again with the check data removed and only the
            //   remaining field data
            let slice_idx_start = 1 + i + *check_number as usize;
            let slice_idx_end = condition_record.field.len();

            if slice_idx_start < condition_record.field.len() {
                let field_new: Vec<char> = condition_record
                    .field[slice_idx_start..slice_idx_end]
                    .iter()
                    .map(|c| *c)
                    .collect();
                // total_arrangements += compute_arrangements(
                //     &ConditionRecord {
                //         field: field_new,
                //         check_data: check_data_new.clone(),
                //     },
                //     depth + 1,
                // );
            }
            // When no more check numbers exist and all permutations have been tested
            //   start returning
            // each return should be something like: (total + result())
        }
    }

    total_arrangements
}

#[derive(Debug, Eq, PartialEq)]
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
}

#[cfg(test)]
mod tests {
    use itertools::enumerate;
    use crate::day12::{compute_arrangements, ConditionRecord};

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
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 1)
    }

    #[test]
    fn test_base_2() {
        let line = "## 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 0)
    }

    #[test]
    fn test_base_2_with_1_wild() {
        let line = "#? 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 1)
    }

    #[test]
    fn test_base_2_with_2_wild() {
        let line = "?? 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 2)
    }

    #[test]
    fn test_base_3_will_fail() {
        let line = "### 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 0)
    }

    #[test]
    fn test_base_3_will_pass() {
        let line = "??? 1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 3)
    }

    #[test]
    fn test_recurse_1_no_fit() {
        let line = "#.## 1,1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 0)
    }

    #[test]
    fn test_simple_compute_arrangement_2() {
        let line = "??? 1,1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 1)
    }

    #[test]
    fn test_simple_compute_arrangement_2_with_fixed_front() {
        let line = "##.?? 2,1";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 2)
    }

    #[test]
    fn test_simple_compute_arrangement_2_with_fixed_end() {
        let line = "??.## 1,2";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 2)
    }

    #[test]
    fn test_all_wild() {
        // #.##.
        // #..##
        // .#.##
        let line = "????? 1,2";
        let record = ConditionRecord::parse_line_to_record(line);
        let arrangements = compute_arrangements(&record, 0);
        assert_eq!(arrangements, 3)
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
            assert_eq!(compute_arrangements(&record, 0), *expected_results.get(idx).unwrap())
        }
    }

    #[test]
    fn test_part1_example_problem_record1() {
        let line = "?#?#?#?#?#?#?#? 1,3,1,6";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(compute_arrangements(&record, 0), 1)
    }

    #[test]
    fn test_part1_example_problem_record2() {
        let line = "?###???????? 3,2,1";
        let record = ConditionRecord::parse_line_to_record(line);
        assert_eq!(compute_arrangements(&record, 0), 10)
    }
}