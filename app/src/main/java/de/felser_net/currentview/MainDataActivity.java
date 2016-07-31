package de.felser_net.currentview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
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
        //uiControl = new PeriodicUiControl(this, getApplicationContext());
        uiControl = new PeriodicUiControl(this, this);

        setContentView(R.layout.activity_main_data);

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        txtDebug = (TextView)findViewById(R.id.textValueDebug);
        btnOverlayStartStop = (Button)findViewById(R.id.buttonOverlayStartStopButton);

        // creating the values in the grid layout
        GridLayout valueGrid = uiControl.getBatteryData().createValueGrid(true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);

        mainLayout.addView(valueGrid, params);


        // setup overlay start/stop button
        overlayRunning = OverlayWindowService.isRunning();
        btnOverlayStartStop.setText(getResources().getString(overlayRunning ? R.string.txtStop : R.string.txtStart));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuaction_settings:
                Intent startIntent = new Intent(this, SettingsActivity.class);
                startIntent.putParcelableArrayListExtra(SettingsActivity.START_EXTRA_VALUES, uiControl.getBatteryData().getValues());
                startActivity(startIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean testOverlayPermission() {
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


