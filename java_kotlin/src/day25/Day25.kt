package day25

import java.io.File

class Day25 {
    fun part1(): Long {
        val lines = File("inputs/day25.txt").readLines()
        return solvePart1(lines)
    }

    fun solvePart1(lines: List<String>): Long {
        val nodes = mutableMapOf<String, Node>()
        val nodeSets = mutableSetOf<MutableSet<String>>()
        lines.forEach {
            val parts = it.split(":")
            val left = parts[0].trim()
            val right = parts[1].trim().split(" ").map { it.trim() }
            if (right.contains("")) {
                val i = 0
            }
            val leftNode = nodes[left] ?: Node(left)
            for (r in right) {
                nodeSets.add(mutableSetOf(left, r))
                val rightNode = nodes[r] ?: Node(r)
                leftNode.connectedNodes.add(rightNode)
                rightNode.connectedNodes.add(leftNode)
                nodes[r] = rightNode
            }
            nodes[left] = leftNode
        }


        return 0
    }


}

data class Node(val id: String) {
    var connectedNodes: MutableSet<Node> = mutableSetOf()
}