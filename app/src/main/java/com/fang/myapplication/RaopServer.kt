package com.fang.myapplication

import android.net.Proxy.getPort
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.fang.myapplication.Util.changeSurfaceSize
import com.fang.myapplication.model.NALPacket
import com.fang.myapplication.model.PCMPacket
import com.fang.myapplication.player.AudioPlayer
import com.fang.myapplication.player.VideoPlayer
import java.util.logging.Handler

class RaopServer(
    surfaceView: SurfaceView,
    private val onVideoSizeChanged: (width: Int, height: Int) -> Unit
) : SurfaceHolder.Callback {
    private var mVideoPlayer: VideoPlayer? = null
    private val mAudioPlayer: AudioPlayer
    private var mServerId: Long = 0

    init {
        surfaceView.holder.addCallback(this)
        mAudioPlayer = AudioPlayer()
        mAudioPlayer.start()
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
        mVideoPlayer!!.addPacker(nalPacket)
    }

    @Suppress("unused")
    fun onRecvAudioData(pcm: ShortArray, pts: Long) {
        Log.d(TAG, "onRecvAudioData pcm length = " + pcm.size + ", pts = " + pts)
        val pcmPacket = PCMPacket()
        pcmPacket.data = pcm
        pcmPacket.pts = pts
        mAudioPlayer.addPacker(pcmPacket)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.i(TAG, "surfaceChanged: format: $format, width: $width, height: $height")
        if (mVideoPlayer == null) {
            mVideoPlayer = VideoPlayer(holder.surface)
            mVideoPlayer!!.start()
        }
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
