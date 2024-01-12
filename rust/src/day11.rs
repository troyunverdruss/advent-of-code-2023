use std::collections::HashMap;
use itertools::{enumerate, Itertools};
use crate::Point;

pub fn part1() -> i64 {
    let path = "inputs/day11.txt";
    let lines = crate::read_file_to_lines(path);

    solve_part1(lines)
}

pub fn part2() -> i64 {
    let path = "inputs/day11.txt";
    let lines = crate::read_file_to_lines(path);

    solve_part2(lines, 1_000_000)
}

fn solve_part1(lines: Vec<String>) -> i64 {
    let universe = expand_universe(&lines);
    let galaxies: Vec<Point> = universe
        .iter()
        .filter(|(_, v)| v == &&'#')
        .map(|(k, _)| k.clone())
        .collect();

    galaxies
        .iter()
        .combinations(2)
        .map(|pair| distance(pair.first().unwrap(), pair.last().unwrap()))
        .sum()
}

fn solve_part2(lines: Vec<String>, expansion_amount: i64) -> i64 {
    let universe = expand_universe_by_more_than_1(&lines, expansion_amount);
    let galaxies: Vec<Point> = universe
        .iter()
        .filter(|(_, v)| v == &&'#')
        .map(|(k, _)| k.clone())
        .collect();

    galaxies
        .iter()
        .combinations(2)
        .map(|pair| distance(pair.first().unwrap(), pair.last().unwrap()))
        .sum()
}

fn distance(p1: &Point, p2: &Point) -> i64 {
    (p2.x - p1.x).abs() + (p2.y - p1.y).abs()
}

fn expand_universe_by_more_than_1(universe: &Vec<String>, expansion_amount: i64) -> HashMap<Point, char> {
    assert_ne!(expansion_amount, 1);

    let mut grid = HashMap::new();
    let mut y = 0;
    for line in universe {
        let mut x = 0;
        for (idx, char) in enumerate(line.chars()) {
            let galaxy_count = universe.iter()
                .map(|l| {
                    let nth = l.chars().nth(idx);
                    match nth {
                        None => { '.' }
                        Some(v) => { v }
                    }
                })
                .filter(|c| c == &'#')
                .count();

            if char == '#' {
                grid.insert(Point { x, y }, char);
            }
            if galaxy_count == 0 && expansion_amount != 1 {
                x += expansion_amount;
            } else {
                x += 1;
            }
        }
        if !line.contains('#') && expansion_amount != 1 {
            y += expansion_amount;
        } else {
            y += 1
        }
    }

    grid
}

fn expand_universe(universe: &Vec<String>) -> HashMap<Point, char> {
    let expanded_y = expand_vertically(universe);
    let grid_with_expanded_y = crate::parse_lines_to_grid(&expanded_y);
    let rotated_grid = crate::rotate_grid(&grid_with_expanded_y);
    let rotated_lines = crate::grid_to_lines(&rotated_grid);
    let expanded_rotated_xy = expand_vertically(&rotated_lines);

    // Should be back in the original orientation and expanded
    crate::rotate_grid(&crate::parse_lines_to_grid(&expanded_rotated_xy))
}

fn expand_vertically(universe: &Vec<String>) -> Vec<String> {
    let mut expanded_y = Vec::new();
    for line in universe {
        if !line.contains("#") {
            expanded_y.push(line.clone())
        }
        expanded_y.push(line.clone());
    };
    expanded_y
}

#[cfg(test)]
mod tests {
    use crate::day11::{expand_universe, solve_part1, solve_part2};
    use crate::{dbg_print_grid, parse_lines_to_grid};

    #[test]
    fn test_expand_universe() {
        let universe = vec![
            ".##".to_string(),
            ".#.".to_string(),
            "...".to_string(),
        ];
        let expanded_universe = expand_universe(&universe);
        let expected_universe_lines = vec![
            "..##".to_string(),
            "..#.".to_string(),
            "....".to_string(),
            "....".to_string(),
        ];
        let expected_universe = parse_lines_to_grid(&expected_universe_lines);

        println!("expanded universe:");
        dbg_print_grid(&expanded_universe);

        println!("expected universe:");
        dbg_print_grid(&expected_universe);

        assert_eq!(expanded_universe, expected_universe)
    }

    #[test]
    fn test_example_1() {
        let universe = vec![
            "...#......".to_string(),
            ".......#..".to_string(),
            "#.........".to_string(),
            "..........".to_string(),
            "......#...".to_string(),
            ".#........".to_string(),
            ".........#".to_string(),
            "..........".to_string(),
            ".......#..".to_string(),
            "#...#.....".to_string(),
        ];
        assert_eq!(solve_part1(universe), 374);
    }

    #[test]
    fn test_part_2_expand_by_10_100() {
        let universe = vec![
            "...#......".to_string(),
            ".......#..".to_string(),
            "#.........".to_string(),
            "..........".to_string(),
            "......#...".to_string(),
            ".#........".to_string(),
            ".........#".to_string(),
            "..........".to_string(),
            ".......#..".to_string(),
            "#...#.....".to_string(),
        ];
        assert_eq!(solve_part1(universe.clone()), 374);
        assert_eq!(solve_part2(universe.clone(), 10), 1030);
        assert_eq!(solve_part2(universe.clone(), 100), 8410);
    }
}
