use std::cmp::min;
use crate::day11::{grid_to_lines, parse_lines_to_grid, rotate_grid};
use crate::day13::Dir::{HORIZONTAL, VERTICAL};
use crate::read_file_to_lines;

pub fn part1() -> i64 {
    let lines = read_file_to_lines("inputs/day13.txt");
    let line_groups: Vec<Vec<String>> = parse_lines_line_groupings(&lines);
    solve_part1(line_groups)
}

pub fn part2() -> i64 {
    let lines = read_file_to_lines("inputs/day13.txt");
    let line_groups: Vec<Vec<String>> = parse_lines_line_groupings(&lines);
    solve_part2(line_groups)
}

fn solve_part1(line_groups: Vec<Vec<String>>) -> i64 {
    line_groups
        .iter()
        .filter(|v| v.len() != 0)
        .map(|l| find_mirroring_data(l, 0, None))
        .map(|md| match md.dir {
            HORIZONTAL => 100 * md.num_row_col,
            VERTICAL => md.num_row_col
        })
        .sum()
}

fn solve_part2(line_groups: Vec<Vec<String>>) -> i64 {
    let part1_mirror_data: Vec<MirrorData> = line_groups
        .iter()
        .filter(|v| v.len() != 0)
        .map(|l| find_mirroring_data(l, 0, None))
        .collect();

    line_groups
        .iter()
        .enumerate()
        .filter(|(_, v)| v.len() != 0)
        .map(|(idx, l)| {
            find_mirroring_data(l, 1, Some(part1_mirror_data.get(idx).unwrap().clone()))
        })
        .map(|md| match md.dir {
            HORIZONTAL => 100 * md.num_row_col,
            VERTICAL => md.num_row_col
        })
        .sum()
}

#[derive(Hash, Eq, PartialEq, Clone, Debug)]
enum Dir {
    HORIZONTAL,
    VERTICAL,
}

#[derive(Hash, Eq, PartialEq, Clone, Debug)]
struct MirrorData {
    dir: Dir,
    num_row_col: i64,
}

fn find_mirroring_data(lines: &Vec<String>, max_permitted_diffs: i64, disallowed_mirror: Option<MirrorData>) -> MirrorData {
    // Scan for horizontal mirroring
    // If there's mirroring, count lines before axis

    let h_disallowed_mirror = match &disallowed_mirror {
        None => None,
        Some(md) => if md.dir == HORIZONTAL {
            Some(md.num_row_col)
        } else {
            None
        }
    };
    let (h_mirroring, rows) = check_for_horizontal_mirroring(
        lines,
        max_permitted_diffs,
        h_disallowed_mirror,
    );
    if h_mirroring {
        // dbg!(100 * rows);
        // return 100 * rows;
        return MirrorData { dir: HORIZONTAL, num_row_col: rows };
    }

    // Then rotate the lines
    let grid = parse_lines_to_grid(lines);
    let rotated_grid = rotate_grid(&grid);
    let v_lines = grid_to_lines(&rotated_grid);

    let v_disallowed_mirror = match &disallowed_mirror {
        None => None,
        Some(md) => if md.dir == VERTICAL {
            Some(md.num_row_col)
        } else {
            None
        }
    };
    let (v_mirroring, v_rows) = check_for_horizontal_mirroring(
        &v_lines,
        max_permitted_diffs,
        v_disallowed_mirror,
    );
    if v_mirroring {
        // dbg!(v_rows);
        // return v_rows;
        return MirrorData { dir: VERTICAL, num_row_col: v_rows };
    }

    panic!("unable to find mirroring!")
}

fn check_for_horizontal_mirroring(lines: &Vec<String>, max_permitted_diff: i64, disallowed_mirror_row_count: Option<i64>) -> (bool, i64) {
    let h_line_count = lines.len();
    let mut h_mirroring = false;
    let mut h_rows_before = -1;
    for i in 0..(h_line_count - 1) {
        let mut mirroring = true;
        let mut total_diffs = count_diffs(lines.get(i).unwrap(), lines.get(i + 1).unwrap());

        if total_diffs <= max_permitted_diff && Some((i + 1) as i64) != disallowed_mirror_row_count {
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
    use crate::day13::{find_mirroring_data, MirrorData, parse_lines_line_groupings, solve_part1, solve_part2};
    use crate::day13::Dir::{HORIZONTAL, VERTICAL};

    #[test]
    fn test_pattern1_horizontal_mirror() {
        let lines = vec![
            "#...##..#".to_string(),
            "#....#..#".to_string(),
            "..##..###".to_string(),
            "#####.##.".to_string(),
            "#####.##.".to_string(),
            "..##..###".to_string(),
            "#....#..#".to_string(),
        ];
        assert_eq!(find_mirroring_data(&lines, 0, None), MirrorData { dir: HORIZONTAL, num_row_col: 4 });
    }

    #[test]
    fn test_pattern1_horizontal_mirror_part2() {
        let lines = vec![
            "#...##..#".to_string(),
            "#....#..#".to_string(),
            "..##..###".to_string(),
            "#####.##.".to_string(),
            "#####.##.".to_string(),
            "..##..###".to_string(),
            "#....#..#".to_string(),
        ];
        let part1_md = MirrorData { dir: HORIZONTAL, num_row_col: 4 };
        assert_eq!(
            find_mirroring_data(
                &lines,
                1,
                Some(part1_md),
            ),
            MirrorData { dir: HORIZONTAL, num_row_col: 1 });
    }

    #[test]
    fn test_pattern2_vertical_mirror() {
        let lines = vec![
            "#.##..##.".to_string(),
            "..#.##.#.".to_string(),
            "##......#".to_string(),
            "##......#".to_string(),
            "..#.##.#.".to_string(),
            "..##..##.".to_string(),
            "#.#.##.#.".to_string(),
        ];
        assert_eq!(find_mirroring_data(&lines, 0, None), MirrorData { dir: VERTICAL, num_row_col: 5 });
    }

    #[test]
    fn test_pattern2_now_horizontal_mirror_part2() {
        let lines = vec![
            "#.##..##.".to_string(),
            "..#.##.#.".to_string(),
            "##......#".to_string(),
            "##......#".to_string(),
            "..#.##.#.".to_string(),
            "..##..##.".to_string(),
            "#.#.##.#.".to_string(),
        ];
        let part1_md = MirrorData { dir: VERTICAL, num_row_col: 5 };
        assert_eq!(
            find_mirroring_data(
                &lines,
                1,
                Some(part1_md),
            ),
            MirrorData { dir: HORIZONTAL, num_row_col: 3 }
        );
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

    #[test]
    fn test_full_example_part_2() {
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
        assert_eq!(solve_part2(line_groups), 400)
    }
}