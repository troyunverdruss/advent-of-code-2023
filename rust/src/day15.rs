use std::collections::HashMap;
use std::ptr::hash;
use itertools::{enumerate, Itertools};
use crate::{day15, read_file_to_lines};

pub fn part1() -> i64 {
    let lines = read_file_to_lines("inputs/day15.txt");
    solve_part1(lines)
}

pub(crate) fn part2() -> i64 {
    let lines = read_file_to_lines("inputs/day15.txt");
    solve_part2(lines)
}

fn solve_part1(lines: Vec<String>) -> i64 {
    lines
        .get(0).unwrap()
        .trim_end()
        .split(",")
        .map(|s| hash_alg(s))
        .sum()
}

fn solve_part2(lines: Vec<String>) -> i64 {
    let mut boxes = Vec::new();
    for i in 0..256 {
        boxes.push(Box { id: i, labels: Vec::new(), labels_to_lenses: HashMap::new() });
    }

    let steps: Vec<String> = lines
        .get(0).unwrap()
        .trim_end()
        .split(",")
        .map(|s| s.to_string())
        .collect();

    for step in steps {
        if step.contains("=") {
            let parts: Vec<&str> = step.split("=").collect();
            let label = parts.get(0).unwrap().to_string();
            let value = parts.get(1).unwrap().parse::<i64>().unwrap();

            let box_id = hash_alg(&label);

            let mut target_box = boxes.get_mut(box_id as usize).unwrap();
            if target_box.labels_to_lenses.contains_key(&label) {
                target_box.labels_to_lenses.insert(label, value);
            } else {
                target_box.labels.push(label.clone());
                target_box.labels_to_lenses.insert(label, value);
            }
        } else if step.contains("-") {
            let label = step.replace("-", "");
            let box_id = hash_alg(&label);

            let mut target_box = boxes.get_mut(box_id as usize).unwrap();
            if target_box.labels_to_lenses.contains_key(&label) {
                let (pos, _) = target_box.labels.iter().find_position(|v| v == &&label).unwrap();
                target_box.labels.remove(pos);
                target_box.labels_to_lenses.remove(&label);
            }
        } else {
            panic!("instruction missing -/=");
        }
    }

    boxes
        .iter()
        .map(|b| b.focusing_power())
        .sum()
}

struct Box {
    id: i64,
    labels: Vec<String>,
    labels_to_lenses: HashMap<String, i64>,
}

impl Box {
    fn focusing_power(&self) -> i64 {
        let mut summed_value = 0;

        for (slot, lens) in enumerate(&self.labels) {
            let box_value = self.id + 1;
            let slot_value = slot as i64 + 1;
            let focal_length = self.labels_to_lenses.get(lens).unwrap();

            summed_value += box_value * slot_value * focal_length;
        }

        summed_value
    }
}

fn hash_alg(s: &str) -> i64 {
    let mut hash = 0;

    for c in s.chars() {
        hash = hash + c as i64;
        hash = hash * 17;
        hash = hash % 256;
    }

    assert!(hash >= 0);
    assert!(hash <= 255);
    hash
}

#[cfg(test)]
mod tests {
    use crate::day15::{hash_alg, solve_part1, solve_part2};

    #[test]
    fn test_hashing_single_word() {
        assert_eq!(hash_alg(&"HASH"), 52);
    }

    #[test]
    fn test_example_1() {
        let line = vec!["rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7".to_string()];
        assert_eq!(solve_part1(line), 1320);
    }

    #[test]
    fn test_example_2() {
        let line = vec!["rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7".to_string()];
        assert_eq!(solve_part2(line), 145);
    }
}