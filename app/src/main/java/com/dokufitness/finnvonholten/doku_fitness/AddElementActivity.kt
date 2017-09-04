package com.dokufitness.finnvonholten.doku_fitness

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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

class AddElementActivity : AppCompatActivity(), View.OnClickListener {

    private var arrayAdapter: ArrayAdapter<String>? = null
    private var itemsList: ListView? = null
    private var sets_np: NumberPicker? = null

    private var fitnessElement: FitnessElement = FitnessElement()

    private fun setTool(tool: String) {
        fitnessElement.tool = tool.substring(0, 3)
    }

    private fun setDuration(duration: String) {
        fitnessElement.duration = duration
    }

    private fun setReps(reps: List<Int>) {
        fitnessElement.reps = reps
    }

    private fun setSets(sets: String) {
        fitnessElement.sets = sets
    }

    private fun setRating(rating: Double) {
        fitnessElement.rating = rating
    }

    private var databaseReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var viewArray: Array<View>? = null

    private var currentView: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_element)

        backAndForth(findViewById(R.id.continueButton))

        findViewById<View>(R.id.select_tool_view).visibility = View.VISIBLE

        findViewById<View>(R.id.continueButton).setOnClickListener(this@AddElementActivity)
        findViewById<View>(R.id.backButton).setOnClickListener(this@AddElementActivity)

        //INIT-STUFF
        buildArrayAdapter()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().reference
        findViewById<View>(R.id.navigation).visibility = View.GONE

        initContainer()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this@AddElementActivity)
                .setTitle(R.string.abort)
                .setMessage(R.string.abortCreation)
                .setPositiveButton(R.string.yes, { _, _ -> super.onBackPressed() })
                .setNegativeButton(R.string.no, null)
                .create().show()
    }

    override fun onClick(view: View?) {
        backAndForth(view)
    }

    private fun backAndForth(view: View?) {

        val containerArray: List<View> = listOf(findViewById(R.id.select_tool_view),
                findViewById(R.id.duration_view),
                findViewById(R.id.set_reps_view),
                findViewById(R.id.rating_view))

        when (view) {
            findViewById<View>(R.id.backButton) -> {
                currentView--
                changeView(containerArray[currentView], containerArray)
            }
            findViewById<View>(R.id.continueButton) -> {
                currentView++
                if (currentView < 4) {
                    changeView(containerArray[currentView], containerArray)
                } else {
                    initContainer()
                }
            }
        }
    }

    private fun initContainer() {
        when (currentView) {
            0 -> {
                findViewById<View>(R.id.navigation).visibility = View.GONE
                tool()
            }
            1 -> {
                findViewById<View>(R.id.navigation).visibility = View.VISIBLE
                duration()
            }
            2 -> {
                findViewById<View>(R.id.navigation).visibility = View.VISIBLE
                setRepsAndSets()
            }
            3 -> {
                findViewById<View>(R.id.navigation).visibility = View.VISIBLE
                (findViewById<View>(R.id.continueButton) as Button).text = getString(R.string.finish)
                rating()
            }
            else -> {
                upload()
                backToMainActivity()
            }

        }
    }

    private fun changeView(view: View, containerArray: List<View>) {
        containerArray.forEach { if (it == view) it.visibility = View.VISIBLE else it.visibility = View.GONE }
        initContainer()
    }

    private fun upload() {
        Uploader().upLoadFitnessElement(databaseReference!!, firebaseUser!!.uid, fitnessElement)
    }

    private fun backToMainActivity() {
        startActivity(Intent(this@AddElementActivity, MainActivity::class.java))
    }

    private fun tool() {
        itemsList = findViewById(R.id.items)
        buildArrayAdapter()
        itemsList?.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            setTool((view as TextView).text as String)
            backAndForth(findViewById(R.id.continueButton))
        }
    }

    private fun duration() {
        val npMinutes = findViewById<NumberPicker>(R.id.np_minutes)
        val npHours = findViewById<NumberPicker>(R.id.np_hours)
        val npSeconds = findViewById<NumberPicker>(R.id.np_seconds)
        var hour = "00"
        var minute = "00"
        var second = "00"

        npHours.setOnValueChangedListener { _, _, newVal ->
            hour = String.format("%02d", newVal)
            setDuration(buildDurationString(hour, minute, second))
        }
        npMinutes.setOnValueChangedListener { _, oldVal, newVal ->
            minute = String.format("%02d", newVal)
            if (oldVal == 59 && newVal == 0)
                npHours.value = npHours.value.plus(1)
            setDuration(buildDurationString(hour, minute, second))
        }
        npSeconds.setOnValueChangedListener { _, oldVal, newVal ->
            second = String.format("%02d", newVal)
            if (oldVal == 59 && newVal == 0)
                npMinutes.value = npMinutes.value.plus(1)
            setDuration(buildDurationString(hour, minute, second))
        }
    }


    private fun buildDurationString(hour: String, minute: String, second: String): String {
        return "$hour:$minute:$second"
    }

    private fun setRepsAndSets() {
        viewArray = arrayOf(findViewById(R.id.reps1), findViewById(R.id.reps2), findViewById(R.id.reps3), findViewById(R.id.reps4), findViewById(R.id.reps5), findViewById(R.id.reps6), findViewById(R.id.reps7), findViewById(R.id.reps8), findViewById(R.id.reps9), findViewById(R.id.reps10))
        sets_np = findViewById(R.id.sets)
        scrollableListener()
        (findViewById<View>(R.id.sets) as NumberPicker).setOnValueChangedListener { _, oldVal, newVal ->
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

            setSets(newVal.toString())
            setReps(viewArray!!.filter { (it as NumberPicker).visibility == View.VISIBLE }.map { (it as NumberPicker).value })
        }
    }

    private fun rating() {
        findViewById<RatingBar>(R.id.rating).setOnRatingBarChangeListener { _, fl, _ -> setRating(fl.toDouble()) }
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

    private fun buildArrayAdapter() {
        databaseReference?.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        itemsList?.adapter = ArrayAdapter(
                                this@AddElementActivity,
                                android.R.layout.simple_list_item_1,
                                getFitnessTools(dataSnapshot))
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

    private fun getFitnessTools(dataSnapshot: DataSnapshot?): ArrayList<String> {
        val arrayList: ArrayList<String> = ArrayList(listOf<String>())
        dataSnapshot?.child("Fitness-Tools")?.children?.forEach { arrayList.add(it.key.toString() + " - " + it.value.toString()) }
        return arrayList
    }
}