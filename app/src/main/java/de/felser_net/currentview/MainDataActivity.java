package de.felser_net.currentview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MainDataActivity extends AppCompatActivity implements PeriodicUiControl.DataListUiView {

    private TextView txtDebug = null;

    private Button btnOverlayStartStop = null;
    private boolean overlayRunning = false;

    private PeriodicUiControl uiControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiControl = new PeriodicUiControl(this, getApplicationContext());

        setContentView(R.layout.activity_main_data);

        txtDebug = (TextView)findViewById(R.id.textValueDebug);
        btnOverlayStartStop = (Button)findViewById(R.id.buttonOverlayStartStopButton);

        RelativeLayout relativeLayout =  (RelativeLayout)findViewById(R.id.mainLayout);
        View recentView = txtDebug;

        int id = (int)System.currentTimeMillis();
        for(DataValue val : uiControl.getBatteryData().getValues()) {

            // create textView for the name
            TextView txtName = new TextView(this);
            txtName.setText(val.displayName());
            txtName.setId(++id);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW, recentView.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            relativeLayout.addView(txtName, layoutParams);

            // create textView for the value
            TextView txtVal = new TextView(this);
            txtVal.setText(val.valueText());
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW, recentView.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            relativeLayout.addView(txtVal, layoutParams);

            //viewElements.add(txtV);
            recentView = txtName;
            val.setTextView(txtVal);
        }



        // setup overlay start/stop button
        btnOverlayStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMyServiceIntent = new Intent(getApplicationContext(), OverlayWindowService.class);
                if(!overlayRunning) {
                    if(!testOverlayPermission())
                        return;
                    startService(startMyServiceIntent);
                } else {
                    stopService(startMyServiceIntent);
                }
                overlayRunning = !overlayRunning;
                btnOverlayStartStop.setText(getResources().getString(overlayRunning ? R.string.txtStop : R.string.txtStart));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiControl.Start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        uiControl.Stop();
    }

    public boolean testOverlayPermission() {
        // SYSTEM_ALERT_WINDOW is a special permission that cannot be handled via the
        // compatibility library (ActivityCompat.requestPermissions).
        // Therefore we have to check the version.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if (Settings.canDrawOverlays(this)) // SDK >= 23 only !!
            return true;

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivity(intent);
        return false;
    }

    private int counter = 0;
    public void refreshUi(List<DataValue> values)
    {
        txtDebug.setText(""+(counter++));
    }

}


