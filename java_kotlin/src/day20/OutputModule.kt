package day20

import java.util.*

class OutputModule(
    override val name: ModuleName = ModuleName("output"),
    private val output: LinkedList<Pulse> = LinkedList(),
    private val receivedLowPulse: Boolean = false,
) : Module {
    override fun processPacket(packet: Packet): List<Packet> {
        output.offer(packet.pulse)
        return listOf()
    }
}