package day20

class ConjunctionModule(
    override val name: ModuleName,
    override val destinations: List<ModuleName>,
    val memory: MutableMap<ModuleName, Pulse>,
) : Module {
    override fun processPacket(packet: Packet): List<Packet> {
        assert(memory.containsKey(packet.src))
        memory[packet.src] = packet.pulse
        val pulse = if (memory.all { it.value == Pulse.HIGH }) {
            Pulse.LOW
        } else {
            Pulse.HIGH
        }

        return destinations.map {
            Packet(name, it, pulse)
        }
    }

    override fun toString(): String {
        val memoryDisplay = memory.map { it }.sortedBy { it.key.name }
            .map {
                when (it.value) {
                    Pulse.HIGH -> "#"
                    Pulse.LOW -> "_"
                }
            }
            .joinToString("")
        if (memoryDisplay.length > 1 && !memoryDisplay.contains("_")) {
            val i =0
        }
        return memoryDisplay
    }
}