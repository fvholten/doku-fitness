package com.dokufitness.finnvonholten.doku_fitness

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.shawnlin.numberpicker.NumberPicker

class AddElementActivity : AppCompatActivity() {

    var fitnessTools: ArrayList<String> = ArrayList()
    var arrayAdapter: ArrayAdapter<String>? = null

    private var fitnessElement: FitnessElement = FitnessElement()
    private var npMinutes: NumberPicker? = null
    private var npHours: NumberPicker? = null
    private var npSeconds: NumberPicker? = null

    private var minute: String = "00"
    private var second: String = "00"
    private var hour: String = "00"

    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var firebaseUser: FirebaseUser? = null

    private var viewArray: Array<View>? = null

    private var containerArray: List<View>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_element)

        containerArray = listOf(findViewById(R.id.select_tool_view),
                findViewById(R.id.duration_view),
                findViewById(R.id.set_reps_view),
                findViewById(R.id.rating_view))

        changeView(findViewById(R.id.select_tool_view))

        npMinutes = findViewById(R.id.np_minutes)
        npHours = findViewById(R.id.np_hours)
        npSeconds = findViewById(R.id.np_seconds)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        getData()
        createList()
    }

    private fun changeView(view: View) {
        containerArray?.forEach { if (it == view) it.visibility = View.VISIBLE else it.visibility = View.GONE }
    }

    private fun createList() {
        val itemsList = findViewById<ListView>(R.id.items)
        itemsList.adapter = arrayAdapter
        itemsList.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            changeView(findViewById(R.id.duration_view))

            val tool = arrayAdapter!!.getItem(i)
            (findViewById<View>(R.id.fitnessTool) as TextView).text = tool
            if (tool != null)
                fitnessElement.tool = tool.substring(0, 3)
            else
                fitnessElement.tool = "000"
            setRepsAndSets()
            rollOverListener()
        }
    }

    private fun setDuration() {
        fitnessElement.duration = "$hour:$minute:$second"
    }

    private fun setRepsAndSets() {
        findViewById<View>(R.id.continueButton).setOnClickListener {
            setDuration()
            changeView(findViewById(R.id.set_reps_view))

            activateRepsPicker(findViewById<View>(R.id.sets) as NumberPicker)
            scrollableListener()
            findViewById<View>(R.id.continueButtonReps).setOnClickListener {
                fitnessElement.sets = (findViewById<View>(R.id.sets) as NumberPicker).value.toString()
                var ints: List<Int> = listOf()
                viewArray!!.filter { (it as NumberPicker).visibility == View.VISIBLE }.forEach { ints += (it as NumberPicker).value }
                fitnessElement.reps = ints
                rating()
            }
        }
    }

    private fun rating() {
        changeView(findViewById(R.id.rating_view))
        findViewById<View>(R.id.addButtonRating).setOnClickListener {
            fitnessElement.rating = findViewById<RatingBar>(R.id.rating).rating.toDouble()
            Uploader().upLoadFitnessElement(databaseReference, firebaseUser!!.uid, fitnessElement)
            startActivity(Intent(this@AddElementActivity, MainActivity::class.java))
        }
    }

    private fun scrollableListener() {
        val scrollView = findViewById<HorizontalScrollView>(R.id.scrollView)
        val observer = scrollView.viewTreeObserver
        observer.addOnGlobalLayoutListener {
            val viewWidth = scrollView.measuredWidth
            val contentWidth = scrollView.getChildAt(0).width
            if (viewWidth - contentWidth < 0)
                findViewById<View>(R.id.scrollBarHelp).setBackgroundColor(ContextCompat.getColor(this@AddElementActivity, R.color.md_blue_grey_100))
            else
                findViewById<View>(R.id.scrollBarHelp).setBackgroundColor(ContextCompat.getColor(this@AddElementActivity, R.color.textOnPrimary))
        }
    }

    private fun activateRepsPicker(repsPicker: NumberPicker) {
        viewArray = arrayOf(findViewById(R.id.reps1), findViewById(R.id.reps2), findViewById(R.id.reps3), findViewById(R.id.reps4), findViewById(R.id.reps5), findViewById(R.id.reps6), findViewById(R.id.reps7), findViewById(R.id.reps8), findViewById(R.id.reps9), findViewById(R.id.reps10))
        repsPicker.setOnValueChangedListener { _, oldVal, newVal ->
            if (newVal < oldVal) {
                for (i in oldVal downTo newVal + 1) {
                    if (viewArray!![i - 1] === findViewById<View>(R.id.reps1))
                        viewArray!![i - 1].visibility = View.INVISIBLE
                    else
                        viewArray!![i - 1].visibility = View.GONE
                }
            } else
                for (i in 0..(newVal - 1))
                    viewArray!![i].visibility = View.VISIBLE
        }
    }

    private fun rollOverListener() {
        npHours?.setOnValueChangedListener { _, _, newVal -> hour = String.format("%02d", newVal) }
        npMinutes?.setOnValueChangedListener { _, oldVal, newVal ->
            minute = String.format("%02d", newVal)
            if (oldVal == 59 && newVal == 0)
                npHours?.value = npHours?.value!!.plus(1)
        }
        npSeconds?.setOnValueChangedListener { _, oldVal, newVal ->
            second = String.format("%02d", newVal)
            if (oldVal == 59 && newVal == 0)
                npMinutes?.value = npMinutes?.value!!.plus(1)
        }
    }

    private fun getData() {
        FirebaseDatabase.getInstance().reference.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        arrayAdapter = ArrayAdapter(
                                this@AddElementActivity,
                                android.R.layout.simple_list_item_1,
                                getFitnessTools(dataSnapshot, fitnessTools))
                        (findViewById<View>(R.id.items) as ListView).adapter = arrayAdapter
                        fitnessTools = getFitnessTools(dataSnapshot, fitnessTools)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        (menu.findItem(R.id.action_search).actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (arrayAdapter != null)
                    arrayAdapter?.filter?.filter(newText)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@AddElementActivity, LoginActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getFitnessTools(dataSnapshot: DataSnapshot, fitnessTools: ArrayList<String>): ArrayList<String> {
        dataSnapshot.child("Fitness-Tools").children.forEach { fitnessTools.add(it.key.toString() + " - " + it.value.toString()) }
        return fitnessTools
    }
}
