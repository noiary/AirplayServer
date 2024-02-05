package com.desaysv.module.airplay

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast

/**
 * Created by Max on Jan 31, 2024
 */
object Util {
    const val TAG = "Util"
    private val SurfaceView.screenWidth get() = resources.displayMetrics.widthPixels
    private val SurfaceView.screenHeight get() = resources.displayMetrics.heightPixels
    private val handler by lazy { Handler(Looper.getMainLooper()) }


    /**
     * Changes the size of the surface.
     *
     * @param width The new width of the surface.
     * @param height The new height of the surface.
     */
    fun changeSurfaceSize(surfaceView: SurfaceView, width: Int, height: Int) {
        if (width > 0f && height > 0f) {
            handler.post {
                Log.i(TAG, "changeSurfaceSize: width: $width, height: $height")
                val widthRatio = width.toFloat() / surfaceView.screenWidth.toFloat()
                val heightRatio = height.toFloat() / surfaceView.screenHeight.toFloat()
                val ratio = widthRatio.coerceAtLeast(heightRatio)
                val finalWidth = (width / ratio).toInt()
                val finalHeight = (height / ratio).toInt()
                surfaceView.layoutParams = surfaceView.layoutParams.apply {
                    this.width = finalWidth
                    this.height = finalHeight
                }
            }
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}