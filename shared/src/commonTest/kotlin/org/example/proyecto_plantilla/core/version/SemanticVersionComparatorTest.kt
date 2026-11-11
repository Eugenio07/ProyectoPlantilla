package org.example.proyecto_plantilla.core.version

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SemanticVersionComparatorTest {
    private val c = SemanticVersionComparator

    private fun assertLess(a: String, b: String) = assertTrue(c.compare(a, b) < 0, "$a < $b")
    private fun assertSameVersion(a: String, b: String) = assertEquals(0, c.compare(a, b), "$a == $b")

    @Test
    fun comparesSegmentsNumericallyNotAsText() = assertLess("1.9.0", "1.10.0")

    @Test
    fun majorDominatesMinorAndPatch() = assertLess("1.99.99", "2.0.0")

    @Test
    fun missingSegmentsCountAsZero() {
        assertSameVersion("1.2", "1.2.0")
        assertLess("1.2", "1.2.1")
    }

    @Test
    fun preReleaseSuffixIsIgnored() {
        assertSameVersion("1.4.0-rc1", "1.4.0")
        assertSameVersion("1.4.0-rc.1", "1.4.0")
        assertLess("1.4.0-rc1", "1.5.0")
    }

    @Test
    fun buildMetadataIsIgnored() {
        assertSameVersion("1.4.0+build.7", "1.4.0")
        assertLess("1.4.0+build.7", "1.4.1")
    }

    @Test
    fun surroundingWhitespaceIsTrimmed() {
        assertSameVersion("  1.4.0 ", "1.4.0")
        assertLess(" 1.4.0 ", "1.4.1")
    }

    @Test
    fun partiallyNumericSegmentUsesItsLeadingDigits() {
        assertSameVersion("1.3rc.0", "1.3.0")
        assertLess("1.3rc.0", "1.4.0")
    }

    @Test
    fun isValidAcceptsThreeNumericSegmentsWithSuffixes() {
        assertTrue(c.isValid("1.4.0"))
        assertTrue(c.isValid("1.4.0-rc1"))
        assertTrue(c.isValid("1.4.0+build.7"))
        assertTrue(c.isValid(" 1.4.0 "))
    }

    @Test
    fun isValidRejectsWhatCompareTolerates() {
        assertFalse(c.isValid("1.4"), "segmentos de menos")
        assertFalse(c.isValid("1.3rc.0"), "segmento parcialmente numérico")
        assertFalse(c.isValid(""), "vacía")
        assertFalse(c.isValid("abc"), "sin números")
        assertFalse(c.isValid("1.4.0.1"), "segmentos de más")
    }
}
