package com.example.virtuwear.screen

import com.example.virtuwear.MainActivity
import com.example.virtuwear.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val splashTime: Long = 3000 // 5 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }

        val centerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0
            ).apply {
                weight = 1f
            }
        }

        val logo = ImageView(this).apply {
            setImageResource(R.drawable.logovirtuwear)
            layoutParams = LinearLayout.LayoutParams(600, 600).apply {
                bottomMargin = 16
                gravity = Gravity.CENTER
            }
        }

        val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            isIndeterminate = true
            layoutParams = LinearLayout.LayoutParams(600, 45).apply {
                topMargin = 8
                gravity = Gravity.CENTER
            }
        }

        val tulisan = android.widget.TextView(this).apply {
            text = "VirtuWear"
            textSize = 16f
            typeface = resources.getFont(R.font.airbnb_cereal_w_md)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
                bottomMargin = 70
            }
        }
        progressBar.indeterminateDrawable.setTint(Color.BLACK)

        centerContainer.addView(logo)
        centerContainer.addView(progressBar)

        mainLayout.addView(centerContainer)
        mainLayout.addView(tulisan)

        setContentView(mainLayout)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashTime)
    }
}