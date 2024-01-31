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


        // Get the traversal order
        val traversal = traversalOrder(nodes).toMutableList()

        // Sort nodes to left/right
        val sortedNodes = mutableMapOf<String, Sort>()

        var a = traversal.removeFirst()!!
        sortedNodes[a] = Sort.LEFT

        while (traversal.isNotEmpty()) {
            for (b in nodes[a]!!.connectedNodes.map { it.id }) {
                if (sortedNodes.containsKey(b)) continue

                val sameSide = isSameSide(nodes, a, b)
                if (sameSide) {
                    sortedNodes[b] = sortedNodes[a]!!
                } else {
                    sortedNodes[b] = sortedNodes[a]!!.opposite()
                }
            }
            a = traversal.removeFirst()
        }

        assert(sortedNodes.size == nodes.size)
        val l = sortedNodes.count { it.value == Sort.LEFT }
        val r = sortedNodes.count { it.value == Sort.RIGHT }
        return (l * r).toLong()
    }

    private fun isSameSide(nodes: Map<String, Node>, a: String, b: String): Boolean {
        // We're going to find paths between node a and node b
        // If there are 4+ paths then we're on the same side, if there are only 3
        // then we know we're crossing over the boundary
        val visited = mutableSetOf<String>()
        val toVisit = LinkedHashSet<String>()
        var paths = 0

        visited.add(a)
        visited.add(b)
        toVisit.addAll(nodes[a]!!.connectedNodes.map { it.id })
        if (toVisit.remove(b)) paths += 1

        while (toVisit.isNotEmpty() && paths <= 3) {
            val currNode = toVisit.removeFirst()
            visited.add(currNode)

            nodes[currNode]!!.connectedNodes
                .forEach {
                    if (it.id == b) {
                        paths += 1
                    }
                    if (!visited.contains(it.id) && !toVisit.contains(it.id)) {
                        toVisit.add(it.id)
                    }
                }
        }
        return paths >= 4
    }

    private fun traversalOrder(nodes: Map<String, Node>): List<String> {
        // We just need to start somewhere, doesn't even matter where
        val a = nodes.keys.first()

        val visited = LinkedHashSet<String>()
        val toVisit = LinkedHashSet<String>()
        visited.add(a)
        toVisit.addAll(nodes[a]!!.connectedNodes.map { it.id })
        while (toVisit.isNotEmpty()) {
            val currNode = toVisit.removeFirst()
            visited.add(currNode)
            nodes[currNode]!!.connectedNodes
                .forEach {
                    if (!visited.contains(it.id) && !toVisit.contains(it.id)) {
                        toVisit.add(it.id)
                    }
                }
        }
        assert(visited.size == nodes.size)
        return visited.toList()
    }
}

data class Node(val id: String) {
    var connectedNodes: MutableSet<Node> = mutableSetOf()
}

enum class Sort {
    LEFT, RIGHT;

    fun opposite(): Sort {
        return when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}