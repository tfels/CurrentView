package de.felser_net.currentview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tf on 21.05.2016.
 * This service creates and manages the overlay window.
 */
public class OverlayWindowService extends Service implements View.OnTouchListener, PeriodicUiControl.DataListUiView {

    public static final String PREFERENCE_KEY_OVERLAY_X = "OverlayX";
    public static final String PREFERENCE_KEY_OVERLAY_Y = "OverlayY";

    private static OverlayWindowService instance = null;

    private SharedPreferences sharedPref = null;
    private WindowManager wm = null;
    private float lastMoveX;
    private float lastMoveY;
    private boolean lastMoveValuesValid = false;
    private GridLayout valueGrid = null;

    private PeriodicUiControl uiControl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TF", "service on Create");
        instance = this;

        uiControl = new PeriodicUiControl(this, getApplicationContext());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // get dimensions for calculating TextView height prior to rendering
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int deviceWidth = size.x;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        final Byte alpha = 0x60;
        final Byte red = 0x00;
        final Byte green = 0x00;
        final Byte blue = 0x00;
        final int color = (alpha << 24) + (red << 16) + (green << 8) + (blue << 0);

        // now let's create our valueGrid as TYPE_SYSTEM_ALERT
        valueGrid = uiControl.getBatteryData().createValueGrid(true, false);
        valueGrid.setColumnCount(1);
        valueGrid.setBackgroundColor(color);
        valueGrid.setOnTouchListener(this);
        valueGrid.setContentDescription("CurrentView Overlay Window");
        int overlayFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.x = sharedPref.getInt(PREFERENCE_KEY_OVERLAY_X, 0);
        params.y = sharedPref.getInt(PREFERENCE_KEY_OVERLAY_Y, 0);
        wm.addView(valueGrid, params);

        uiControl.Start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        uiControl.Stop();
        // save position
        if (valueGrid != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) valueGrid.getLayoutParams();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(PREFERENCE_KEY_OVERLAY_X, params.x);
            editor.putInt(PREFERENCE_KEY_OVERLAY_Y, params.y);
            editor.apply();
        }
        if(valueGrid != null)
            wm.removeView(valueGrid);

        valueGrid = null;
        sharedPref = null;
    }

    public static boolean isRunning() {
        return instance != null;
    }

    public void refreshUi(List<DataValue> values) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastMoveX = 0;
            lastMoveY = 0;
            lastMoveValuesValid = false;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // get values, calculate offset and save values
            float moveX = event.getRawX();
            float moveY = event.getRawY();

            float offsetX = (moveX - lastMoveX);
            float offsetY = (moveY - lastMoveY);

            lastMoveX = moveX;
            lastMoveY = moveY;

            if(!lastMoveValuesValid) {
                lastMoveValuesValid = true;
                return false;
            }

            // update the position
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) valueGrid.getLayoutParams();
            params.x += offsetX;
            params.y += offsetY;
            wm.updateViewLayout(valueGrid, params);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // return event as consumed if we moved something,
            // so that the click event is not triggered
            return lastMoveValuesValid;
        }
        return false;
    }
}
