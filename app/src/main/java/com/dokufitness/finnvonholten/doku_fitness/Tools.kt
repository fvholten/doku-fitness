package com.dokufitness.finnvonholten.doku_fitness

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class Tools(listView: ListView, databaseReference: DatabaseReference, context: Context) {

    private var mCallBack: OnItemClickListener? = null
    private var arrayAdapter: ArrayAdapter<String>? = null

    init {
        databaseReference.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        arrayAdapter = ArrayAdapter(
                                context,
                                android.R.layout.simple_list_item_1,
                                getFitnessTools(dataSnapshot))
                        listView.adapter = arrayAdapter
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            mCallBack?.ItemClicked((view as TextView).text as String)
        }
    }

    fun filterAdapter(newText: String) {
        if (arrayAdapter != null)
            arrayAdapter?.filter?.filter(newText)
    }

    interface OnItemClickListener {
        fun ItemClicked(tool: String)
    }

    fun setOnEditClickedListener(mCallBack: OnItemClickListener) {
        this.mCallBack = mCallBack
    }

    private fun getFitnessTools(dataSnapshot: DataSnapshot?): ArrayList<String> {
        val arrayList: ArrayList<String> = ArrayList(listOf<String>())
        dataSnapshot?.child("Fitness-Tools")?.children?.forEach { arrayList.add(it.key.toString() + " - " + it.value.toString()) }
        return arrayList
    }
}