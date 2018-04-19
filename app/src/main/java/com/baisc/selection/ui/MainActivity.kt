package com.baisc.selection.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.baisc.selection.R

/**
 * Created by basic on 2018/4/18.
 */
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        var gridItem = findViewById<TextView>(R.id.grid)
        gridItem.setOnClickListener {
//            var gridIntent = Intent(activity, SelectionGridActivity()::class.java)
            var gridIntent = Intent(this@MainActivity, SelectionGridActivity().javaClass)
            startActivity(gridIntent)
        }
    }
}