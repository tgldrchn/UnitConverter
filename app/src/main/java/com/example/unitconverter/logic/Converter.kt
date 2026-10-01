package com.example.unitconverter.logic

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs

/** Хэмжилтийн ангиллууд. */
enum class Category(val title: String) {
    LENGTH("Length"), AREA("Area"), VOLUME("Volume"), MASS("Mass"),
    TIME("Time"), SPEED("Speed"), TEMPERATURE("Temperature"), DENSITY("Density"),
    ENERGY("Energy"), ANGLE("Angle"), WEIGHT("Weight"), FUEL("Fuel")
}

/** [factor] нь 1 нэгж нь суурь нэгжийн хэдтэй тэнцэхийг заана. */
data class UnitDef(val name: String, val symbol: String, val factor: Double = 1.0) {
    override fun toString() = "$name ($symbol)"
}

/** Оролтын текстийг шалгасан үр дүн. */
sealed class ParseResult {
    object Empty : ParseResult()
    data class Invalid(val message: String) : ParseResult()
    data class Valid(val value: Double) : ParseResult()
}

object Converter {

    const val CELSIUS = "°C"
    const val FAHRENHEIT = "°F"
    const val KELVIN = "K"

    // Суурь нэгж: Length=м, Area=м², Volume=л, Mass=кг, Time=с, Speed=м/с,
    // Density=кг/м³, Energy=Ж, Angle=градус, Weight(хүч)=Н, Fuel=км/л
    private val units: Map<Category, List<UnitDef>> = mapOf(
        Category.LENGTH to listOf(
            UnitDef("Millimeter", "mm", 0.001), UnitDef("Centimeter", "cm", 0.01),
            UnitDef("Meter", "m", 1.0), UnitDef("Kilometer", "km", 1000.0),
            UnitDef("Inch", "in", 0.0254), UnitDef("Foot", "ft", 0.3048),
            UnitDef("Yard", "yd", 0.9144), UnitDef("Mile", "mi", 1609.344)
        ),
        Category.AREA to listOf(
            UnitDef("Square millimeter", "mm²", 1e-6), UnitDef("Square centimeter", "cm²", 1e-4),
            UnitDef("Square meter", "m²", 1.0), UnitDef("Square kilometer", "km²", 1e6),
            UnitDef("Hectare", "ha", 1e4), UnitDef("Square inch", "in²", 0.00064516),
            UnitDef("Square foot", "ft²", 0.09290304), UnitDef("Acre", "ac", 4046.8564224)
        ),
        Category.VOLUME to listOf(
            UnitDef("Milliliter", "mL", 0.001), UnitDef("Liter", "L", 1.0),
            UnitDef("Cubic meter", "m³", 1000.0), UnitDef("Teaspoon (US)", "tsp", 0.00492892159375),
            UnitDef("Tablespoon (US)", "tbsp", 0.01478676478125), UnitDef("Cup (US)", "cup", 0.2365882365),
            UnitDef("Gallon (US)", "gal", 3.785411784), UnitDef("Cubic foot", "ft³", 28.316846592)
        ),
        Category.MASS to listOf(
            UnitDef("Milligram", "mg", 1e-6), UnitDef("Gram", "g", 0.001),
            UnitDef("Kilogram", "kg", 1.0), UnitDef("Tonne", "t", 1000.0),
            UnitDef("Ounce", "oz", 0.028349523125), UnitDef("Pound", "lb", 0.45359237),
            UnitDef("Stone", "st", 6.35029318)
        ),
        Category.TIME to listOf(
            UnitDef("Millisecond", "ms", 0.001), UnitDef("Second", "s", 1.0),
            UnitDef("Minute", "min", 60.0), UnitDef("Hour", "h", 3600.0),
            UnitDef("Day", "d", 86400.0), UnitDef("Week", "wk", 604800.0),
            UnitDef("Year", "yr", 31557600.0)
        ),
        Category.SPEED to listOf(
            UnitDef("Meter/second", "m/s", 1.0), UnitDef("Kilometer/hour", "km/h", 1.0 / 3.6),
            UnitDef("Mile/hour", "mph", 0.44704), UnitDef("Knot", "kn", 1852.0 / 3600.0),
            UnitDef("Foot/second", "ft/s", 0.3048), UnitDef("Mach", "Ma", 340.29)
        ),
        Category.TEMPERATURE to listOf(
            UnitDef("Celsius", CELSIUS), UnitDef("Fahrenheit", FAHRENHEIT), UnitDef("Kelvin", KELVIN)
        ),
        Category.DENSITY to listOf(
            UnitDef("Kilogram/m³", "kg/m³", 1.0), UnitDef("Gram/L", "g/L", 1.0),
            UnitDef("Gram/cm³", "g/cm³", 1000.0), UnitDef("Kilogram/L", "kg/L", 1000.0),
            UnitDef("Pound/ft³", "lb/ft³", 16.01846337), UnitDef("Pound/in³", "lb/in³", 27679.9047)
        ),
        Category.ENERGY to listOf(
            UnitDef("Joule", "J", 1.0), UnitDef("Kilojoule", "kJ", 1000.0),
            UnitDef("Calorie", "cal", 4.184), UnitDef("Kilocalorie", "kcal", 4184.0),
            UnitDef("Watt-hour", "Wh", 3600.0), UnitDef("Kilowatt-hour", "kWh", 3.6e6),
            UnitDef("BTU", "BTU", 1055.05585262)
        ),
        Category.ANGLE to listOf(
            UnitDef("Degree", "°", 1.0), UnitDef("Radian", "rad", 180.0 / Math.PI),
            UnitDef("Gradian", "grad", 0.9), UnitDef("Arcminute", "′", 1.0 / 60.0),
            UnitDef("Arcsecond", "″", 1.0 / 3600.0), UnitDef("Turn", "turn", 360.0)
        ),
        Category.WEIGHT to listOf(
            UnitDef("Newton", "N", 1.0), UnitDef("Kilonewton", "kN", 1000.0),
            UnitDef("Kilogram-force", "kgf", 9.80665), UnitDef("Gram-force", "gf", 0.00980665),
            UnitDef("Pound-force", "lbf", 4.4482216152605), UnitDef("Dyne", "dyn", 1e-5)
        ),
        Category.FUEL to listOf(
            UnitDef("Kilometer/liter", "km/L", 1.0), UnitDef("Meter/liter", "m/L", 0.001),
            UnitDef("Mile/liter", "mi/L", 1.609344),
            UnitDef("Mile/gallon (US)", "mpg US", 1.609344 / 3.785411784),
            UnitDef("Mile/gallon (UK)", "mpg UK", 1.609344 / 4.54609),
            UnitDef("Kilometer/gallon (US)", "km/gal", 1.0 / 3.785411784)
        )
    )

