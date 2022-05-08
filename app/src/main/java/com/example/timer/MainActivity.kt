package com.example.timer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    private var currentCountDownTimer: CountDownTimer? = null

    private val remainSecondTextView: TextView by lazy {
        findViewById(R.id.remainSecondTextView)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindViews() {

        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    updateRemainTime(p1 * 60 * 1000L)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    currentCountDownTimer?.cancel()
                    currentCountDownTimer = null
                    soundPool.autoPause()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    seekBar ?: return

                    if(seekBar.progress == 0){
                        currentCountDownTimer?.cancel()
                        currentCountDownTimer = null
                        soundPool.autoPause()

                    }else{
                        currentCountDownTimer =
                            createCountDownTimer(seekBar.progress * 60 * 1000L).start()
                        currentCountDownTimer?.start()

                        tickingSoundId?.let { soundId ->
                            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
                    }

                    }
                }
            }
        )
    }

    private fun initSounds() {
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(p0: Long) {

                updateRemainTime(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }



        private fun completeCountDown(){
            updateRemainTime(0)
            updateSeekBar(0)

            soundPool.autoPause()
            bellSoundId?.let { soundId ->
                soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
            }
        }




    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(reaminMillis: Long) {
        seekBar.progress = (reaminMillis / 1000 / 60).toInt()
    }
}