package com.fang.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import com.desaysv.module.airplay.SvAirplay.getDeviceName
import com.desaysv.module.airplay.SvAirplay.start
import com.desaysv.module.airplay.SvAirplay.stop

class MainActivity : Activity() {
    private var mIsStart = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSystemService(NSD_SERVICE)
        val mTxtDevice = findViewById<TextView>(R.id.txt_device)
        findViewById<View>(R.id.btn_control).setOnClickListener { v: View ->
            if (!mIsStart) {
                start(surfaceView = findViewById(R.id.surface))
                mTxtDevice.text = "Device name:" + getDeviceName()
            } else {
                stop()
                mTxtDevice.text = "have not started"
            }
            mIsStart = !mIsStart
            (v as TextView).text = if (mIsStart) "End" else "Start"
        }
        val surfaceView = findViewById<SurfaceView>(R.id.surface)
        start(surfaceView)
    }
}
