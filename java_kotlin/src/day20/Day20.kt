package day20

import java.io.File

class Day20 {

    fun part1(): Long {
        val lines = File("inputs/day20.txt").readLines().filter { it.isNotEmpty() }

        return solvePart1(lines)
    }

    fun solvePart1(lines: List<String>): Long {
        val modules = parseModules(lines)
        val network = Network()
        repeat(1000) {
            pressButtonModule(network, modules)
        }
        return network.getProductOfHighAndLowPulses()
    }

    fun pressButtonModule(network: Network, modules: List<Module>) {
        val moduleMap = modules.associateBy { it.name }

        // Send the button's low pulse
        val initSrc = ModuleName("button")
        val initDest = ModuleName("broadcaster")
        network.offer(Packet(initSrc, initDest, Pulse.LOW))

        while (network.isNotEmpty()) {
            val packet = network.poll()
            val dest = moduleMap[packet.dest] ?: throw RuntimeException("Unknown module: ${packet.dest}")
            dest.processPacket(packet).forEach { network.offer(it) }
        }
    }

    companion object {
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
            // Include all output-only modules
            val foundModuleNameStrings = modules.map { it.name.name }
            val outputModules = srcDestMap.values
                .flatMap { it.split(",") }
                .filter { !foundModuleNameStrings.contains(it) }
                .map { OutputModule(ModuleName(it)) }

            val modulesWithOutputs = modules.toMutableList()
            modulesWithOutputs.addAll(outputModules)

            return modulesWithOutputs
        }

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