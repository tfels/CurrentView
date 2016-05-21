package de.felser_net.currentview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
    private ImageView imgIcon = null;

    private BatteryData batData;

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
        imgIcon  = (ImageView)findViewById(R.id.imageIcon);

        batData = new BatteryData(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        batData.updateData();
        refreshUi();
    }

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

        imgIcon.setImageResource(batData.getIconId());
    }

}


