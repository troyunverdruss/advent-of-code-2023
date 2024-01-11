use std::fs::File;
use std::io::Read;

mod day11;
mod day12;
mod day13;

fn main() {
    // println!("Day 11");
    // println!("{}", day11::part1());
    // println!("{}", day11::part2());

    // println!("Day 12");
    // println!("{}", day12::part1());
    // println!("{}", day12::part2());

    println!("Day 13");
    println!("{}", day13::part1());
    // println!("{}", day12::part2());

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
