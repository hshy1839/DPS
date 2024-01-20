package com.example.dps


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView


class MainActivity : AppCompatActivity() {

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<ImageView>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val heartrateBtn = findViewById<CardView>(R.id.heartrate_btn)
        heartrateBtn.setOnClickListener {
            val intent = Intent (this@MainActivity, HeartbeatActivity::class.java)
            startActivity(intent)
        }

        val workoutBtn = findViewById<CardView>(R.id.workout_btn)
        workoutBtn.setOnClickListener{
            val intent = Intent ( this@MainActivity, WorkoutActivity::class.java)
            startActivity(intent)
        }


        val sleepBtn = findViewById<CardView>(R.id.sleep_btn)
        sleepBtn.setOnClickListener{
            val intent = Intent ( this@MainActivity, SleepActivity::class.java)
            startActivity(intent)
        }

        firstTextView = findViewById(R.id.firstTextView)
        secondTextView = findViewById(R.id.secondTextView)

        firstTextView.alpha = 0f
        secondTextView.alpha = 0f

        firstTextView.animate()
            .alpha(1f)
            .setDuration(1000)
            .withStartAction { }
            .withEndAction {
                secondTextView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start()
            }
            .start()
    }

    override fun onResume() {
        super.onResume()
        startAnimation()
    }

    private fun startAnimation() {
        firstTextView.alpha = 0f
        secondTextView.alpha = 0f

        firstTextView.animate()
            .alpha(1f)
            .setDuration(1000)
            .withStartAction { }
            .withEndAction {
                secondTextView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start()
            }
            .start()

    }
}
