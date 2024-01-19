package day20

import java.util.*

class Network(
    private val queue: LinkedList<Packet> = LinkedList(),
    private var lowPulseCount: Long = 0,
    private var highPulseCount: Long = 0,
) {
    fun isNotEmpty(): Boolean {
        return queue.isNotEmpty()
    }

    fun offer(packet: Packet) {
        queue.offer(packet)
        when (packet.pulse) {
            Pulse.HIGH -> highPulseCount += 1
            Pulse.LOW -> lowPulseCount += 1
        }
    }

    fun poll(): Packet {
        return queue.poll()
    }

    fun getProductOfHighAndLowPulses(): Long {
        return lowPulseCount * highPulseCount
    }


}