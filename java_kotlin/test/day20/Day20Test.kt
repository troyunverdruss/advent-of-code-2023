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
        val day = Day20()
        val modules = day.parseModules(lines)
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
        val day = Day20()
        val modules = day.parseModules(lines)
        assertEquals(modules.count { it.name.name == "broadcaster" }, 1)
        assertEquals(modules.count { it.name.name == "a" }, 1)
        assertEquals(modules.count { it.name.name == "b" }, 1)
        assertEquals(modules.count { it.name.name == "inv" }, 1)
        assertEquals(modules.count { it.name.name == "con" }, 1)

    }
}