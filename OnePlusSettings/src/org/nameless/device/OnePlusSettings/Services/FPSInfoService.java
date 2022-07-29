/*
 * Copyright (C) 2019 The OmniROM Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.dreams.DreamService;
import android.service.dreams.IDreamManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.util.Log;
import android.util.TypedValue;

import org.nameless.device.OnePlusSettings.Utils.FileUtils;
import org.nameless.device.OnePlusSettings.Utils.FpsUtils;

public class FPSInfoService extends Service {

    private static final boolean DEBUG = false;
    private static final String TAG = "FPSInfoService";
    private static final String MEASURED_FPS = "/sys/devices/platform/soc/ae00000.qcom,mdss_mdp/drm/card0/sde-crtc-0/measured_fps";

    public static final String INTENT_UPDATE_SETTINGS = "org.nameless.device.OnePlusSettings.UPDATE_FPS_SETTINGS";

    private static final int POSITION_TOP_LEFT = 0;
    private static final int POSITION_TOP_CENTER = 1;
    private static final int POSITION_TOP_RIGHT = 2;
    private static final int POSITION_BOTTOM_LEFT = 3;
    private static final int POSITION_BOTTOM_CENTER = 4;
    private static final int POSITION_BOTTOM_RIGHT = 5;

    private static final int[] colorArray = {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.BLACK};
    private static final int[] sizeArray = {16, 18, 20, 22, 24};

    private FPSView mView;
    private Thread mCurFPSThread;

    private IDreamManager mDreamManager;
    private WindowManager mWindowManager;

    private String mFps;
    private int mColor;
    private int mPosition;
    private int mSize;

    private class FPSView extends View {
        private Paint mOnlinePaint;
        private float mAscent;
        private int mFH;
        private int mMaxWidth;

        private int mNeededWidth;
        private int mNeededHeight;

        private boolean mDataAvail;

        private Handler mCurFPSHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj == null || msg.what != 1) {
                    return;
                }

                mFps = parseMeasuredFps((String) msg.obj);
                mDataAvail = true;
                updateDisplay();
            }
        };

        FPSView(Context c) {
            super(c);
            float density = c.getResources().getDisplayMetrics().density;
            int paddingPx = Math.round(10 * density);
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            setBackgroundColor(Color.argb(0x0, 0, 0, 0));

            Typeface typeface = Typeface.create("google-sans", Typeface.BOLD);

            mOnlinePaint = new Paint();
            mOnlinePaint.setTypeface(typeface);
            mOnlinePaint.setAntiAlias(true);
            mOnlinePaint.setShadowLayer(5.0f, 0.0f, 0.0f, Color.BLACK);

            updateColorAndSize(c, density);

            mAscent = mOnlinePaint.ascent();
            float descent = mOnlinePaint.descent();
            mFH = (int) (descent - mAscent + .5f);

            final String maxWidthStr = "FPS: XYZ";
            mMaxWidth = (int) mOnlinePaint.measureText(maxWidthStr);

            updateDisplay();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            mCurFPSHandler.removeMessages(1);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(resolveSize(mNeededWidth, widthMeasureSpec),
                    resolveSize(mNeededHeight, heightMeasureSpec));
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!mDataAvail) {
                return;
            }

            final int W = mNeededWidth;
            final int LEFT = getWidth() - 1;

            int x = LEFT - mPaddingLeft;
            int top = mPaddingTop + 2;

            int y = mPaddingTop - (int) mAscent;

            canvas.drawText(mFps, x - mMaxWidth,
                    y - 1, mOnlinePaint);
            y += mFH;
        }

        private String parseMeasuredFps(String data) {
            String result = "err";
            try {
                float fps = Float.parseFloat(data.trim().split("\\s+")[1]);
                result = String.valueOf(Math.round(fps));
            } catch (NumberFormatException e) {
                if (DEBUG) Log.e(TAG, "NumberFormatException occured at parsing FPS data");
            }
            return "FPS: " + result;
        }

        private int getAccentColor(Context context) {
            TypedValue typedValue = new TypedValue();
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context,
                    android.R.style.Theme_DeviceDefault);
            contextThemeWrapper.getTheme().resolveAttribute(android.R.attr.colorAccent,
                    typedValue, true);
            return typedValue.data;
        }

        void updateDisplay() {
            if (!mDataAvail) {
                return;
            }

            int neededWidth = mPaddingLeft + mPaddingRight + mMaxWidth + 40;
            int neededHeight = mPaddingTop + mPaddingBottom + 70;  //In case incomplete display on largest display size.
            if (neededWidth != mNeededWidth || neededHeight != mNeededHeight) {
                mNeededWidth = neededWidth;
                mNeededHeight = neededHeight;
                requestLayout();
            } else {
                invalidate();
            }
        }

        public Handler getHandler(){
            return mCurFPSHandler;
        }

        public void updateColorAndSize(Context context) {
            updateColorAndSize(context, context.getResources().getDisplayMetrics().density);
        }

        public void updateColorAndSize(Context context, float density) {
            mSize = sizeArray[FpsUtils.getSizeIndex(context)];
            final int textSize = Math.round(mSize * density);

            final int colorIndex = FpsUtils.getColorIndex(context);
            if (colorIndex < colorArray.length) {
                mColor = colorArray[colorIndex];
            } else {
                mColor = getAccentColor(context);
            }

            mOnlinePaint.setTextSize(textSize);
            mOnlinePaint.setColor(mColor);
        }
    }

    protected class CurFPSThread extends Thread {
        private boolean mInterrupt = false;
        private Handler mHandler;

        public CurFPSThread(Handler handler){
            mHandler = handler;
        }

        public void interrupt() {
            mInterrupt = true;
        }

        @Override
        public void run() {
            try {
                while (!mInterrupt) {
                    sleep(1000);
                    String fpsVal = FileUtils.readOneLine(MEASURED_FPS);
                    mHandler.sendMessage(mHandler.obtainMessage(1, fpsVal));
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mView = new FPSView(this);
        WindowManager.LayoutParams params = getFpsViewParams();

        startThread();

        mDreamManager = IDreamManager.Stub.asInterface(
                ServiceManager.checkService(DreamService.DREAM_SERVICE));
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(INTENT_UPDATE_SETTINGS);
        registerReceiver(mScreenStateReceiver, screenStateFilter);

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
        mWindowManager.removeView(mView);
        mView = null;
        unregisterReceiver(mScreenStateReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (DEBUG) Log.d(TAG, "ACTION_SCREEN_ON " + isDozeMode());
                if (!isDozeMode()) {
                    startThread();
                    mView.setVisibility(View.VISIBLE);
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (DEBUG) Log.d(TAG, "ACTION_SCREEN_OFF");
                mView.setVisibility(View.GONE);
                stopThread();
            } else if (intent.getAction().equals(INTENT_UPDATE_SETTINGS)) {
                updateSettings(context);
            }
        }
    };

    private boolean isDozeMode() {
        try {
            if (mDreamManager != null && mDreamManager.isDreaming()) {
                return true;
            }
        } catch (RemoteException e) {
            return false;
        }
        return false;
    }

    private void startThread() {
        if (DEBUG) Log.d(TAG, "started CurFPSThread");
        mCurFPSThread = new CurFPSThread(mView.getHandler());
        mCurFPSThread.start();
    }

    private void stopThread() {
        if (mCurFPSThread != null && mCurFPSThread.isAlive()) {
            if (DEBUG) Log.d(TAG, "stopping CurFPSThread");
            mCurFPSThread.interrupt();
            try {
                mCurFPSThread.join();
            } catch (InterruptedException e) {
            }
        }
        mCurFPSThread = null;
    }

    private int getGravity() {
        mPosition = FpsUtils.getPosition(this);
        int gravity;
        switch (mPosition) {
            default:
            case POSITION_TOP_LEFT:
                gravity = Gravity.LEFT | Gravity.TOP;
                break;
            case POSITION_TOP_CENTER:
                gravity = Gravity.CENTER | Gravity.TOP;
                break;
            case POSITION_TOP_RIGHT:
                gravity = Gravity.RIGHT | Gravity.TOP;
                break;
            case POSITION_BOTTOM_LEFT:
                gravity = Gravity.LEFT | Gravity.BOTTOM;
                break;
            case POSITION_BOTTOM_CENTER:
                gravity = Gravity.CENTER | Gravity.BOTTOM;
                break;
            case POSITION_BOTTOM_RIGHT:
                gravity = Gravity.RIGHT | Gravity.BOTTOM;
                break;
        }
        return gravity;
    }

    private WindowManager.LayoutParams getFpsViewParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT);
        params.gravity = getGravity();
        params.setTitle("FPS Info");
        return params;
    }

    private void updateSettings(Context context) {
        WindowManager.LayoutParams params = getFpsViewParams();
        mWindowManager.updateViewLayout(mView, params);
        mView.updateColorAndSize(context);
    }
}
