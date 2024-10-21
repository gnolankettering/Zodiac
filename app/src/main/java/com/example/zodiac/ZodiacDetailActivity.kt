package com.example.zodiac

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class ZodiacDetailActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zodiac_detail)

        val zodiacName = intent.getStringExtra("zodiac_name")
        val zodiacDetails = getZodiacDetails(zodiacName)

        val ivZodiacImage: ImageView = findViewById(R.id.ivZodiacImage)
        val tvName: TextView = findViewById(R.id.tvZodiacName)
        val tvMonth: TextView = findViewById(R.id.tvZodiacMonth)
        val tvDescription: TextView = findViewById(R.id.tvZodiacDescription)
        val tvDailyHoroscope: TextView = findViewById(R.id.tvDailyHoroscope)

        if (zodiacDetails != null) {
            val imageResId = resources.getIdentifier(zodiacDetails.getString("image"), "drawable", packageName)
            ivZodiacImage.setImageResource(imageResId)
            tvName.text = zodiacDetails.getString("name")
            tvMonth.text = zodiacDetails.getString("month")
            tvDescription.text = zodiacDetails.getString("description")

            // Fetch daily horoscope
            fetchDailyHoroscope(zodiacName?.toLowerCase() ?: "")
        }
    }

    private fun fetchDailyHoroscope(zodiacSign: String) {
        val url = "https://truth-open-acorn.glitch.me/daily_horoscope?zodiac_sign=$zodiacSign"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseData = it.string()
                    val jsonObject = JSONObject(responseData)
                    val dailyHoroscope = jsonObject.getString("horoscope")
                    runOnUiThread {
                        findViewById<TextView>(R.id.tvDailyHoroscope).text = dailyHoroscope
                    }
                }
            }
        })
    }

    private fun getZodiacDetails(name: String?): JSONObject? {
        val jsonString = loadJsonFromAssets("zodiac_signs.json")
        if (jsonString != null) {
            val jsonObject = JSONObject(jsonString)
            val jsonArray: JSONArray = jsonObject.getJSONArray("zodiac_signs")
            for (i in 0 until jsonArray.length()) {
                val zodiacObject = jsonArray.getJSONObject(i)
                if (zodiacObject.getString("name") == name) {
                    return zodiacObject
                }
            }
        }
        return null
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
