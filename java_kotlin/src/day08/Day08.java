package day08;

import day06.Day06;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day08 {
    public long part1() {
        List<String> lines = Day06.read_input("inputs/day8.txt");
        String instructions = lines.getFirst();
        List<Node> nodes = parseNodes(lines.subList(2, lines.size()));

        return solvePart1(instructions, nodes);
    }

    public long part2() {
        List<String> lines = Day06.read_input("inputs/day8.txt");
        String instructions = lines.getFirst();
        List<Node> nodes = parseNodes(lines.subList(2, lines.size()));

        return solvePart2(instructions, nodes);
    }

    static long solvePart2(String instructions, List<Node> nodes) {

        Map<String, Node> lookup = createLookup(nodes);
        List<String> startingLocations = nodes.stream()
                .map(Node::name)
                .filter(name -> name.charAt(2) == 'A')
                .toList();

        List<TraversalResults> traversalResults = new ArrayList<>();
        for (String location : startingLocations) {
            StepsResultData stepsToFirstZ = stepsToNextEndingZ(instructions, lookup, location);
            StepsResultData stepsToNextZ = stepsToNextEndingZ(instructions, lookup, stepsToFirstZ.endingNode);
            if (!stepsToFirstZ.endingNode.equals(stepsToNextZ.endingNode)) {
                throw new RuntimeException("Expected no midway Zs");
            }
            traversalResults.add(new TraversalResults(stepsToFirstZ.steps, stepsToNextZ.steps));
        }

        List<Long> stepsList = traversalResults.stream().map(r -> r.stepsToFirstZ).toList();
        Long currentLowest = stepsList.getFirst();
        for (Long steps : stepsList.subList(1, stepsList.size())) {
            Long testValue = currentLowest;
            while (testValue % steps != 0) {
                testValue += currentLowest;
            }
            currentLowest = testValue;
        }

        return currentLowest;
    }

    static long stepsBetweenAAAZZZ(String instructions, Map<String, Node> lookup) {
        long steps = 0;
        String location = "AAA";
        long instructionsLength = instructions.length();

        while (!location.equals("ZZZ")) {
            String currentInstruction = getCurrentInstruction(instructions, steps, instructionsLength);
            Node currentNode = lookup.get(location);
            location = getNextLocation(currentNode, currentInstruction);
            steps += 1;
        }
        return steps;
    }

    static StepsResultData stepsToNextEndingZ(String instructions, Map<String, Node> lookup, String start) {
        long steps = 0;
        String location = start;
        long instructionsLength = instructions.length();

        while (location.charAt(2) != 'Z' || steps == 0) {
            String currentInstruction = getCurrentInstruction(instructions, steps, instructionsLength);
            Node currentNode = lookup.get(location);
            location = getNextLocation(currentNode, currentInstruction);
            steps += 1;
        }
        return new StepsResultData(steps, location);
    }

    record StepsResultData(long steps, String endingNode) {
    }

    record TraversalResults(long stepsToFirstZ, long stepsPeriodBetweenSubsequentZ) {

    }

    static long solvePart2BruteForce(String instructions, List<Node> nodes) {
        Map<String, Node> lookup = createLookup(nodes);

        long steps = 0;
        List<String> locations = nodes.stream()
                .map(Node::name)
                .filter(name -> name.charAt(2) == 'A')
                .collect(Collectors.toList());
        long instructionsLength = instructions.length();

        while (!locations.stream().map(s -> s.charAt(2)).allMatch(c -> c == 'Z')) {
            String currentInstruction = getCurrentInstruction(instructions, steps, instructionsLength);
            locations.replaceAll(key -> getNextLocation(lookup.get(key), currentInstruction));
            steps += 1;
        }

        return steps;
    }

    static long solvePart1(String instructions, List<Node> nodes) {
        // Create a lookup map
        Map<String, Node> lookup = createLookup(nodes);
        return stepsBetweenAAAZZZ(instructions, lookup);
    }

    private static String getNextLocation(Node currentNode, String currentInstruction) {
        String location;
        if (currentNode == null) {
            throw new RuntimeException("Unknown node");
        }

        if (currentInstruction.equals("L")) {
            location = currentNode.left();
        } else if (currentInstruction.equals("R")) {
            location = currentNode.right();
        } else {
            throw new RuntimeException("Direction not recognized");
        }
        return location;
    }

    @NotNull
    private static String getCurrentInstruction(String instructions, long steps, long instructionsLength) {
        return instructions.substring((int) (steps % instructionsLength), (int) (steps % instructionsLength + 1));
    }

    @NotNull
    private static Map<String, Node> createLookup(List<Node> nodes) {
        return nodes.stream()
                .map(n -> Map.entry(n.name(), n))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    static List<Node> parseNodes(List<String> lines) {
        return lines.stream()
                .map(Node::parseLine)
                .toList();
    }
}
