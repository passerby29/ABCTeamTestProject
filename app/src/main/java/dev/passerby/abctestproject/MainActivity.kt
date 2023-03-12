package dev.passerby.abctestproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.passerby.abctestproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        getBestResult()
        binding.startGameBtn.setOnClickListener { launchGameActivity() }
    }

    private fun launchGameActivity() {
        startActivity(GameActivity.newIntent(this))
    }

    private fun getBestResult() {
        val flag = preferences.contains(ResultActivity.KEY_RESULT)
        if (flag) {
            val bestResult = "Best: ${preferences.getInt(ResultActivity.KEY_RESULT, 0)}"
            binding.recordTxt.apply {
                visibility = View.VISIBLE
                text = bestResult
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}