package day10;

import day06.Day06;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Day10 {
    static final String POSITION_WAS_IN_ORIGINAL_GRID_VALUE = "*";
    static final String POSITION_IS_OUTSIDE_PIPES_VALUE = "O";
    static final String POSITION_IS_INSIDE_PIPES_VALUE = "I";
    static final String POSITION_IS_NOT_PART_OF_ORIGINAL_GRID = "_";
    static final String POSITION_IS_PIPE = "#";

    public Long part1() {
        List<String> lines = Day06.read_input("inputs/day10.txt");
        Map<Point, String> grid = parseGrid(lines);
        return solvePart1(grid);
    }

    public Long part2() {
        List<String> lines = Day06.read_input("inputs/day10.txt");
        Map<Point, String> grid = parseGrid(lines);
        return solvePart2(grid);
    }

    static Long solvePart1(Map<Point, String> grid) {
        Point start = getStartingPoint(grid);

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
                throw new RuntimeException("Uh oh, no new point found for path2");
            }

            visited.add(path1Loc);
            visited.add(path2Loc);
            path1Loc = nextPath1Loc;
            path2Loc = nextPath2Loc;

            steps += 1;
        }

        return steps;
    }

    private static Point getStartingPoint(Map<Point, String> grid) {
        return grid
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals("S"))
                .map(Map.Entry::getKey)
                .toList()
                .getFirst();
    }

    static Long solvePart2(Map<Point, String> grid) {
        Point start = getStartingPoint(grid);
        Map<Point, String> expandedGrid = expandGrid(grid, start);

        long countCannotReachPerimeter = 0;
        Set<Point> testedPoints = new HashSet<>();
        for (Point point : grid.keySet()) {
//            System.out.println("testedPoints.size() = " + testedPoints.size());
            Point expandedPoint = new Point(point.x * 2, point.y * 2);
            // Skip points we've found already
            if (testedPoints.contains(expandedPoint)) {
                continue;
            }
            testedPoints.add(expandedPoint);
            String currentValue = expandedGrid.get(expandedPoint);
            if (!POSITION_IS_PIPE.equals(currentValue)) {
                PerimeterSearchResult perimeterSearchResult = canReachPerimeter(expandedGrid, expandedPoint);

                for (Point visitedPoint : perimeterSearchResult.visited) {
                    String val = expandedGrid.get(visitedPoint);
                    testedPoints.add(visitedPoint);
                    if (perimeterSearchResult.canReach) {
                        expandedGrid.put(visitedPoint, POSITION_IS_OUTSIDE_PIPES_VALUE);
                    } else {
                        expandedGrid.put(visitedPoint, POSITION_IS_INSIDE_PIPES_VALUE);
                        if (!POSITION_WAS_IN_ORIGINAL_GRID_VALUE.equals(val)) {
                            countCannotReachPerimeter += 1;
                        }
                    }
                }
            }
        }

        return countCannotReachPerimeter;
    }

    @NotNull
    private static Map<Point, String> expandGrid(Map<Point, String> grid, Point start) {
        Map<Point, String> expandedGrid = grid
                .entrySet()
                .stream()
                .map(es -> Map.entry(new Point(es.getKey().x() * 2, es.getKey().y * 2), es.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Set<Point> pipePositions = getAllConnectedPipePositions(grid, start);
        for (Point point : pipePositions) {
            String val = grid.get(point);
            Point expandedGridPoint = new Point(point.x * 2, point.y * 2);
            expandedGrid.put(expandedGridPoint, POSITION_IS_PIPE);
            switch (val) {
                case "S" -> {
                    // Nothing to do for S, neighboring pipes will expand to connect
                    // to this original place and then we don't have to figure out
                    // in which directions the pipes are connected
                }
                case "|" -> {
                    expandedGrid.put(expandedGridPoint.up(), POSITION_IS_PIPE);
                    expandedGrid.put(expandedGridPoint.down(), POSITION_IS_PIPE);
                }
                case "-" -> {
                    expandedGrid.put(expandedGridPoint.left(), POSITION_IS_PIPE);
                    expandedGrid.put(expandedGridPoint.right(), POSITION_IS_PIPE);
                }
                case "L" -> {
                    expandedGrid.put(expandedGridPoint.up(), POSITION_IS_PIPE);
                    expandedGrid.put(expandedGridPoint.right(), POSITION_IS_PIPE);
                }
                case "J" -> {
                    expandedGrid.put(expandedGridPoint.up(), POSITION_IS_PIPE);
                    expandedGrid.put(expandedGridPoint.left(), POSITION_IS_PIPE);
                }
                case "7" -> {
                    expandedGrid.put(expandedGridPoint.left(), POSITION_IS_PIPE);
                    expandedGrid.put(expandedGridPoint.down(), POSITION_IS_PIPE);
                }
                case "F" -> {
                    expandedGrid.put(expandedGridPoint.right(), POSITION_IS_PIPE);
                    expandedGrid.put(expandedGridPoint.down(), POSITION_IS_PIPE);
                }
                case null, default ->
                        throw new RuntimeException("Shouldn't have any unexpected chars in the pipe positions");
            }
        }

        long maxX = expandedGrid.keySet().stream().map(p -> p.x).max(Long::compare).orElse(-1L);
        long maxY = expandedGrid.keySet().stream().map(p -> p.y).max(Long::compare).orElse(-1L);
        assert maxX != -1 && maxY != -1;

        // Fill all the empty space in the new grid
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                String currVal = expandedGrid.getOrDefault(new Point(x, y), POSITION_IS_NOT_PART_OF_ORIGINAL_GRID);
                if (POSITION_IS_NOT_PART_OF_ORIGINAL_GRID.equals(currVal)) {
                    expandedGrid.put(new Point(x, y), POSITION_WAS_IN_ORIGINAL_GRID_VALUE);
                }
            }
        }
        return expandedGrid;
    }

    record PerimeterSearchResult(boolean canReach, Set<Point> visited) {
    }

    private static PerimeterSearchResult canReachPerimeter(Map<Point, String> grid, Point start) {
        Set<Point> visited = new HashSet<>();
        LinkedList<Point> toVisit = new LinkedList<>();
        Set<Point> toVisitLookup = new HashSet<>();
        toVisit.add(start);
        toVisitLookup.add(start);

        while (!toVisit.isEmpty()) {
            Point current = toVisit.pop();
            visited.add(current);
            List<Point> neighbors = List.of(current.up(), current.right(), current.down(), current.left());
            for (Point neighborLoc : neighbors) {
                String neighborVal = grid.getOrDefault(neighborLoc, POSITION_IS_NOT_PART_OF_ORIGINAL_GRID);
                if (POSITION_IS_NOT_PART_OF_ORIGINAL_GRID.equals(neighborVal) || POSITION_IS_OUTSIDE_PIPES_VALUE.equals(neighborVal)) {
                    return new PerimeterSearchResult(true, visited);
                } else if (POSITION_IS_PIPE.equals(neighborVal) || POSITION_IS_INSIDE_PIPES_VALUE.equals(neighborVal)) {
                    // Dead end, don't append this point
                    continue;
                } else {
                    if (!visited.contains(neighborLoc) && !toVisitLookup.contains(neighborLoc)) {
                        toVisit.add(neighborLoc);
                        toVisitLookup.add(neighborLoc);
                    }
                }
            }
        }
        return new PerimeterSearchResult(false, visited);
    }

    @NotNull
    private static Set<Point> getAllConnectedPipePositions(Map<Point, String> grid, Point start) {
        ConnectedPipes connectedPipes = findConnectedPipeNeighbors(grid, start);
        Set<Point> visited = new HashSet<>();
        visited.add(start);

        Point path1Loc = connectedPipes.first;
        Point nextPath1Loc;
        visited.add(path1Loc);


        // Start at 1 because we're not starting at "S"
        // but instead the first step along the pathway
        boolean nothingNewFound = false;
        while (!nothingNewFound) {
            ConnectedPipes connectedPipes1 = findConnectedPipeNeighbors(grid, path1Loc);
            if (!visited.contains(connectedPipes1.first)) {
//                System.out.println("next location:" + connectedPipes1.first);
                nextPath1Loc = connectedPipes1.first;
                path1Loc = nextPath1Loc;
            } else if (!visited.contains(connectedPipes1.second)) {
                nextPath1Loc = connectedPipes1.second;
//                System.out.println("next location:" + connectedPipes1.second);
                path1Loc = nextPath1Loc;
            } else {
                nothingNewFound = true;
            }

            visited.add(path1Loc);
        }
        return visited;
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

        Point up() {
            return new Point(this.x, this.y - 1);
        }

        Point right() {
            return new Point(this.x + 1, this.y);
        }

        Point down() {
            return new Point(this.x, this.y + 1);
        }

        Point left() {
            return new Point(this.x - 1, this.y);
        }

    }

    record ConnectedPipes(Point first, Point second) {

    }
}
