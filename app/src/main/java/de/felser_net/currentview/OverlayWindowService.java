package de.felser_net.currentview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tf on 21.05.2016.
 * This service creates and manages the overlay window.
 */
public class OverlayWindowService extends Service implements View.OnTouchListener, View.OnClickListener, PeriodicUiControl.DataListUiView {

    private SharedPreferences sharedPref = null;
    private WindowManager wm = null;
    private float lastMoveX;
    private float lastMoveY;
    private boolean lastMoveValuesValid = false;
    private List<TextView> viewElements = null;

    private PeriodicUiControl uiControl;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TF", "service on Create");

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

        // now let's create our TextViews
        int yPos =  sharedPref.getInt(getString(R.string.saved_overlay_y), 0);
        viewElements = new ArrayList<TextView>();

        for(int i=0; i<4; i++) {
            TextView txtV = new TextView(this);
            txtV.setText("Text "+i);
            txtV.setBackgroundColor(color);
            txtV.setOnTouchListener(this);
            txtV.setOnClickListener(this);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.START | Gravity.TOP;
            params.x = sharedPref.getInt(getString(R.string.saved_overlay_x), 0);
            params.y = yPos;
            wm.addView(txtV, params);

            txtV.measure(widthMeasureSpec, heightMeasureSpec);
            yPos += txtV.getMeasuredHeight();

            viewElements.add(txtV);
        }

        uiControl.Start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiControl.Stop();
        // save position
        if (viewElements != null && !viewElements.isEmpty()) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) viewElements.get(0).getLayoutParams();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.saved_overlay_x), params.x);
            editor.putInt(getString(R.string.saved_overlay_y), params.y);
            editor.apply();
        }
        if(viewElements != null)
            for (View v : viewElements)
                wm.removeView(v);
        viewElements = null;
        sharedPref = null;
    }

    public void refreshUi(BatteryData batData) {
        if(viewElements == null)
            return;
        Iterator<TextView> it = viewElements.iterator();
        if(it.hasNext()) it.next().setText(batData.getCurrentText());
        if(it.hasNext()) it.next().setText(batData.getCurrentAvgText());
        if(it.hasNext()) it.next().setText(batData.getVoltageText());
        if(it.hasNext()) it.next().setText(batData.getTemperatureText());
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
            for (View velm : viewElements) {
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) velm.getLayoutParams();
                params.x += offsetX;
                params.y += offsetY;
                wm.updateViewLayout(velm, params);
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // return event as consumed if we moved something,
            // so that the click event is not triggered
            return lastMoveValuesValid;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Overlay click on " + ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
    }
}
