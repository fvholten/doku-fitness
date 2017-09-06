package com.dokufitness.finnvonholten.doku_fitness

import android.widget.RatingBar

class Rating(ratingBar: RatingBar) {
    private var mCallBack: OnRatingChangeListener? = null
    init {
        ratingBar.setOnRatingBarChangeListener { _, fl, _ -> mCallBack?.RatingChanged(fl.toDouble()) }
    }

    interface OnRatingChangeListener {
        fun RatingChanged(rating: Double)
    }

    fun setOnRatingChangeListener(mCallBack: OnRatingChangeListener) {
        this.mCallBack = mCallBack
    }
}