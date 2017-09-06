package com.dokufitness.finnvonholten.doku_fitness

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddElementActivity : AppCompatActivity(),
        View.OnClickListener,
        Tools.OnItemClickListener,
        Duration.OnDurationChangeListener,
        RepsAndSets.OnSetsChangedListener,
        RepsAndSets.OnRepsChangedListener,
        Rating.OnRatingChangeListener {

    private var fitnessElement: FitnessElement = FitnessElement(timestamp = null)
    private var tools: Tools? = null
    private var currentView: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_element)

        (findViewById<View>(R.id.adView) as AdView).loadAd(AdRequest.Builder()
                .addTestDevice("D0B7DE1E149C08D3A1BF36DD1549A38C")
                .build())

        backAndForth(findViewById(R.id.continueButton))

        findViewById<View>(R.id.select_tool_view).visibility = View.VISIBLE

        findViewById<View>(R.id.continueButton).setOnClickListener(this@AddElementActivity)
        findViewById<View>(R.id.backButton).setOnClickListener(this@AddElementActivity)

        //INIT-STUFF
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
                tools = Tools(findViewById(R.id.items), FirebaseDatabase.getInstance().reference, this@AddElementActivity)
                tools?.setOnEditClickedListener(this)
            }
            1 -> {
                findViewById<View>(R.id.navigation).visibility = View.VISIBLE
                Duration(findViewById(R.id.np_seconds), findViewById(R.id.np_minutes), findViewById(R.id.np_hours)).setOnDurationChangeListener(this)
            }
            2 -> {
                findViewById<View>(R.id.navigation).visibility = View.VISIBLE
                val repsAndSets = RepsAndSets(
                        listOf(findViewById(R.id.reps1),
                                findViewById(R.id.reps2),
                                findViewById(R.id.reps3),
                                findViewById(R.id.reps4),
                                findViewById(R.id.reps5),
                                findViewById(R.id.reps6),
                                findViewById(R.id.reps7),
                                findViewById(R.id.reps8),
                                findViewById(R.id.reps9),
                                findViewById(R.id.reps10)),
                        findViewById(R.id.reps1),
                        findViewById(R.id.reps2),
                        findViewById(R.id.reps3),
                        findViewById(R.id.reps4),
                        findViewById(R.id.reps5),
                        findViewById(R.id.reps6),
                        findViewById(R.id.reps7),
                        findViewById(R.id.reps8),
                        findViewById(R.id.reps9),
                        findViewById(R.id.reps10),
                        findViewById(R.id.sets),
                        this,
                        findViewById(R.id.scrollView),
                        findViewById(R.id.scrollBarHelp))
                repsAndSets.setOnRepsChangeListener(this)
                repsAndSets.setOnSetsChangeListener(this)
            }
            3 -> {
                findViewById<View>(R.id.navigation).visibility = View.VISIBLE
                (findViewById<View>(R.id.continueButton) as Button).text = getString(R.string.finish)
                Rating(findViewById(R.id.rating)).setOnRatingChangeListener(this)
            }
            else -> {
                Uploader().upLoadFitnessElement(FirebaseDatabase.getInstance().reference, FirebaseAuth.getInstance().currentUser!!.uid, fitnessElement)
                backToMainActivity()
            }
        }
    }

    private fun changeView(view: View, containerArray: List<View>) {
        containerArray.forEach { if (it == view) it.visibility = View.VISIBLE else it.visibility = View.GONE }
        initContainer()
    }

    private fun backToMainActivity() {
        startActivity(Intent(this@AddElementActivity, MainActivity::class.java))
    }

    override fun ItemClicked(tool: String) {
        fitnessElement.tool = tool.substring(0, 3)
        backAndForth(findViewById(R.id.continueButton))
    }

    override fun DurationChanged(string: String) {
        fitnessElement.duration = string
    }

    override fun SetsChanged(sets: String) {
        fitnessElement.sets = sets
    }

    override fun RepsChanged(reps: List<Int>) {
        fitnessElement.reps = reps
    }

    override fun RatingChanged(rating: Double) {
        fitnessElement.rating = rating
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        (menu.findItem(R.id.action_search).actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                tools?.filterAdapter(newText)
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
}