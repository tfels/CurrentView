package de.felser_net.currentview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by tf on 21.05.2016.
 * This service creates and manages the overlay window.
 */
public class OverlayWindowService extends Service implements View.OnTouchListener, View.OnClickListener {


    private WindowManager wm = null;
    private Button overlayedButton = null;
    private float lastMoveX;
    private float lastMoveY;
    private boolean lastMoveValuesValid = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TF", "service on Create");

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        overlayedButton = new Button(this);
        overlayedButton.setText("Overlay button");
        overlayedButton.setOnTouchListener(this);
        overlayedButton.setAlpha(0.9f);
        overlayedButton.setBackgroundColor(0x55fe4444);
        overlayedButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        wm.addView(overlayedButton, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayedButton != null) {
            wm.removeView(overlayedButton);
            overlayedButton = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastMoveX = 0;
            lastMoveY = 0;
            lastMoveValuesValid = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

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
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayedButton.getLayoutParams();
            params.x += offsetX;
            params.y += offsetY;
            wm.updateViewLayout(overlayedButton, params);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            return lastMoveValuesValid;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Overlay onClick called", Toast.LENGTH_SHORT).show();
    }
}
