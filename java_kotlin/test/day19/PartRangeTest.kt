package day19

import org.testng.Assert.*
import org.testng.annotations.Test

class PartRangeTest {
    @Test
    fun `subdivide x less than`() {
        val partRange = PartRange(1..10L, 1..10L, 1..10L, 1..10L)
        val subdivideResult = partRange.subdivide(Compare.LESS_THAN, Rating.X, 5)
        assertEquals(
            subdivideResult.included,
            PartRange(1..4L, 1..10L, 1..10L, 1..10L)
        )
        assertEquals(
            subdivideResult.excluded,
            PartRange(5..10L, 1..10L, 1..10L, 1..10L)
        )
    }
    @Test
    fun `subdivide x greater than`() {
        val partRange = PartRange(1..10L, 1..10L, 1..10L, 1..10L)
        val subdivideResult = partRange.subdivide(Compare.GREATER_THAN, Rating.X, 5)
        assertEquals(
            subdivideResult.included,
            PartRange(6..10L, 1..10L, 1..10L, 1..10L)
        )
        assertEquals(
            subdivideResult.excluded,
            PartRange(1..5L, 1..10L, 1..10L, 1..10L)
        )
    }
}