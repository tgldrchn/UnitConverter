package com.example.unitconverter

import com.example.unitconverter.logic.Category
import com.example.unitconverter.logic.Converter
import com.example.unitconverter.logic.ParseResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConverterTest {

    private fun unit(cat: Category, symbol: String) =
        Converter.unitsOf(cat).first { it.symbol == symbol }

    private fun conv(cat: Category, from: String, to: String, v: Double) =
        Converter.convert(cat, unit(cat, from), unit(cat, to), v)

    @Test fun kmToMeters() = assertEquals(1000.0, conv(Category.LENGTH, "km", "m", 1.0), 1e-9)

    @Test fun milesToKm() = assertEquals(1.609344, conv(Category.LENGTH, "mi", "km", 1.0), 1e-9)

    @Test fun celsiusToFahrenheit() =
        assertEquals(212.0, conv(Category.TEMPERATURE, "°C", "°F", 100.0), 1e-9)

    @Test fun fahrenheitToKelvin() =
        assertEquals(273.15, conv(Category.TEMPERATURE, "°F", "K", 32.0), 1e-9)

    @Test fun zeroKelvinToCelsius() =
        assertEquals(-273.15, conv(Category.TEMPERATURE, "K", "°C", 0.0), 1e-9)

    @Test fun sameUnitUnchanged() = assertEquals(42.5, conv(Category.MASS, "kg", "kg", 42.5), 0.0)

    @Test fun hoursToMinutes() = assertEquals(120.0, conv(Category.TIME, "h", "min", 2.0), 1e-9)

    @Test fun radiansToDegrees() = assertEquals(180.0, conv(Category.ANGLE, "rad", "°", Math.PI), 1e-9)

    @Test fun everyCategoryHasAtLeastThreeUnits() {
        Category.values().forEach { assertTrue(Converter.unitsOf(it).size >= 3) }
    }

    @Test fun roundTripIsStable() {
        Category.values().filter { it != Category.TEMPERATURE }.forEach { cat ->
            val list = Converter.unitsOf(cat)
            val back = Converter.convert(cat, list[1], list[0], Converter.convert(cat, list[0], list[1], 7.0))
            assertEquals(7.0, back, 1e-9)
        }
    }

    @Test fun formatTrimsZeros() {
        assertEquals("1.5", Converter.format(1.5))
        assertEquals("0.001", Converter.format(0.001))
        assertEquals("0", Converter.format(0.0))
        assertEquals("0.333333", Converter.format(1.0 / 3.0))
    }

    @Test fun formatHugeUsesScientific() = assertTrue(Converter.format(1e15).contains("E"))

    @Test fun parseEmpty() = assertTrue(Converter.parseInput("", Category.LENGTH) is ParseResult.Empty)

    @Test fun parseLetters() =
        assertTrue(Converter.parseInput("abc", Category.LENGTH) is ParseResult.Invalid)

    @Test fun parseTwoDots() =
        assertTrue(Converter.parseInput("1.2.3", Category.LENGTH) is ParseResult.Invalid)

    @Test fun parseCommaDecimal() =
        assertEquals(1.5, (Converter.parseInput("1,5", Category.LENGTH) as ParseResult.Valid).value, 0.0)

    @Test fun negativeRejectedOutsideTemperature() =
        assertTrue(Converter.parseInput("-5", Category.LENGTH) is ParseResult.Invalid)

    @Test fun negativeAllowedForTemperature() =
        assertTrue(Converter.parseInput("-5", Category.TEMPERATURE) is ParseResult.Valid)
}