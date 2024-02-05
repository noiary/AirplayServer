package com.desaysv.module.airplay

import android.util.Log
import android.view.SurfaceView
import com.desaysv.module.airplay.Util.changeSurfaceSize

/**
 * Created by Max on Feb 01, 2024
 */
object SvAirplay {
    private const val TAG = "SvAirplay"
    private val airPlayServer: AirPlayServer by lazy { AirPlayServer() }
    private lateinit var raopServer: RaopServer
    private val dnsNotify: DNSNotify by lazy { DNSNotify() }
    fun start(surfaceView: SurfaceView) {
        Log.i(TAG, "start")
        raopServer = RaopServer(surfaceView) { width, height ->
            // 横竖屏切换时，需要重新设置surfaceView的大小
            changeSurfaceSize(surfaceView, width, height)
        }
        dnsNotify.changeDeviceName()
        airPlayServer.startServer()
        val airplayPort: Int = airPlayServer.port
        if (airplayPort == 0) {
            Util.showToast(surfaceView.context, "Start the AirPlay service failed")
        } else {
            dnsNotify.registerAirplay(airplayPort)
        }
        raopServer.startServer()
        val raopPort: Int = raopServer.port
        if (raopPort == 0) {
            Util.showToast(surfaceView.context, "Start the RAOP service failed")
        } else {
            dnsNotify.registerRaop(raopPort)
        }
        Log.d(TAG, "airplayPort = $airplayPort, raopPort = $raopPort")
    }

    fun stop() {
        Log.i(TAG, "stop")
        dnsNotify.stop()
        airPlayServer.stopServer()
        raopServer.stopServer()
    }

    fun getDeviceName(): String? = dnsNotify.deviceName
}