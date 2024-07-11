package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.view.View
import android.view.MotionEvent
import android.graphics.Color
//import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var colorView: View
    private var colorHue = 0f // Initial hue
    private var colorBrightness = 0.2f // Initial brightness
    private var startX = 0f
    private var startY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Enable dark text and icons for light backgrounds on both the status and navigation bars
        windowInsetsController.isAppearanceLightStatusBars = true  // Dark icons on light status bar
        windowInsetsController.isAppearanceLightNavigationBars = true  // Dark icons on light navigation bar



        colorView = findViewById(R.id.colorView)
        loadColorState()
        updateColor()
        colorView.setOnTouchListener(fun(_, event: MotionEvent): Boolean {
            handleTouch(event)
            return true
        })
    }

    private fun updateBarsColors (color: Int) {
        window.statusBarColor = color
        window.navigationBarColor = color
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = startX - event.x
                val deltaY = startY - event.y
                updateHue(deltaX)
                updateBrightness(deltaY)
                startX = event.x // Reset startX to the current position
                startY = event.y // Reset startY to the current position
            }
            MotionEvent.ACTION_UP -> {
                saveColorState() // Save the color state when the user finishes interaction
            }
        }
        return true
    }


    private fun updateHue(deltaX: Float) {
        colorHue += deltaX / 10 // Adjust the divisor for sensitivity
        colorHue = (colorHue + 360) % 360 // Wrap around the hue
        updateColor()
    }

    private fun updateBrightness(deltaY: Float) {
        colorBrightness += deltaY / 2_000 // Adjust the divisor for sensitivity
        colorBrightness = colorBrightness.coerceIn(0.2f, 1f) // Clamp the value between 0 and 1
        window.attributes = window.attributes.apply { screenBrightness = colorBrightness }
        updateColor()
        // Log.d("Brightness", "Current Brightness: $colorBrightness") // Log the brightness value
    }

    private fun updateColor() {
        // Convert HSV to RGB and update the view's background color
        val interpolatedColor = Color.HSVToColor(floatArrayOf(colorHue, 1.0f, colorBrightness))
        colorView.setBackgroundColor(interpolatedColor)
        updateBarsColors(interpolatedColor)
    }

    private fun saveColorState() {
        val sharedPreferences = getSharedPreferences("ColorPreferences", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putFloat("ColorHue", colorHue)
            putFloat("ColorBrightness", colorBrightness)
            apply()
        }
    }

    private fun loadColorState() {
        val sharedPreferences = getSharedPreferences("ColorPreferences", MODE_PRIVATE)
        colorHue = sharedPreferences.getFloat("ColorHue", 0f) // Default to 0 (red)
        colorBrightness = sharedPreferences.getFloat("ColorBrightness", 0.2f)
    }
}
