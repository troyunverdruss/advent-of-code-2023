use std::cmp::min;
use crate::day11::{grid_to_lines, parse_lines_to_grid, rotate_grid};
use crate::read_file_to_lines;

pub fn part1() -> i64 {
    let lines = read_file_to_lines("inputs/day13.txt");
    let line_groups: Vec<Vec<String>> = parse_lines_line_groupings(&lines);
    solve_part1(line_groups)
}

fn solve_part1(line_groups: Vec<Vec<String>>) -> i64 {
    line_groups
        .iter()
        .filter(|v| v.len() != 0)
        .map(|l| find_mirroring_score(l))
        .sum()
}

fn find_mirroring_score(lines: &Vec<String>) -> i64 {
    // Scan for horizontal mirroring
    // If there's mirroring, count lines before axis

    let (h_mirroring, rows) = check_for_horizontal_mirroring(lines, 0);
    if h_mirroring {
        // dbg!(100 * rows);
        return 100 * rows;
    }

    // Then rotate the lines
    let grid = parse_lines_to_grid(lines);
    let rotated_grid = rotate_grid(&grid);
    let v_lines = grid_to_lines(&rotated_grid);

    let (v_mirroring, v_rows) = check_for_horizontal_mirroring(&v_lines, 0);
    if v_mirroring {
        // dbg!(v_rows);
        return v_rows;
    }

    panic!("unable to find mirroring!")
}

fn check_for_horizontal_mirroring(lines: &Vec<String>, max_permitted_diff: i64) -> (bool, i64) {
    let h_line_count = lines.len();
    let mut h_mirroring = false;
    let mut h_rows_before = -1;
    for i in 0..(h_line_count - 1) {
        let mut mirroring = true;
        let mut total_diffs = count_diffs(lines.get(i).unwrap(), lines.get(i + 1).unwrap());

        if total_diffs <= max_permitted_diff {
            let steps = min(i, h_line_count - i - 2);
            for j in 0..steps {
                total_diffs += count_diffs(lines.get(i - 1 - j).unwrap(), lines.get(i + 2 + j).unwrap());

                if total_diffs > max_permitted_diff {
                    mirroring = false;
                    break;
                }
            }
            if mirroring {
                h_mirroring = true;
                h_rows_before = (i + 1) as i64;
            }
        }
    }

    (h_mirroring, h_rows_before)
}

fn count_diffs(l1: &str, l2: &str) -> i64 {
    let d1 = map_chars_to_binary(l1);
    let d2 = map_chars_to_binary(l2);

    let mut diffs = 0;
    for i in 0..d1.len() {
        diffs += (d2.get(i).unwrap() - d1.get(i).unwrap()).abs();
    }

    diffs
}

fn map_chars_to_binary(line: &str) -> Vec<i64> {
    line
        .chars()
        .map(|c| {
            match c {
                '.' => 0,
                '#' => 1,
                _ => panic!("Unexpected char")
            }
        })
        .collect()
}

fn parse_lines_line_groupings(lines: &Vec<String>) -> Vec<Vec<String>> {
    let mut line_groupings = Vec::new();

    let mut line_group = Vec::new();
    for line in lines {

        if line.is_empty() {
            line_groupings.push(line_group.clone());
            line_group.clear()
        } else {
            line_group.push(line.clone());
        }
    }

    line_groupings.push(line_group.clone());
    line_groupings
}

#[cfg(test)]
mod tests {
    use crate::day13::{find_mirroring_score, parse_lines_line_groupings, solve_part1};

    #[test]
    fn test_horizontal_mirror() {
        let lines = vec![
            "#...##..#".to_string(),
            "#....#..#".to_string(),
            "..##..###".to_string(),
            "#####.##.".to_string(),
            "#####.##.".to_string(),
            "..##..###".to_string(),
            "#....#..#".to_string(),
        ];
        assert_eq!(find_mirroring_score(&lines), 400);
    }

    #[test]
    fn test_vertical_mirror() {
        let lines = vec![
            "#.##..##.".to_string(),
            "..#.##.#.".to_string(),
            "##......#".to_string(),
            "##......#".to_string(),
            "..#.##.#.".to_string(),
            "..##..##.".to_string(),
            "#.#.##.#.".to_string(),
        ];
        assert_eq!(find_mirroring_score(&lines), 5);
    }

    #[test]
    fn test_full_example_part_1() {
        let lines = vec![
            "#.##..##.".to_string(),
            "..#.##.#.".to_string(),
            "##......#".to_string(),
            "##......#".to_string(),
            "..#.##.#.".to_string(),
            "..##..##.".to_string(),
            "#.#.##.#.".to_string(),
            "".to_string(),
            "#...##..#".to_string(),
            "#....#..#".to_string(),
            "..##..###".to_string(),
            "#####.##.".to_string(),
            "#####.##.".to_string(),
            "..##..###".to_string(),
            "#....#..#".to_string(),
        ];
        let line_groups = parse_lines_line_groupings(&lines);
        assert_eq!(solve_part1(line_groups), 405)
    }


}