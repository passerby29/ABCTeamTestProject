package dev.passerby.abctestproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import dev.passerby.abctestproject.databinding.ActivityGameBinding
import java.util.*

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private var mainCountDownTimer: CountDownTimer? = null
    private val availableMoleIdArrayList: ArrayList<Int> = ArrayList()
    private var moleIdArray = intArrayOf(
        R.id.firstMoleBtn,
        R.id.secondMoleBtn,
        R.id.thirdMoleBtn,
        R.id.fourthMoleBtn,
        R.id.fifthMoleBtn,
        R.id.sixthMoleBtn,
        R.id.seventhMoleBtn,
        R.id.eighthMoleBtn,
        R.id.ninthMoleBtn
    )
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
            rePauseTimer()
        }
    }

    private fun launchResultActivity() {
        val intent = ResultActivity.newIntent(this, score)
        startActivity(intent)
    }

    private fun fillMolesArrayList(notAvailableMoleId: Int) {
        for (i in moleIdArray.indices) {
            availableMoleIdArrayList.add(moleIdArray[i])
        }
        // Removing last mole from list
        if (notAvailableMoleId != 0) {
            availableMoleIdArrayList.remove(notAvailableMoleId)
        }
    }

    private fun rePauseTimer() {
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

    // Resume game
    private fun resumeGame() {
        startTimer(leftTime)
        binding.pauseGameBtn.setImageResource(R.drawable.pause_selector)
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - minutes * SECONDS_IN_MINUTES
        return String.format("%02d:%02d", minutes, leftSeconds)
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

    private fun showMole() {
        val activeMole = findViewById<ImageButton>(
            availableMoleIdArrayList[Random().nextInt(availableMoleIdArrayList.size)]
        )
        activeMole.setImageResource(R.drawable.mole)
        activeMole.isEnabled = true
        moleIsActive = true
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            availableMoleIdArrayList.clear()
            fillMolesArrayList(activeMole.id)
            activeMole.setImageResource(R.drawable.hole)
            activeMole.isEnabled = false
            moleIsActive = false
        }
        handler.postDelayed(runnable, 500)
        for (i in moleIdArray.indices) {
            val imageButton = findViewById<ImageButton>(moleIdArray[i])
            imageButton.setOnClickListener(View.OnClickListener {
                if (!imageButton.isEnabled) return@OnClickListener
                imageButton.setImageResource(R.drawable.hole)
                binding.scoreTxt.text = "${++score}"
            })
        }
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