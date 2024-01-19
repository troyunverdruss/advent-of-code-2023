package day20

import java.io.File
import java.util.LinkedList

class Day20 {
    val network = LinkedList<Packet>()
    fun part1(): Long {
        val lines = File("inputs/day20.txt").readLines().filter { it.isNotEmpty() }
        val modules = parseModules(lines)
        return 0
    }

    fun parseModules(lines: List<String>): List<Module> {
        // FlipFlop = prefix %
        // Conjunction = prefix &
        // Broadcast = always "broadcaster"

        val srcDestMap = lines
            .map { it.split(" -> ", limit = 2) }
            .associate {
                Pair(
                    it[0].trim(),
                    it[1].replace(" ", "").trim()
                )
            }

        assert(lines.size == srcDestMap.keys.size)

        val modules = srcDestMap
            .map { entry ->
                val input = entry.key
                val output = entry.value

                val type = input[0]
                val name = if (type == 'b') {
                    ModuleName(input)
                } else {
                    ModuleName(input.substring(1..input.lastIndex))
                }
                val destinations = output
                    .split(",")
                    .map { ModuleName(it) }

                when (type) {
                    '%' -> FlipFlopModule(name, destinations)
                    '&' -> {
                        val memory = srcDestMap
                            .filter { it.value.contains(name.name) }
                            .map { it.key }
                            .map { it.substring(1..it.lastIndex) }
                            .associate { Pair(ModuleName(it), Pulse.LOW) }
                        ConjuctionModule(name, memory.toMutableMap(), destinations)
                    }

                    'b' -> BroadcastModule(name, destinations)
                    else -> throw RuntimeException("Unknown module type: $type")
                }
            }
        return modules
    }
}

data class Packet(val src: ModuleName, val dest: ModuleName, val pulse: Pulse)
enum class Pulse { HIGH, LOW }
enum class OnOff { ON, OFF }

interface Module {
    val name: ModuleName
    fun processPacket(packet: Packet): List<Packet>
}

data class ModuleName(val name: String)