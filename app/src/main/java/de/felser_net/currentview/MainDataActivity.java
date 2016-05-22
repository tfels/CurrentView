package de.felser_net.currentview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainDataActivity extends AppCompatActivity {

    private TextView txtValueStatus = null;
    private TextView txtValueHealth = null;
    private TextView txtValuePresent = null;
    private TextView txtValueLevel = null;
    private TextView txtValueScale = null;
    private TextView txtValuePlugged = null;
    private TextView txtValueVoltage = null;
    private TextView txtValueTemperature = null;
    private TextView txtValueTechnology = null;
    private TextView txtValueCurrent = null;
    private TextView txtValueCurrentAvg = null;
    private TextView txtDebug = null;
    private ImageView imgIcon = null;
    private Button btnOverlayStartStop = null;
    private boolean overlayRunning = false;

    private BatteryData batData;
    Handler updateHandler;
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_data);

        txtValueStatus = (TextView)findViewById(R.id.textValueStatus);
        txtValueHealth = (TextView)findViewById(R.id.textValueHealth);
        txtValuePresent = (TextView)findViewById(R.id.textValuePresent);
        txtValueLevel = (TextView)findViewById(R.id.textValueLevel);
        txtValueScale = (TextView)findViewById(R.id.textValueScale);
        txtValuePlugged = (TextView)findViewById(R.id.textValuePlugged);
        txtValueVoltage = (TextView)findViewById(R.id.textValueVoltage);
        txtValueTemperature = (TextView)findViewById(R.id.textValueTemperature);
        txtValueTechnology = (TextView)findViewById(R.id.textValueTechnology);
        txtValueCurrent = (TextView)findViewById(R.id.textValueCurrent);
        txtValueCurrentAvg = (TextView)findViewById(R.id.textValueCurrentAvg);
        txtDebug = (TextView)findViewById(R.id.textValueDebug);
        imgIcon  = (ImageView)findViewById(R.id.imageIcon);
        btnOverlayStartStop = (Button)findViewById(R.id.buttonOverlayStartStopButton);

        batData = new BatteryData(getApplicationContext());

        // prepare data update handler
        updateHandler = new Handler();
        updateRunnable = new Runnable(){
            public void run(){
                batData.updateData();
                refreshUi();
                updateHandler.postDelayed(updateRunnable, 1*1000);
            }
        };

        // setup overlay start/stop button
        btnOverlayStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMyServiceIntent = new Intent(getApplicationContext(), OverlayWindowService.class);
                if(!overlayRunning) {
                    if(!testOverlayPermission())
                        return;
                    startService(startMyServiceIntent);
                    //finish();
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
        updateHandler.post(updateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
    }

    public boolean testOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return false;
        }
        return true;
    }

    private int counter = 0;
    private void refreshUi()
    {
        txtValueStatus.setText(batData.getStatusText());
        txtValueHealth.setText(batData.getHealthText());
        txtValuePresent.setText(batData.getPresentText());
        txtValueLevel.setText(batData.getLevelText());
        txtValueScale.setText(batData.getScaleText());
        //txtValueStatus.setText(batData.getIconStatusText());
        txtValuePlugged.setText(batData.getPluggedText());
        txtValueVoltage.setText(batData.getVoltageText());
        txtValueTemperature.setText(batData.getTemperatureText());
        txtValueTechnology.setText(batData.getTechnologyText());
        txtValueCurrent.setText(batData.getCurrentText());
        txtValueCurrentAvg.setText(batData.getCurrentAvgText());
        txtDebug.setText(""+(counter++));

        imgIcon.setImageResource(batData.getIconId());
    }

}


