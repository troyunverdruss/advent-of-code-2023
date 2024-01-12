use std::cmp::Ordering;
use std::fs::File;
use std::io::Read;
use std::collections::HashMap;
use std::ops::Add;

mod day11;
mod day12;
mod day13;
mod day14;
mod day15;

fn main() {
    // println!("Day 11");
    // println!("{}", day11::part1());
    // println!("{}", day11::part2());

    // println!("Day 12");
    // println!("{}", day12::part1());
    // println!("{}", day12::part2());

    // println!("Day 13");
    // println!("{}", day13::part1());
    // println!("{}", day13::part2());

    // println!("Day 14");
    // println!("{}", day14::part1());
    // println!("{}", day14::part2());

    println!("Day 15");
    println!("{}", day15::part1());
    println!("{}", day15::part2());
}

fn read_file_to_lines(path: &str) -> Vec<String> {
    let mut input_lines = String::new();

    File::open(path).unwrap()
        .read_to_string(&mut input_lines)
        .unwrap();

    input_lines
        .split("\n")
        .map(|s| s.to_string())
        .collect()
}

pub fn grid_to_lines(grid: &HashMap<Point, char>) -> Vec<String> {
    let max_x = grid.iter().map(|(k, _)| k.x).max().unwrap();
    let max_y = grid.iter().map(|(k, _)| k.y).max().unwrap();

    let mut lines = Vec::new();
    for y in 0..=max_y {
        let mut line_y = String::new();
        for x in 0..=max_x {
            line_y.push(grid.get(&Point { x, y }).unwrap().clone())
        }
        lines.push(line_y);
    }
    lines
}

pub fn rotate_grid(grid: &HashMap<Point, char>) -> HashMap<Point, char> {
    grid.iter()
        .map(|(p, c)| (Point { x: p.y, y: p.x }, c.clone()))
        .collect()
}

pub fn parse_lines_to_grid(lines: &Vec<String>) -> HashMap<Point, char> {
    let mut grid = HashMap::new();
    let mut y = 0;
    for line in lines {
        let mut x = 0;
        for char in line.chars() {
            grid.insert(Point { x, y }, char.clone());
            x += 1;
        }
        y += 1;
    }

    grid
}

pub fn dbg_print_grid(grid: &HashMap<Point, char>) {
    let max_x = grid.iter().map(|(k, _)| k.x).max().unwrap();
    let max_y = grid.iter().map(|(k, _)| k.y).max().unwrap();

    for y in 0..=max_y {
        for x in 0..=max_x {
            print!("{}", grid.get(&Point { x, y }).unwrap_or(&'.'));
        }
        println!()
    }
}

#[derive(Debug, Hash, Eq, PartialEq, Copy, Clone)]
pub struct Point {
    x: i64,
    y: i64,
}

impl Add for Point {
    type Output = Point;

    fn add(self, rhs: Self) -> Self::Output {
        Point { x: self.x + rhs.x, y: self.y + rhs.y }
    }
}

#[cfg(test)]
mod tests {
    use itertools::Itertools;
    use crate::Point;

    #[test]
    fn test_point_cmp() {
        // ....
        // .2..
        // .1..
        // .43.

        let points = vec![
            Point { x: 1, y: 2 }, // 1
            Point { x: 1, y: 1 }, // 2
            Point { x: 2, y: 3 }, // 3
            Point { x: 1, y: 3 }, // 4
        ];
        let sorted: Vec<&Point> = points
            .iter()
            .sorted_by_key(|p| (p.y, p.x))
            .collect();

        let expected: Vec<&Point> = vec![
            points.get(1).unwrap(),
            points.get(0).unwrap(),
            points.get(3).unwrap(),
            points.get(2).unwrap(),
        ];
        assert_eq!(sorted, expected);
    }
}