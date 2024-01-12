use std::collections::HashMap;
use itertools::Itertools;
use crate::{parse_lines_to_grid, Point, read_file_to_lines};
use crate::day14::Direction::{East, North, South, West};

pub fn part1() -> i64 {
    let lines = read_file_to_lines("inputs/day14.txt");
    let grid = parse_lines_to_grid(&lines);
    let tilted_grid = tilt(&grid, North);
    compute_load(&tilted_grid)
}

pub fn part2() -> i64 {
    let lines = read_file_to_lines("inputs/day14.txt");
    let grid = parse_lines_to_grid(&lines);

    solve_part_2(&grid)
}

fn solve_part_2(grid: &HashMap<Point, char>) -> i64 {
    let goal_cycles = 1_000_000_000;
    let mut grid = grid.clone();

    let mut seen_configs: HashMap<String, i64> = HashMap::new();
    let mut cycle_count = 0;
    let mut seen_count = 0;
    while true {
        grid = cycle(&grid);
        cycle_count += 1;
        let hash = hash_grid(&grid);
        if seen_configs.contains_key(&hash) {
            let cycle_length = cycle_count - seen_configs.get(&hash).unwrap();
            println!("{}: Previously seen, delta: {}", cycle_count, cycle_length);
            seen_count += 1;

            let cycles_left_before_equiv = (goal_cycles - cycle_count) % cycle_length;
            for i in 0..cycles_left_before_equiv {
                grid = cycle(&grid);
            }

            return compute_load(&grid);
        }
        seen_configs.insert(hash, cycle_count);
        if seen_count > 20 || cycle_count > 1_000 {
            break;
        }
    }

    panic!("Should have found something repeating by now")
}

fn hash_grid(grid: &HashMap<Point, char>) -> String {
    grid
        .iter()
        .filter(|(_, v)| v == &&'O')
        .sorted_by_key(|(k, _)| (k.x, k.y))
        .map(|(k, _)| format!("{},{}", k.x, k.y))
        .join(" ")
}


fn cycle(grid: &HashMap<Point, char>) -> HashMap<Point, char> {
    let titled_grid = tilt(&grid, North);
    let titled_grid = tilt(&titled_grid, West);
    let titled_grid = tilt(&titled_grid, South);
    let titled_grid = tilt(&titled_grid, East);

    titled_grid
}

fn tilt(grid: &HashMap<Point, char>, dir: Direction) -> HashMap<Point, char> {
    let mut tilted_grid: HashMap<Point, char> = grid
        .iter()
        .filter(|(_, v)| v == &&'#')
        .map(|(k, v)| (k.clone(), *v))
        .collect();

    let rolling_stones: Vec<&Point> = grid
        .iter()
        .filter(|(_, v)| v == &&'O')
        .sorted_by_key(|(k, _)| match dir {
            North => (k.y, k.x),
            East => (-k.x, k.y),
            South => (-k.y, k.x),
            West => (k.x, k.y),
        })
        .map(|(k, _)| k)
        .collect();

    let limit = match dir {
        North => 0,
        East => grid.keys().map(|k| k.x).max().unwrap(),
        South => grid.keys().map(|k| k.y).max().unwrap(),
        West => 0,
    };
    for stone in rolling_stones {
        match dir {
            North => {
                let mut curr_pos = stone.clone();
                let mut next_pos = curr_pos + dir.point_vec();
                let mut next_pos_val = tilted_grid.get(&next_pos);
                while next_pos.y >= limit && next_pos_val.unwrap_or(&'.') == &'.' {
                    curr_pos = next_pos;
                    next_pos = curr_pos + dir.point_vec();
                    next_pos_val = tilted_grid.get(&next_pos);
                }
                tilted_grid.insert(curr_pos, 'O');
            }
            South => {
                let mut curr_pos = stone.clone();
                let mut next_pos = curr_pos + dir.point_vec();
                let mut next_pos_val = tilted_grid.get(&next_pos);
                while next_pos.y <= limit && next_pos_val.unwrap_or(&'.') == &'.' {
                    curr_pos = next_pos;
                    next_pos = curr_pos + dir.point_vec();
                    next_pos_val = tilted_grid.get(&next_pos);
                }
                tilted_grid.insert(curr_pos, 'O');
            }
            West => {
                let mut curr_pos = stone.clone();
                let mut next_pos = curr_pos + dir.point_vec();
                let mut next_pos_val = tilted_grid.get(&next_pos);
                while next_pos.x >= limit && next_pos_val.unwrap_or(&'.') == &'.' {
                    curr_pos = next_pos;
                    next_pos = curr_pos + dir.point_vec();
                    next_pos_val = tilted_grid.get(&next_pos);
                }
                tilted_grid.insert(curr_pos, 'O');
            }
            East => {
                let mut curr_pos = stone.clone();
                let mut next_pos = curr_pos + dir.point_vec();
                let mut next_pos_val = tilted_grid.get(&next_pos);
                while next_pos.x <= limit && next_pos_val.unwrap_or(&'.') == &'.' {
                    curr_pos = next_pos;
                    next_pos = curr_pos + dir.point_vec();
                    next_pos_val = tilted_grid.get(&next_pos);
                }
                tilted_grid.insert(curr_pos, 'O');
            }
        }
    }

    tilted_grid
}

fn compute_load(grid: &HashMap<Point, char>) -> i64 {
    let max_y = grid.keys().map(|k| k.y).max().unwrap();
    grid
        .iter()
        .filter(|(_, v)| v == &&'O')
        .map(|(k, _)| ((max_y + 1) - k.y))
        .sum()
}

