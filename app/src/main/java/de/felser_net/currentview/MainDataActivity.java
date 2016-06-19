package de.felser_net.currentview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
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

        // creating the values in the grid layout
        GridLayout gridLayout = (GridLayout) findViewById(R.id.valueGrid);
        int rowCount = 0;
        GridLayout.Spec columnSpec0 = GridLayout.spec(0, 1.0f);
        GridLayout.Spec columnSpec1 = GridLayout.spec(1, GridLayout.END);

        for(DataValue val : uiControl.getBatteryData().getValues()) {
            GridLayout.Spec rowSpec = GridLayout.spec(rowCount);
            GridLayout.LayoutParams layoutParams;

            // create textView for the name
            TextView txtName = new TextView(this);
            txtName.setText(val.displayName());
            layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec0);
            gridLayout.addView(txtName, layoutParams);

            // create textView for the value
            TextView txtVal = new TextView(this);
            txtVal.setText(val.valueText());
            layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec1);
            gridLayout.addView(txtVal, layoutParams);

            //viewElements.add(txtV);
            val.setTextView(txtVal);

            rowCount++;
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


