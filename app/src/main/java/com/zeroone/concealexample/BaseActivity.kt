package com.zeroone.concealexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import com.zeroone.conceal.SharedChamber
import com.zeroone.conceal.listener.OnDataChamberChangeListener
import com.zeroone.conceal.model.ChamberType

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
                .enableKeyPrefix(true, "walaoweh")
                .setPassword("Password@123")
                .setPrefListener(this)
                .buildChamber()
    }

    override fun onDataChange(key: String, value: String) {
        Log.d("DATACHANGE", key + " :: " + value)
    }
}
