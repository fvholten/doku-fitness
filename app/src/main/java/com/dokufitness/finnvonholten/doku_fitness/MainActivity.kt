package com.dokufitness.finnvonholten.doku_fitness

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.yavski.fabspeeddial.FabSpeedDial
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter

class MainActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser

    private var recyclerView: RecyclerView? = null

    private var dataSnapshot: DataSnapshot? = null

    private var fitnessElementList: MutableList<FitnessElement>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (findViewById<View>(R.id.adView) as AdView).loadAd(AdRequest.Builder()
                .addTestDevice("D0B7DE1E149C08D3A1BF36DD1549A38C")
                .build())

        createRecyclerView()
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createRecyclerView() {
        recyclerView = findViewById(R.id.feed)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.itemAnimator = DefaultItemAnimator()
    }

    private fun getData() {
        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                getFitnessActivities(dataSnapshot)
                buildFabButton()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Ups... something went wrong. Try again!", Toast.LENGTH_LONG).show()
                Log.d(this@MainActivity.packageName, "getDataFromDatabase: " + databaseError.message)
            }
        })
    }

    private fun buildFabButton() {
        (findViewById<View>(R.id.fab_add) as FabSpeedDial).setMenuListener(object : SimpleMenuListenerAdapter() {
            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                if (menuItem!!.itemId == R.id.action_session)
                    Toast.makeText(this@MainActivity, "action_session", Toast.LENGTH_SHORT).show()
                else if (menuItem.itemId == R.id.action_exercise)
                    startActivity(Intent(this@MainActivity, AddElementActivity::class.java))
                return super.onMenuItemSelected(menuItem)
            }
        })
    }

    private fun setValues(fitnessElement: FitnessElement, content: DataSnapshot) {
        when {
            dataSnapShotExists(content, "Sets") -> fitnessElement.sets = content.value.toString()
            dataSnapShotExists(content, "Duration") -> fitnessElement.duration = content.value.toString()
            dataSnapShotExists(content, "Rating") -> fitnessElement.rating = java.lang.Double.parseDouble(content.value.toString())
            dataSnapShotExists(content, "Fitness-Tool") -> dataSnapshot?.child("Fitness-Tools")?.children?.forEach {
                if (dataSnapShotExists(it, content.value.toString()))
                    fitnessElement.tool = it.value.toString()
            }
            dataSnapShotExists(content, "Reps") -> {
                val reps = ArrayList<Int>()
                content.children.forEach { reps.add(Integer.parseInt(it.value.toString())) }
                fitnessElement.reps = reps
            }
        }
    }

    private fun dataSnapShotExists(content: DataSnapshot, element: String): Boolean {
        return content.key == element && content.value != null
    }

    private fun getFitnessActivities(dataSnapshot: DataSnapshot) {
        fitnessElementList = ArrayList()
        this.dataSnapshot = dataSnapshot
        for (userSnap in dataSnapshot.child("Users").children)
            if (userSnap.key == user!!.uid)
                for (timestampSnap in userSnap.children) {
                    val fitnessElement = FitnessElement()
                    for (content in timestampSnap.children)
                        setValues(fitnessElement, content)
                    fitnessElementList?.add(fitnessElement)
                }
        findViewById<View>(R.id.dataLoad_progress).visibility = View.GONE

        recyclerView!!.adapter = FitnessAdapter(fitnessElementList as ArrayList<FitnessElement>, this)
    }
}
