package day20

class BroadcastModule(
    override val name: ModuleName,
    override val destinations: List<ModuleName>
) : Module {
    override fun processPacket(packet: Packet): List<Packet> {
        return destinations.map {
            Packet(name, it, packet.pulse)
        }
    }


}