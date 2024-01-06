package day10;

import day06.Day06;

import java.util.*;

public class Day10 {
    public Long part1() {
        List<String> lines = Day06.read_input("inputs/day10.txt");
        Map<Point, String> grid = parseGrid(lines);
        return solvePart1(grid);
    }

    static Long solvePart1(Map<Point, String> grid) {
        Point start = grid
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals("S"))
                .map(Map.Entry::getKey)
                .toList()
                .getFirst();

        ConnectedPipes connectedPipes = findConnectedPipeNeighbors(grid, start);
        Set<Point> visited = new HashSet<>();
        visited.add(start);

        Point path1Loc = connectedPipes.first;
        Point path2Loc = connectedPipes.second;
        Point nextPath1Loc;
        Point nextPath2Loc;


        // Start at 1 because we're not starting at "S"
        // but instead the first step along the pathway
        long steps = 1;
        while (!path1Loc.equals(path2Loc)) {
            ConnectedPipes connectedPipes1 = findConnectedPipeNeighbors(grid, path1Loc);
            if (!visited.contains(connectedPipes1.first)) {
                nextPath1Loc = connectedPipes1.first;
            } else if (!visited.contains(connectedPipes1.second)) {
                nextPath1Loc = connectedPipes1.second;
            } else {
                throw new RuntimeException("Uh oh, no new point found for path1");
            }

            ConnectedPipes connectedPipes2 = findConnectedPipeNeighbors(grid, path2Loc);
            if (!visited.contains(connectedPipes2.first)) {
                nextPath2Loc = connectedPipes2.first;
            } else if (!visited.contains(connectedPipes2.second)) {
                nextPath2Loc = connectedPipes2.second;
            } else {
                throw new RuntimeException("Uh oh, no new point found for path1");
            }

            visited.add(path1Loc);
            visited.add(path2Loc);
            path1Loc = nextPath1Loc;
            path2Loc = nextPath2Loc;

            steps += 1;
        }

        return steps;
    }

    static Map<Point, String> parseGrid(List<String> lines) {
        Map<Point, String> grid = new HashMap<>();
        long y = 0;
        for (String line : lines) {
            long x = 0;
            for (char c : line.toCharArray()) {
                grid.put(new Point(x, y), String.valueOf(c));
                x += 1;
            }
            y += 1;
        }
        return grid;
    }

    static ConnectedPipes findConnectedPipeNeighbors(Map<Point, String> grid, Point loc) {
        List<Point> connections = new ArrayList<>();
        String currentValue = grid.get(loc);

        // Up
        // Test for connections only if current value can connect upwards
        if ("S".equals(currentValue) || "|".equals(currentValue) || "J".equals(currentValue) || "L".equals(currentValue)) {
            Point testUp = loc.add(new Point(0, -1));
            String upValue = grid.getOrDefault(testUp, ".");
            if ("S".equals(upValue) || "|".equals(upValue) || "7".equals(upValue) || "F".equals(upValue)) {
                connections.add(testUp);
            }
        }

        // Right
        // Test for connections only if current value can connect rightwards
        if ("S".equals(currentValue) || "-".equals(currentValue) || "L".equals(currentValue) || "F".equals(currentValue)) {
            Point testRight = loc.add(new Point(1, 0));
            String rightValue = grid.getOrDefault(testRight, ".");
            if ("S".equals(rightValue) || "-".equals(rightValue) || "J".equals(rightValue) || "7".equals(rightValue)) {
                connections.add(testRight);
            }
        }

        // Down
        // Test for connections only if current value can connect downwards
        if ("S".equals(currentValue) || "|".equals(currentValue) || "7".equals(currentValue) || "F".equals(currentValue)) {
            Point testDown = loc.add(new Point(0, 1));
            String downValue = grid.getOrDefault(testDown, ".");
            if ("S".equals(downValue) || "|".equals(downValue) || "J".equals(downValue) || "L".equals(downValue)) {
                connections.add(testDown);
            }
        }

        // Left
        // Test for connections only if current value can connect leftwards
        if ("S".equals(currentValue) || "-".equals(currentValue) || "J".equals(currentValue) || "7".equals(currentValue)) {
            Point testLeft = loc.add(new Point(-1, 0));
            String leftValue = grid.getOrDefault(testLeft, ".");
            if ("S".equals(leftValue) || "-".equals(leftValue) || "L".equals(leftValue) || "F".equals(leftValue)) {
                connections.add(testLeft);
            }
        }

        if (connections.size() != 2) {
            throw new RuntimeException("Expected exactly 2 connections");
        }
        return new ConnectedPipes(connections.getFirst(), connections.getLast());
    }

    record Point(long x, long y) {
        // This is just the neighbors that could be connected up/down/left/right
        static List<Point> neighbors = List.of(
                /*new Point(-1, -1), */new Point(0, -1), /*new Point(1, -1),*/
                new Point(-1, 0), /* self, */ new Point(1, 0),
                /*new Point(-1, 1),*/ new Point(0, 1)/*, new Point(1, 1)*/
        );

        Point add(Point p2) {
            return new Point(this.x + p2.x, this.y + p2.y);
        }


    }

    record ConnectedPipes(Point first, Point second) {

    }
}
