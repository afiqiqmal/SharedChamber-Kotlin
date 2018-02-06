package com.chamber.kotlin.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.chamber.kotlin.library.SharedChamber
import com.zeroone.conceal.listener.OnDataChamberChangeListener
import com.chamber.kotlin.library.model.ChamberType

/**
 * @author : hafiq on 27/03/2017.
 */

open class BaseActivity : AppCompatActivity(), OnDataChamberChangeListener {

    lateinit var chamber: SharedChamber

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chamber = SharedChamber.ChamberBuilder(this)
                .setChamberType(ChamberType.KEY_256)
                .enableCrypto(false, false)
                .enableKeyPrefix(false, "walaoweh")
                .setPassword("Password@123")
                .setPrefListener(this)
                .buildChamber()
    }

    override fun onDataChange(key: String, value: String) {
        Log.d("DATACHANGE", key + " :: " + value)
    }
}