enum Direction {
    North,
    East,
    South,
    West,
}

impl Direction {
    fn point_vec(&self) -> Point {
        match self {
            North => Point { x: 0, y: -1 },
            East => Point { x: 1, y: 0 },
            South => Point { x: 0, y: 1 },
            West => Point { x: -1, y: 0 },
        }
    }
}

#[cfg(test)]
mod tests {
    use crate::day14::Direction::North;
    use crate::day14::{compute_load, cycle, solve_part_2, tilt};
    use crate::{dbg_print_grid, parse_lines_to_grid};

    #[test]
    fn test_example_1() {
        let lines = vec![
            "O....#....".to_string(),
            "O.OO#....#".to_string(),
            ".....##...".to_string(),
            "OO.#O....O".to_string(),
            ".O.....O#.".to_string(),
            "O.#..O.#.#".to_string(),
            "..O..#O..O".to_string(),
            ".......O..".to_string(),
            "#....###..".to_string(),
            "#OO..#....".to_string(),
        ];

        let grid = parse_lines_to_grid(&lines);
        let tilted_grid = tilt(&grid, North);
        assert_eq!(compute_load(&tilted_grid), 136);
    }

    #[test]
    fn test_part2_1_cycle() {
        let lines = vec![
            "O....#....".to_string(),
            "O.OO#....#".to_string(),
            ".....##...".to_string(),
            "OO.#O....O".to_string(),
            ".O.....O#.".to_string(),
            "O.#..O.#.#".to_string(),
            "..O..#O..O".to_string(),
            ".......O..".to_string(),
            "#....###..".to_string(),
            "#OO..#....".to_string(),
        ];
        let expected_lines = vec![
            ".....#....".to_string(),
            "....#...O#".to_string(),
            "...OO##...".to_string(),
            ".OO#......".to_string(),
            ".....OOO#.".to_string(),
            ".O#...O#.#".to_string(),
            "....O#....".to_string(),
            "......OOOO".to_string(),
            "#...O###..".to_string(),
            "#..OO#....".to_string(),
        ];

        let grid = parse_lines_to_grid(&lines);
        let after_cycle = cycle(&grid);
        dbg_print_grid(&after_cycle);
        assert_eq!(compute_load(&after_cycle), compute_load(&parse_lines_to_grid(&expected_lines)));
    }

    #[test]
    fn test_part2_2_cycle() {
        let lines = vec![
            "O....#....".to_string(),
            "O.OO#....#".to_string(),
            ".....##...".to_string(),
            "OO.#O....O".to_string(),
            ".O.....O#.".to_string(),
            "O.#..O.#.#".to_string(),
            "..O..#O..O".to_string(),
            ".......O..".to_string(),
            "#....###..".to_string(),
            "#OO..#....".to_string(),
        ];
        let expected_lines = vec![
            ".....#....".to_string(),
            "....#...O#".to_string(),
            ".....##...".to_string(),
            "..O#......".to_string(),
            ".....OOO#.".to_string(),
            ".O#...O#.#".to_string(),
            "....O#...O".to_string(),
            ".......OOO".to_string(),
            "#..OO###..".to_string(),
            "#.OOO#...O".to_string(),
        ];

        let grid = parse_lines_to_grid(&lines);
        let after_cycle = cycle(&grid);
        let after_cycle = cycle(&after_cycle);
        dbg_print_grid(&after_cycle);
        assert_eq!(compute_load(&after_cycle), compute_load(&parse_lines_to_grid(&expected_lines)));
    }

    #[test]
    fn test_part2_3_cycle() {
        let lines = vec![
            "O....#....".to_string(),
            "O.OO#....#".to_string(),
            ".....##...".to_string(),
            "OO.#O....O".to_string(),
            ".O.....O#.".to_string(),
            "O.#..O.#.#".to_string(),
            "..O..#O..O".to_string(),
            ".......O..".to_string(),
            "#....###..".to_string(),
            "#OO..#....".to_string(),
        ];
        let expected_lines = vec![
            ".....#....".to_string(),
            "....#...O#".to_string(),
            ".....##...".to_string(),
            "..O#......".to_string(),
            ".....OOO#.".to_string(),
            ".O#...O#.#".to_string(),
            "....O#...O".to_string(),
            ".......OOO".to_string(),
            "#...O###.O".to_string(),
            "#.OOO#...O".to_string(),
        ];

        let grid = parse_lines_to_grid(&lines);
        let after_cycle = cycle(&grid);
        let after_cycle = cycle(&after_cycle);
        let after_cycle = cycle(&after_cycle);
        dbg_print_grid(&after_cycle);
        assert_eq!(compute_load(&after_cycle), compute_load(&parse_lines_to_grid(&expected_lines)));
    }

    #[test]
    fn test_part2_all_cycles() {
        let lines = vec![
            "O....#....".to_string(),
            "O.OO#....#".to_string(),
            ".....##...".to_string(),
            "OO.#O....O".to_string(),
            ".O.....O#.".to_string(),
            "O.#..O.#.#".to_string(),
            "..O..#O..O".to_string(),
            ".......O..".to_string(),
            "#....###..".to_string(),
            "#OO..#....".to_string(),
        ];

        let grid = parse_lines_to_grid(&lines);
        let result = solve_part_2(&grid);
        assert_eq!(result, 64);
    }
}