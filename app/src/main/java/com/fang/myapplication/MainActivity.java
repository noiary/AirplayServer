package com.fang.myapplication;

import android.app.Activity;
import android.content.Context;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends Activity {

    public static String TAG = "AIS-RAOP-Main";

    private AirPlayServer mAirPlayServer;
    private RaopServer mRaopServer;
    private DNSNotify mDNSNotify;

    private SurfaceView mSurfaceView;
    private TextView mTxtDevice;
    private boolean mIsStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSystemService(Context.NSD_SERVICE);
        findViewById(R.id.btn_control).setOnClickListener(v -> {
            if (!mIsStart) {
                startServer();
                mTxtDevice.setText("Device name:" + mDNSNotify.getDeviceName());
            } else {
                stopServer();
                mTxtDevice.setText("have not started");
            }
            mIsStart = !mIsStart;
            ((TextView) v).setText(mIsStart ? "End" : "Start");
        });
        mTxtDevice = findViewById(R.id.txt_device);
        mSurfaceView = findViewById(R.id.surface);
        mAirPlayServer = new AirPlayServer();
        mRaopServer = new RaopServer(mSurfaceView, (width, height) -> {
            runOnUiThread(() -> Util.INSTANCE.changeSurfaceSize(mSurfaceView, width, height));
            return Unit.INSTANCE;
        });
        mDNSNotify = new DNSNotify();

        MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        MediaCodecInfo[] mediaCodecInfos = mediaCodecList.getCodecInfos();
        for (int i = 0; i < mediaCodecInfos.length; i++) {
            if (mediaCodecInfos[i].isEncoder()) {
                continue;
            }
            Log.d(TAG, "codec= " + mediaCodecInfos[i].getName() +
                    "\nis_encoder=" + mediaCodecInfos[i].isEncoder() +
                    "\nis_vendor=" + mediaCodecInfos[i].isVendor() +
                    "\nhw_acc=" + mediaCodecInfos[i].isHardwareAccelerated() +
                    "\nsw_acc=" + mediaCodecInfos[i].isSoftwareOnly());
            String[] types = mediaCodecInfos[i].getSupportedTypes();
            Log.d(TAG, "supported codec = " + String.join(", ", types));

//			mediaCodecInfos[i].VideoCapabilities.getSupportedPerformancePoints();
            // for (int j = 0; j < types.length; j++) {
            // Log.d(TAG, "supported codec = " + types[j]);
            // }
        }
    }

    private void startServer() {
        mDNSNotify.changeDeviceName();
        mAirPlayServer.startServer();
        int airplayPort = mAirPlayServer.getPort();
        if (airplayPort == 0) {
            Toast.makeText(this.getApplicationContext(), "Start the AirPlay service failed", Toast.LENGTH_SHORT).show();
        } else {
            mDNSNotify.registerAirplay(airplayPort);
        }
        mRaopServer.startServer();
        int raopPort = mRaopServer.getPort();
        if (raopPort == 0) {
            Toast.makeText(this.getApplicationContext(), "Start the RAOP service failed", Toast.LENGTH_SHORT).show();
        } else {
            mDNSNotify.registerRaop(raopPort);
        }
        Log.d(TAG, "airplayPort = " + airplayPort + ", raopPort = " + raopPort);
    }

    private void stopServer() {
        mDNSNotify.stop();
        mAirPlayServer.stopServer();
        mRaopServer.stopServer();
    }

}
