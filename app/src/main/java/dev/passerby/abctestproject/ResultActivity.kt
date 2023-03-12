package dev.passerby.abctestproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.passerby.abctestproject.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private var score = 0
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE)
        parseIntent()
        binding.textView3.text = "Score: $score"
        binding.startGameBtn.setOnClickListener { launchGame() }
        binding.menuBtn.setOnClickListener { launchMenu() }
    }

    private fun parseIntent() {
        if (!intent.hasExtra(KEY_SCORE)) {
            throw RuntimeException("Param score is absent")
        }
        score = intent.getIntExtra(KEY_SCORE, 0)
        saveResult()
    }

    private fun saveResult() {
        val best = preferences.getInt(KEY_RESULT, 0)
        if (best < score){
            val editor = preferences.edit()
            editor.putInt(KEY_RESULT, score)
            editor.apply()
        }
    }

    private fun launchMenu() {
        val intent = MainActivity.newIntent(this)
        startActivity(intent)
    }

    private fun launchGame() {
        val intent = GameActivity.newIntent(this)
        startActivity(intent)
    }

    companion object {

        private const val KEY_SCORE = "score"
        const val KEY_RESULT = "score"

        fun newIntent(context: Context, score: Int): Intent {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(KEY_SCORE, score)
            return intent
        }
    }
}