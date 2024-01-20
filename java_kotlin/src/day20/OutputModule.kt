package day20

import java.util.*

class OutputModule(
    override val name: ModuleName = ModuleName("output"),
    override val destinations: List<ModuleName> = listOf(),
    private val output: LinkedList<Pulse> = LinkedList(),
    private var receivedLowPulse: Boolean = false,
) : Module {
    override fun processPacket(packet: Packet): List<Packet> {
//        output.offer(packet.pulse)
        if (packet.pulse == Pulse.LOW) {
            receivedLowPulse = true
        }
        return listOf()
    }

    fun hasReceivedLowPulse(): Boolean {
        return receivedLowPulse
    }
}