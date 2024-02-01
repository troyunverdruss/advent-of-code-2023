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

        var a = if (traversal.contains("ntq")) {
            traversal.remove("ntq")
            "ntq"
        } else {
            traversal.removeFirst()!!
        }
        sortedNodes[a] = Sort.LEFT

        while (traversal.isNotEmpty()) {
            for (b in nodes[a]!!.connectedNodes.map { it.id }) {
                if (sortedNodes.containsKey(b)) continue

                val sameSide = isSameSide(nodes, a, b)
//                if (b == "rsh") {
                    val sameSide1 = isSameSide(nodes, a, b)
//                }
                if (sameSide) {
                    sortedNodes[b] = sortedNodes[a]!!
                } else {
                    sortedNodes[b] = sortedNodes[a]!!.opposite()
                }
            }
            a = traversal.removeFirst()
        }

        assert(sortedNodes.size == nodes.size)
        val l = sortedNodes.filter { it.value == Sort.LEFT }
        val r = sortedNodes.filter { it.value == Sort.RIGHT }
        return (l.size * r.size).toLong()
    }

    private fun isSameSide(nodes: Map<String, Node>, startId: String, destId: String): Boolean {
        val paths = LinkedHashSet<LinkedHashSet<PathNode>>()
        val usedEdges = mutableSetOf<Set<PathNode>>()
        val tmpUsedEdges = mutableSetOf<Set<PathNode>>()
        val toVisit = LinkedHashSet<PathNode>()

        val start = PathNode(startId)
        val dest = PathNode(destId)

        toVisit.add(start)
        while (toVisit.isNotEmpty()) {
            val currNode = toVisit.removeFirst()

            for (nextNode in nodes[currNode.id]!!.connectedNodes.map { it.toPathNode() }) {
                val thisEdge = setOf(currNode, nextNode)
                if (usedEdges.contains(thisEdge) || tmpUsedEdges.contains(thisEdge)) {
                    continue
                }

                tmpUsedEdges.add(thisEdge)
                nextNode.prevNode = currNode

                if (nextNode == dest) {
                    // Construct the path
                    val path = mutableListOf<PathNode>()
                    path.add(dest)
                    var prevNode = currNode
                    while (prevNode != null) {
                        path.add(prevNode)
                        prevNode = prevNode.prevNode
                    }
                    path.reverse()
                    path.windowed(2)
                        .forEach { usedEdges.add(it.toSet()) }
                    val pathSet = LinkedHashSet<PathNode>(path)
                    paths.add(pathSet)
                    toVisit.clear()
                    toVisit.add(start)
                    tmpUsedEdges.clear()
                    break
                }
//                else if (
////                    !usedEdges.contains(nextNode) &&
//                    toVisit.contains(nextNode)
//                ) {
//                    toVisit.add(nextNode)
//                }
                else {

                    toVisit.add(nextNode)
                }
            }
        }

        return paths.size >= 4
    }

    private fun xisSameSide(nodes: Map<String, Node>, aId: String, bId: String): Boolean {
        println("\nFinding paths from $aId => $bId")
        val a = PathNode(aId)
        val b = PathNode(bId)

        // Can't use the starting node as part of a path
        val usedNodes = mutableSetOf<PathNode>()
        usedNodes.add(a)
        // We don't want to use b as a transit point for another path
        if (nodes[aId]!!.connectedNodes.contains(Node(bId))) {
            usedNodes.add(b)
        }

        // Count paths
        var paths = 0

        // The most possible unique paths there can be from
        // a to b is equal to the number of connected nodes to a
        // so let's just iterate over those
        var firstSearch = true
        var foundPath: List<PathNode>? = null
        while (firstSearch || foundPath != null) {
            firstSearch = false
            foundPath = findPath(nodes, usedNodes, a, b)
            usedNodes.addAll(foundPath ?: listOf())
            paths += 1
        }
        println("$aId => $bId: $paths paths found")
        return paths >= 4
    }

    private fun findPath(
        nodes: Map<String, Node>,
        usedNodes: MutableSet<PathNode>,
        a: PathNode,
        b: PathNode
    ): List<PathNode>? {

        val visited = mutableSetOf<PathNode>()
        val toVisit = LinkedHashSet<PathNode>()

        toVisit.add(a)

        while (toVisit.isNotEmpty()) {
            val currNode = toVisit.removeFirst()
            visited.add(currNode)
            for (cn in nodes[currNode.id]!!.connectedNodes) {
                val currentNode = PathNode(cn.id)

                if (cn.id == b.id) {
                    // Add all nodes from this path to the used nodes
                    // And stop
                    val path = mutableListOf<PathNode>()
                    print("  ${cn.id}")
                    print(" <= ${currNode.id}")
                    path.add(currentNode)
                    path.add(currNode)
                    var prevNode = currNode.prevNode
                    while (prevNode != null) {
                        path.add(prevNode)
                        print(" <= ${prevNode.id}")
                        prevNode = prevNode.prevNode
                    }
                    println()

                    val pathCopy = path.toMutableList()
                    pathCopy.remove(a)
                    pathCopy.remove(b)
                    if (path.size > 2 && pathCopy.all { !usedNodes.contains(it) }) {
                        return path
                    }

                }
                val nextNode = currentNode
                nextNode.prevNode = currNode
                if (
                    !visited.contains(nextNode) &&
                    !toVisit.contains(nextNode) &&
                    !usedNodes.contains(nextNode)
                ) {
                    toVisit.add(nextNode)
                }
            }
        }

        // No path found
        return null
    }

    private fun traversalOrder(nodes: Map<String, Node>): List<String> {
        // We just need to start somewhere, doesn't even matter where
        val a = if (nodes.containsKey("ntq")) {
            "ntq"
        } else {
            nodes.keys.first()
        }

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
    fun toPathNode(): PathNode {
        return PathNode(id)
    }
}

data class PathNode(val id: String) {
    var prevNode: PathNode? = null
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