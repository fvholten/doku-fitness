package com.dokufitness.finnvonholten.doku_fitness

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.HorizontalScrollView
import com.shawnlin.numberpicker.NumberPicker

class RepsAndSets(viewArray: List<NumberPicker>, reps1: NumberPicker, reps2: NumberPicker, reps3: NumberPicker, reps4: NumberPicker, reps5: NumberPicker,
                  reps6: NumberPicker, reps7: NumberPicker, reps8: NumberPicker, reps9: NumberPicker, reps10: NumberPicker, sets_np: NumberPicker, private val context: Context,
                  private val scrollView: HorizontalScrollView, private val scrollBarHelp: View) {

    private var mCallBackSets: OnSetsChangedListener? = null
    private var mCallBackReps: OnRepsChangedListener? = null
    private var repsContent: HashMap<NumberPicker, Int> = HashMap()

    init {
        scrollableListener()
        sets_np.setOnValueChangedListener { _, oldVal, newVal ->
            if (newVal < oldVal) {
                for (i in oldVal downTo newVal + 1) {
                    if (viewArray[i - 1].id == R.id.reps1)
                        viewArray[i - 1].visibility = View.INVISIBLE
                    else
                        viewArray[i - 1].visibility = View.GONE
                }
            } else
                for (i in 0..(newVal - 1)) {
                    viewArray[i].visibility = View.VISIBLE
                }
            mCallBackSets?.SetsChanged(newVal.toString())
        }

        reps1.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps2.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps3.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps4.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps5.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps6.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps7.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps8.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps9.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
        reps10.setOnValueChangedListener { picker, _, newValReps ->
            repsContent.put(picker, newValReps)
            mCallBackReps?.RepsChanged(repsContent.values.map { it })
        }
    }

    interface OnSetsChangedListener {
        fun SetsChanged(sets: String)
    }

    interface OnRepsChangedListener {
        fun RepsChanged(reps: List<Int>)
    }

    fun setOnSetsChangeListener(mCallBack: OnSetsChangedListener) {
        this.mCallBackSets = mCallBack
    }

    fun setOnRepsChangeListener(mCallBack: OnRepsChangedListener) {
        this.mCallBackReps = mCallBack
    }

    private fun scrollableListener() {
        val observer = scrollView.viewTreeObserver
        observer.addOnGlobalLayoutListener {
            val viewWidth = scrollView.measuredWidth
            val contentWidth = scrollView.getChildAt(0).width
            if (viewWidth - contentWidth < 0)
                scrollBarHelp.setBackgroundColor(ContextCompat.getColor(context, R.color.md_blue_grey_100))
            else
                scrollBarHelp.setBackgroundColor(ContextCompat.getColor(context, R.color.textOnPrimary))
        }
    }
}