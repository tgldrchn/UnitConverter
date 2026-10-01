package com.example.unitconverter

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.unitconverter.logic.Category
import com.example.unitconverter.logic.Converter
import com.example.unitconverter.logic.ParseResult

class ConverterActivity : AppCompatActivity() {

    private lateinit var category: Category
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var valueInput: EditText
    private lateinit var resultText: TextView
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_converter)

        category = intent.getStringExtra(EXTRA_CATEGORY)
            ?.let { name -> Category.values().firstOrNull { it.name == name } }
            ?: Category.LENGTH

        findViewById<TextView>(R.id.titleText).text = category.title
        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        fromSpinner = findViewById(R.id.fromSpinner)
        toSpinner = findViewById(R.id.toSpinner)
        valueInput = findViewById(R.id.valueInput)
        resultText = findViewById(R.id.resultText)
        errorText = findViewById(R.id.errorText)

        setupSpinners()
        setupInput()
        findViewById<ImageView>(R.id.swapButton).setOnClickListener { swapUnits() }
    }

    private fun setupSpinners() {
        val units = Converter.unitsOf(category)
        for (spinner in listOf(fromSpinner, toSpinner)) {
            spinner.adapter = ArrayAdapter(this, R.layout.spinner_item, units).also {
                it.setDropDownViewResource(R.layout.spinner_dropdown_item)
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) = update()
                override fun onNothingSelected(p: AdapterView<*>?) = Unit
            }
        }
        val (fromIndex, toIndex) = Converter.defaultIndices(category)
        fromSpinner.setSelection(fromIndex)
        toSpinner.setSelection(toIndex)
    }

    private fun setupInput() {
        var type = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        if (category == Category.TEMPERATURE) type = type or InputType.TYPE_NUMBER_FLAG_SIGNED
        valueInput.inputType = type
        valueInput.doAfterTextChanged { update() }
    }

    private fun swapUnits() {
        val from = fromSpinner.selectedItemPosition
        fromSpinner.setSelection(toSpinner.selectedItemPosition)
        toSpinner.setSelection(from)
    }

    private fun update() {
        val units = Converter.unitsOf(category)
        val from = units[fromSpinner.selectedItemPosition]
        val to = units[toSpinner.selectedItemPosition]

        when (val parsed = Converter.parseInput(valueInput.text.toString(), category)) {
            ParseResult.Empty -> showResult("", "")
            is ParseResult.Invalid -> showResult("", parsed.message)
            is ParseResult.Valid ->
                showResult(Converter.format(Converter.convert(category, from, to, parsed.value)), "")
        }
    }

    private fun showResult(result: String, error: String) {
        resultText.text = result
        errorText.text = error
    }

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }
}