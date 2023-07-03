package com.octopus.task.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.octopus.task.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}