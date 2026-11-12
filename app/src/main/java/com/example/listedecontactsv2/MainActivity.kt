package com.example.listedecontactsv2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val contacts = mutableListOf(
        Contact("Dupont", "Claire", "06 12 34 56 78", "claire.dupont@example.com"),
        Contact("Martin", "Thomas", "07 23 45 67 89", "thomas.martin@example.com"),
        Contact("Bernard", "Sophie", "06 98 76 54 32", "sophie.bernard@example.com"),
        Contact("Petit", "Lucas", "07 11 22 33 44", "lucas.petit@example.com"),
        Contact("Moreau", "Julien", "06 55 44 33 22", "julien.moreau@example.com")
    )

    private lateinit var adapter: ContactAdapter
    private var selectedIndex = -1

    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                @Suppress("DEPRECATION")
                val updated = result.data?.getSerializableExtra("contact") as? Contact
                if (updated != null && selectedIndex in contacts.indices) {
                    contacts[selectedIndex] = updated
                    adapter.notifyDataSetChanged()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)
        adapter = ContactAdapter(this, contacts)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedIndex = position
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("contact", contacts[position].copy())
            detailLauncher.launch(intent)
        }
    }
}