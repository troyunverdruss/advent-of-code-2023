package day25

import org.testng.Assert.*
import org.testng.annotations.Test

class Day25Test {

    val lines = listOf(
        "jqt: rhn xhk nvd",
        "rsh: frs pzl lsr",
        "xhk: hfx",
        "cmg: qnr nvd lhk bvb",
        "rhn: xhk bvb hfx",
        "bvb: xhk hfx",
        "pzl: lsr hfx nvd",
        "qnr: nvd",
        "ntq: jqt hfx bvb xhk",
        "nvd: lhk",
        "lsr: lhk",
        "rzs: qnr cmg lsr rsh",
        "frs: qnr lhk lsr",
    )
    @Test
    fun `example 1`() {
        val day = Day25()
        day.solvePart1(lines)
    }
}