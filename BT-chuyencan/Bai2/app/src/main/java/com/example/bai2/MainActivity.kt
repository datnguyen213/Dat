package com.example.bai2

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.example.bai2.databinding.ActivityMainBinding
import android.os.*
import android.view.View
import android.widget.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var tvCount: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var progressBar: ProgressBar

    private var isCounting = false
    private var countThread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view
        tvCount = findViewById(R.id.tvCount)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        progressBar = findViewById(R.id.progressBar)

        btnStart.setOnClickListener {
            if (!isCounting) startCounting()
        }

        btnStop.setOnClickListener {
            stopCounting()
        }
    }

    private fun startCounting() {
        isCounting = true
        progressBar.visibility = View.VISIBLE
        tvCount.text = "Bắt đầu..."

        countThread = thread {
            for (i in 1..10) {
                if (!isCounting) break

                runOnUiThread {
                    tvCount.text = "$i"
                }
                Thread.sleep(1000)
            }

            if (isCounting) {
                runOnUiThread {
                    tvCount.text = "Hoàn thành!"
                    progressBar.visibility = View.GONE
                }
            }
            isCounting = false
        }
    }

    private fun stopCounting() {
        isCounting = false
        progressBar.visibility = View.GONE
        tvCount.text = "Đã dừng."
    }
}