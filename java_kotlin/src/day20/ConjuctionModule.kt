package day20

class ConjuctionModule(
    override val name: ModuleName,
    private val memory: MutableMap<ModuleName, Pulse>,
    private val destinations: List<ModuleName>,
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
}