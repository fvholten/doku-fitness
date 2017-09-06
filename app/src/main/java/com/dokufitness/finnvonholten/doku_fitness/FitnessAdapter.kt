package com.dokufitness.finnvonholten.doku_fitness

import android.app.AlertDialog
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import com.google.firebase.database.DatabaseReference

internal class FitnessAdapter(private val fitnessElements: List<FitnessElement>, private val context: Context, private val databaseReference: DatabaseReference, private val uid: String) : RecyclerView.Adapter<FitnessAdapter.Holder>() {

    private var mCallBack: OnEditClickListener? = null

    internal inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var tool_tw: TextView = view.findViewById(R.id.tool_tw)
        var duration_tw: TextView = view.findViewById(R.id.duration_tw)
        var sets_tw: TextView = view.findViewById(R.id.sets_tw)
        var reps_tw: TextView = view.findViewById(R.id.reps_tw)
        var ratingBar: RatingBar = view.findViewById(R.id.rating)
        var fabDelete: FloatingActionButton = view.findViewById(R.id.delete)
        var fabEdit: FloatingActionButton = view.findViewById(R.id.edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_element, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val res = context.resources
        val (tool, duration, sets, reps1, rating) = fitnessElements[position]
        holder.tool_tw.text = tool
        holder.duration_tw.text = res.getString(R.string.duration, duration)
        holder.sets_tw.text = res.getString(R.string.sets, sets)
        var reps = ""
        for (i in reps1) {
            reps += if (reps == "")
                i.toString() + " "
            else
                " - " + i
        }

        holder.reps_tw.text = res.getString(R.string.reps, reps)
        holder.ratingBar.rating = java.lang.Float.parseFloat(rating.toString() + "")

        holder.fabDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.delete)
                    .setMessage(R.string.deleteData)
                    .setPositiveButton(R.string.yes, { _, _ -> databaseReference.child("Users").child(uid).child(fitnessElements[position].timestamp).setValue(null) })
                    .setNegativeButton(R.string.no, null)
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        holder.fabEdit.setOnClickListener {
            mCallBack?.EditClicked(fitnessElements[position])
        }
    }

    interface OnEditClickListener {
        fun EditClicked(fitnessElement: FitnessElement)
    }

    fun setOnEditClickedListener(mCallBack: OnEditClickListener) {
        this.mCallBack = mCallBack
    }

    override fun getItemCount(): Int {
        return fitnessElements.size
    }
}

