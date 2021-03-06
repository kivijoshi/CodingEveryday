package com.example.jackytsang.presentationapi;

import android.annotation.TargetApi;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import java.lang.annotation.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private MyPresentation mPresentation;
    private MyDisplayListener mDisplayListener;

    @BindView(R.id.control_btn)
    Button controlBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            multiInit();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            multiDestroy();
        super.onDestroy();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void multiInit() {
        DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (dm == null)
            return;

        mDisplayListener = new MyDisplayListener();
        dm.registerDisplayListener(mDisplayListener, null);
        Display[] displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if (displays == null)
            return;

        for (Display display : displays) {
            mPresentation = new MyPresentation(this, display);
            mPresentation.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void multiDestroy() {
        DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (dm != null && mDisplayListener != null) {
            dm.unregisterDisplayListener(mDisplayListener);
            mDisplayListener = null;
        }

        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    @OnClick(R.id.control_btn)
    public void onControlBtnClick(View view) {
        if (mPresentation != null)
            mPresentation.setDynamicText("Changed");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private class MyDisplayListener implements DisplayManager.DisplayListener {

        @Override
        public void onDisplayAdded(int displayId) {
            DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
            if (dm == null)
                return;

            Display display = dm.getDisplay(displayId);
            if (display == null)
                return;

            mPresentation = new MyPresentation(MainActivity.this, display);
            mPresentation.show();
        }

        @Override
        public void onDisplayRemoved(int displayId) {
            if (mPresentation != null && mPresentation.getDisplay().getDisplayId() == displayId) {
                mPresentation.dismiss();
                mPresentation = null;
            }
        }

        @Override
        public void onDisplayChanged(int displayId) {

        }
    }
}
