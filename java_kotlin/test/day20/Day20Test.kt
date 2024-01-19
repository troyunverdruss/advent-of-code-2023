package day20

import org.testng.Assert.*
import org.testng.annotations.Test

class Day20Test {
    @Test
    fun `validate example 1 module parsing`() {
        val lines = listOf(
            "broadcaster -> a, b, c",
            "%a -> b",
            "%b -> c",
            "%c -> inv",
            "&inv -> a",
        )

        val modules = Day20.parseModules(lines)
        assertEquals(modules.count { it.name.name == "broadcaster" }, 1)
        assertEquals(modules.count { it.name.name == "a" }, 1)
        assertEquals(modules.count { it.name.name == "b" }, 1)
        assertEquals(modules.count { it.name.name == "c" }, 1)
        assertEquals(modules.count { it.name.name == "inv" }, 1)
    }

    @Test
    fun `validate example 2 module parsing`() {
        val lines = listOf(
            "broadcaster -> a",
            "%a -> inv, con",
            "&inv -> b",
            "%b -> con",
            "&con -> output",
        )

        val modules = Day20.parseModules(lines)
        assertEquals(modules.count { it.name.name == "broadcaster" }, 1)
        assertEquals(modules.count { it.name.name == "a" }, 1)
        assertEquals(modules.count { it.name.name == "b" }, 1)
        assertEquals(modules.count { it.name.name == "inv" }, 1)
        assertEquals(modules.count { it.name.name == "con" }, 1)
    }

    @Test
    fun `verify first example, press button once`() {
        val lines = listOf(
            "broadcaster -> a, b, c",
            "%a -> b",
            "%b -> c",
            "%c -> inv",
            "&inv -> a",
        )
        val day = Day20()
        val network = Network()
        val modules = Day20.parseModules(lines)

        day.pressButtonModule(network, modules)
        assertEquals(network.getProductOfHighAndLowPulses(), 8 * 4)
    }

    @Test
    fun `verify second example, press button 1000 times`() {
        val lines = listOf(
            "broadcaster -> a",
            "%a -> inv, con",
            "&inv -> b",
            "%b -> con",
            "&con -> output",
        )
        val day = Day20()

        assertEquals(day.solvePart1(lines), 11687500)
    }

}