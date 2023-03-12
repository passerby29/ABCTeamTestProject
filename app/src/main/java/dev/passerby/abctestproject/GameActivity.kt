package dev.passerby.abctestproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import dev.passerby.abctestproject.Constants.moleIdArray
import dev.passerby.abctestproject.databinding.ActivityGameBinding
import java.util.*

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private var mainCountDownTimer: CountDownTimer? = null
    private val availableMoleIdArrayList: ArrayList<Int> = ArrayList()
    private var moleIsActive = false
    private var countDownWorks = false
    private var score = 0
    private var leftTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fillMolesArrayList(0)
        startTimer(GAME_TIME * MILLIS_IN_SECONDS)
        binding.scoreTxt.text = score.toString()
        binding.pauseGameBtn.setOnClickListener {
            pauseClickListener()
        }
    }

    private fun fillMolesArrayList(notAvailableMoleId: Int) {
        availableMoleIdArrayList.addAll(moleIdArray)
        // Removing last mole from list
        if (notAvailableMoleId != 0) {
            availableMoleIdArrayList.remove(notAvailableMoleId)
        }
    }

    // Start main game timer
    private fun startTimer(time: Long) {
        mainCountDownTimer =
            object : CountDownTimer(time, INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                    countDownWorks = true
                    binding.countDownTimerTxt.text = formatTime(millisUntilFinished)
                    leftTime = millisUntilFinished
                    if (!moleIsActive) {
                        showMole()
                    }
                }

                override fun onFinish() {
                    launchResultActivity()
                }
            }.start()
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - minutes * SECONDS_IN_MINUTES
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun showMole() {
        val available = availableMoleIdArrayList
        val activeMole = findViewById<ImageButton>(available[Random().nextInt(available.size)])
        activeMole.apply {
            setImageResource(R.drawable.mole)
            isEnabled = true
        }
        moleIsActive = true
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            availableMoleIdArrayList.clear()
            activeMole.apply {
                fillMolesArrayList(this.id)
                setImageResource(R.drawable.hole)
                isEnabled = false
            }
            moleIsActive = false
        }
        handler.postDelayed(runnable, 500)
        activeMole.setOnClickListener {
            activeMole.setImageResource(R.drawable.hole)
            binding.scoreTxt.text = "${++score}"
        }
    }

    private fun launchResultActivity() {
        val intent = ResultActivity.newIntent(this, score)
        startActivity(intent)
    }

    private fun pauseClickListener() {
        if (countDownWorks) {
            pauseGame()
            return
        }
        resumeGame()
    }
    private fun pauseGame() {
        countDownWorks = false
        mainCountDownTimer!!.cancel()
        binding.pauseGameBtn.setImageResource(R.drawable.play_selector)
    }
    private fun resumeGame() {
        startTimer(leftTime)
        binding.pauseGameBtn.setImageResource(R.drawable.pause_selector)
    }
    companion object {

        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60L
        private const val GAME_TIME = 30
        private const val INTERVAL = 1L

        fun newIntent(context: Context): Intent {
            return Intent(context, GameActivity::class.java)
        }
    }
}