    fun unitsOf(category: Category): List<UnitDef> = units.getValue(category)

    /** Анхдагч From/To нэгжийн индекс. */
    fun defaultIndices(category: Category): Pair<Int, Int> = when (category) {
        Category.LENGTH -> 2 to 3          // метр -> километр
        Category.TEMPERATURE -> 0 to 1     // Цельс -> Фаренгейт
        else -> 0 to 1
    }

    /** Суурь нэгжээр дамжуулан хөрвүүлнэ. Хос бүрийг hard-code хийхгүй. */
    fun convert(category: Category, from: UnitDef, to: UnitDef, value: Double): Double {
        if (from == to) return value
        if (category == Category.TEMPERATURE) return convertTemperature(from.symbol, to.symbol, value)
        return value * from.factor / to.factor
    }

    // Температур: шилжилттэй томьёо тул Цельсээр дамжуулна.
    private fun convertTemperature(from: String, to: String, value: Double): Double {
        val celsius = when (from) {
            FAHRENHEIT -> (value - 32.0) * 5.0 / 9.0
            KELVIN -> value - 273.15
            else -> value
        }
        return when (to) {
            FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
            KELVIN -> celsius + 273.15
            else -> celsius
        }
    }

    /** Оролтыг шалгана. Сөрөг утгыг зөвхөн Температурт зөвшөөрнө. */
    fun parseInput(text: String, category: Category): ParseResult {
        val raw = text.trim().replace(',', '.')
        if (raw.isEmpty()) return ParseResult.Empty
        if (raw == "-" || raw == ".") return ParseResult.Empty // бичиж байгаа хэсэг
        if (!raw.matches(Regex("-?\\d*\\.?\\d*([eE][-+]?\\d+)?"))) {
            return ParseResult.Invalid("Зөвхөн тоо оруулна уу")
        }
        val number = raw.toDoubleOrNull() ?: return ParseResult.Invalid("Буруу тоо")
        if (number < 0 && category != Category.TEMPERATURE) {
            return ParseResult.Invalid("Сөрөг утга зөвхөн Температурт зөвшөөрөгдөнө")
        }
        if (number.isInfinite() || number.isNaN()) return ParseResult.Invalid("Хэт том утга")
        return ParseResult.Valid(number)
    }

    /** Хамгийн ихдээ 6 орон, төгсгөлийн тэг хасна; хэт том/жижиг бол шинжлэх ухааны хэлбэр. */
    fun format(value: Double): String {
        if (value == 0.0) return "0"
        if (value.isNaN() || value.isInfinite()) return "—"
        val a = abs(value)
        if (a < 1e-6 || a >= 1e12) return DecimalFormat("0.######E0").format(value)
        return BigDecimal(value).setScale(6, RoundingMode.HALF_UP)
            .stripTrailingZeros().toPlainString()
    }
}