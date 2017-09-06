package com.dokufitness.finnvonholten.doku_fitness

import com.shawnlin.numberpicker.NumberPicker

class Duration(npSeconds: NumberPicker, npMinutes: NumberPicker, npHours: NumberPicker) {

    private var hour = "00"
    private var minute = "00"
    private var second = "00"

    init {
        npHours.setOnValueChangedListener { _, _, newVal ->
            hour = String.format("%02d", newVal)
            mCallBack?.DurationChanged(buildDurationString(hour, minute, second))
        }
        npMinutes.setOnValueChangedListener { _, oldVal, newVal ->
            minute = String.format("%02d", newVal)
            if (oldVal == 59 && newVal == 0)
                npHours.value = npHours.value.plus(1)
            mCallBack?.DurationChanged(buildDurationString(hour, minute, second))
        }
        npSeconds.setOnValueChangedListener { _, oldVal, newVal ->
            second = String.format("%02d", newVal)
            if (oldVal == 59 && newVal == 0)
                npMinutes.value = npMinutes.value.plus(1)
            mCallBack?.DurationChanged(buildDurationString(hour, minute, second))
        }
    }

    private var mCallBack: OnDurationChangeListener? = null

    interface OnDurationChangeListener {
        fun DurationChanged(string: String)
    }

    fun setOnDurationChangeListener(mCallBack: OnDurationChangeListener) {
        this.mCallBack = mCallBack
    }

    private fun buildDurationString(hour: String, minute: String, second: String): String {
        return "$hour:$minute:$second"
    }
}