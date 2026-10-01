package com.example.unitconverter

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unitconverter.logic.Category

class MainActivity : AppCompatActivity() {

    private val allCategories = Category.values().toList()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = CategoryAdapter { category ->
            startActivity(
                Intent(this, ConverterActivity::class.java)
                    .putExtra(ConverterActivity.EXTRA_CATEGORY, category.name)
            )
        }
        findViewById<RecyclerView>(R.id.categoryList).apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = this@MainActivity.adapter
        }
        adapter.submit(allCategories)

        findViewById<EditText>(R.id.searchInput).doAfterTextChanged { text ->
            val query = text?.toString().orEmpty().trim()
            adapter.submit(allCategories.filter { it.title.contains(query, ignoreCase = true) })
        }
    }
}
