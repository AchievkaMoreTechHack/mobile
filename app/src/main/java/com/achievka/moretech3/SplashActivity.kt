package com.achievka.animator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.achievka.moretech3.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}