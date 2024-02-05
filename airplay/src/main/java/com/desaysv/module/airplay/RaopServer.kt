package com.desaysv.module.airplay

import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.desaysv.module.airplay.model.NALPacket
import com.desaysv.module.airplay.model.PCMPacket
import com.desaysv.module.airplay.player.AudioPlayer
import com.desaysv.module.airplay.player.VideoPlayer

class RaopServer(
    private val surfaceView: SurfaceView,
    private val onVideoSizeChanged: (width: Int, height: Int) -> Unit
) : SurfaceHolder.Callback {
    private var mVideoPlayer = VideoPlayer(surfaceView.holder.surface)
    private val mAudioPlayer = AudioPlayer()
    private var mServerId: Long = 0

    init {
        surfaceView.holder.addCallback(this)
        mAudioPlayer.start()
        mVideoPlayer.start()
    }

    @Suppress("LongParameterList", "unused")
    fun onRecvVideoData(
        nal: ByteArray,
        nalType: Int,
        dts: Long,
        pts: Long,
        width: Float,
        height: Float
    ) {
        val nalPacket = NALPacket()
        nalPacket.nalData = nal
        nalPacket.nalType = nalType
        nalPacket.pts = pts
        nalPacket.dts = dts
        onVideoSizeChanged(width.toInt(), height.toInt())
        mVideoPlayer.addPacker(nalPacket)
    }

    @Suppress("unused")
    fun onRecvAudioData(pcm: ShortArray, pts: Long) {
        Log.d(TAG, "onRecvAudioData pcm length = " + pcm.size + ", pts = " + pts)
        val pcmPacket = PCMPacket()
        pcmPacket.data = pcm
        pcmPacket.pts = pts
        mAudioPlayer.addPacker(pcmPacket)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.i(TAG, "surfaceCreated")
//        mVideoPlayer.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.i(TAG, "surfaceChanged: format: $format, width: $width, height: $height")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
    fun startServer() {
        if (mServerId == 0L) {
            mServerId = start()
        }
    }

    fun stopServer() {
        if (mServerId != 0L) {
            stop(mServerId)
        }
        mServerId = 0
    }

    val port: Int
        get() = if (mServerId != 0L) {
            getPort(mServerId)
        } else 0

    private external fun start(): Long
    private external fun stop(serverId: Long)
    private external fun getPort(serverId: Long): Int

    companion object {
        init {
            System.loadLibrary("raop_server")
            System.loadLibrary("play-lib")
        }

        private const val TAG = "AIS-RaopServer"
    }
}
