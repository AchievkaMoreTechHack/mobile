package com.achievka.animator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.achievka.moretech3.GameActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }
}