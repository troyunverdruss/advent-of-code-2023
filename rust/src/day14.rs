use std::collections::HashMap;
use itertools::Itertools;
use crate::{parse_lines_to_grid, Point, read_file_to_lines};
use crate::day14::Direction::North;

pub fn part1() -> i64 {
    let lines = read_file_to_lines("inputs/day14.txt");
    let grid  = parse_lines_to_grid(&lines);
    let tilted_grid = tilt(&grid, North);
    compute_load(&tilted_grid, North)
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
        .sorted_by_key(|(k, _)| (k.y, k.x))
        .map(|(k, _)| k)
        .collect();

    let limit = match dir {
        Direction::North => 0,
        Direction::East => todo!(),
        Direction::South => todo!(),
        Direction::West => todo!(),
    };
    for stone in rolling_stones {
        match dir {
            Direction::North => {
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
            Direction::South | Direction::East | Direction::West => {
                todo!()
            }
        }
    }

    tilted_grid
}

fn compute_load(grid: &HashMap<Point, char>, dir: Direction) -> i64 {
    match dir {
        Direction::North => {
            let max_y = grid.keys().map(|k| k.y).max().unwrap();
            grid
                .iter()
                .filter(|(_, v)| v == &&'O')
                .map(|(k, _)| ((max_y + 1) - k.y))
                .sum()
        }
        Direction::East => { todo!() }
        Direction::South => { todo!() }
        Direction::West => { todo!() }
    }
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
            Direction::North => Point { x: 0, y: -1 },
            Direction::East => Point { x: 1, y: 0 },
            Direction::South => Point { x: 0, y: 1 },
            Direction::West => Point { x: -1, y: 0 },
        }
    }
}

#[cfg(test)]
mod tests {
    use crate::day14::Direction::North;
    use crate::day14::{compute_load, tilt};
    use crate::parse_lines_to_grid;

    #[test]
    fn test() {
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
        assert_eq!(compute_load(&tilted_grid, North), 136);
    }
}