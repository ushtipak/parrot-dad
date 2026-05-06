package com.parrotdad.soundboard

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun soundboardItems_count_isNine() {
        val items = com.parrotdad.soundboard.data.soundboardItems
        assertEquals(9, items.size)
    }
}
