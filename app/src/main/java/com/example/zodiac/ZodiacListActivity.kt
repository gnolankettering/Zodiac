package com.example.zodiac

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class ZodiacListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zodiac_list)

        val zodiacSigns = getZodiacSignsFromJson()
        val listView: ListView = findViewById(R.id.listViewZodiac)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, zodiacSigns)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedZodiac = zodiacSigns[position]
            val intent = Intent(this, ZodiacDetailActivity::class.java)
            intent.putExtra("zodiac_name", selectedZodiac)
            startActivity(intent)
        }
    }

    private fun getZodiacSignsFromJson(): List<String> {
        val zodiacSigns = mutableListOf<String>()
        val jsonString = loadJsonFromAssets("zodiac_signs.json")
        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            val jsonArray: JSONArray = jsonObject.getJSONArray("zodiac_signs")
            for (i in 0 until jsonArray.length()) {
                val zodiacObject = jsonArray.getJSONObject(i)
                val name = zodiacObject.getString("name")
                zodiacSigns.add(name)
            }
        }
        return zodiacSigns
    }

    private fun loadJsonFromAssets(filename: String): String? {
        return try {
            val inputStream: InputStream = assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }
}
