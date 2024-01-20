package day20

class FlipFlopModule(
    override val name: ModuleName,
    override val destinations: List<ModuleName>,
    var state: OnOff = OnOff.OFF,
) : Module {
    override fun processPacket(packet: Packet): List<Packet> {
        val output = mutableListOf<Packet>()
        when (packet.pulse) {
            Pulse.HIGH -> {
                // High pulses are ignored
            }

            Pulse.LOW -> {
                when (state) {
                    OnOff.ON -> {
                        state = OnOff.OFF
                        destinations.forEach {
                            output.add(Packet(name, it, Pulse.LOW))
                        }
                    }

                    OnOff.OFF -> {
                        state = OnOff.ON
                        destinations.forEach {
                            output.add(Packet(name, it, Pulse.HIGH))
                        }
                    }
                }
            }
        }
        return output
    }

    override fun toString(): String {
        return when (state) {
            OnOff.ON -> "#"
            OnOff.OFF -> "_"

        }
    }

}