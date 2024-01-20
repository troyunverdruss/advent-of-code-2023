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

    fun part2(): Long {
        val lines = File("inputs/day20.txt").readLines().filter { it.isNotEmpty() }
        val modules = parseModules(lines)
        val network = Network()

        val rxModule = modules.find { it.name.name == "rx" } ?: throw RuntimeException("Couldn't find RX module")

        // Calculate what modules feed to RX
        modulesThatFeedToRx = modules
            .filter {
                it.destinations.toSet().intersect(setOf(rxModule.name)).isNotEmpty()
            }
            .map { it.name }
            .toMutableList()
        modulesThatFeedToRx = modules
            .filter {
                it.destinations.toSet().intersect(modulesThatFeedToRx.toSet()).isNotEmpty()
            }
            .map { it.name }
            .toMutableList()


        while (!(rxModule as OutputModule).hasReceivedLowPulse() && calculatedPart2Solution == null) {
            part2ButtonPresses += 1
            pressButtonModule(network, modules)
        }

        return calculatedPart2Solution as Long
    }


    companion object {
        var part2ButtonPresses = 0L

        var modulesThatFeedToRx = mutableListOf<ModuleName>()
        val whenModulesThatFeedToRxSendLow = mutableMapOf<ModuleName, Long>()
        var calculatedPart2Solution: Long? = null


        fun pressButtonModule(
            network: Network,
            modules: List<Module>
        ) {
            val moduleMap = modules.associateBy { it.name }

            // Send the button's low pulse
            val initSrc = ModuleName("button")
            val initDest = ModuleName("broadcaster")
            network.offer(Packet(initSrc, initDest, Pulse.LOW))

            while (network.isNotEmpty()) {
                val packet = network.poll()
                val dest = moduleMap[packet.dest] ?: throw RuntimeException("Unknown module: ${packet.dest}")
                dest.processPacket(packet).forEach {
                    if (
                        modulesThatFeedToRx.isNotEmpty()
                        && modulesThatFeedToRx.contains(packet.dest)
                        && packet.pulse == Pulse.LOW
                        ) {

                        whenModulesThatFeedToRxSendLow[packet.dest] = part2ButtonPresses

                        if (whenModulesThatFeedToRxSendLow.size == 4) {
                            // Calculate the lowest number that divides all 4
                            val numbers = whenModulesThatFeedToRxSendLow.values.toMutableList()
                            var delta = numbers.removeAt(0)
                            var nextTarget = numbers[0]
                            var sum = 0L
                            while (true) {
                                sum += delta
                                if (sum % nextTarget == 0L) {
                                    delta = sum
                                    numbers.removeAt(0)

                                    if (numbers.isEmpty()) {
                                        break
                                    }
                                    nextTarget = numbers[0]
                                }
                            }
                            calculatedPart2Solution = sum
                        }
                    }

                    network.offer(it)
                }
            }
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
                            ConjunctionModule(name, destinations, memory.toMutableMap())
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
    val destinations: List<ModuleName>
    fun processPacket(packet: Packet): List<Packet>
}

data class ModuleName(val name: String)