package de.felser_net.currentview;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by tf on 30.04.2016.
 */
public class BatteryData {

    private Context context = null;

    private int status = BatteryManager.BATTERY_STATUS_UNKNOWN;
    private int health = 0;
    private boolean present = false;
    private int level = -1;
    private int scale = -1;
    private int icon = 0;
    private int plugged = 0;
    private int voltage = 0;
    private int temperature = 0;
    private String technology = "";
    private int invalid_charger = 0;

    private int current = 0;
    private int currentAvg = 0;

    public BatteryData(Context iContext) {
        context = iContext;
    }

    public void updateData() {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        extractIntentData(batteryStatus);

        // additional data which is also available in the intent, but this is the official API
        BatteryManager mBatteryManager = (BatteryManager)context.getSystemService(Context.BATTERY_SERVICE);
        current = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        currentAvg = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
    }

    public void extractIntentData(Intent batteryStatus) {
        status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        present = batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        icon = batteryStatus.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, -1);
        plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        //invalid_charger = batteryStatus.getIntExtra(BatteryManager.EXTRA_INVALID_CHARGER, -1);
    }


    public String getStatusText() {
        switch(status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                return context.getResources().getString(R.string.valUnknown);
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return context.getResources().getString(R.string.valCharging);
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return context.getResources().getString(R.string.valDischarging);
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return context.getResources().getString(R.string.valNotCharging);
            case BatteryManager.BATTERY_STATUS_FULL:
                return context.getResources().getString(R.string.valFull);
            default:
                return context.getResources().getString(R.string.valUnknownValue);
        }
    }

    public String getHealthText() {
        switch(health) {
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return context.getResources().getString(R.string.valUnknown);
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return context.getResources().getString(R.string.valGood);
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return context.getResources().getString(R.string.valOverHeat);
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return context.getResources().getString(R.string.valDead);
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return context.getResources().getString(R.string.valOverVoltage);
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return context.getResources().getString(R.string.valUnspecifiedFailure);
            case BatteryManager.BATTERY_HEALTH_COLD:
                return context.getResources().getString(R.string.valCold);
            default:
                return context.getResources().getString(R.string.valUnknownValue);
        }
    }

    public String getPresentText() {
        return String.valueOf(present);
    }
    public String getLevelText() {
        return String.valueOf(level*100/scale) + "%";
    }
    public String getScaleText() {
        return String.valueOf(scale) + "%";
    }
    public int getIconId() {
        return icon;
    }

    public String getPluggedText() {
        switch(plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return context.getResources().getString(R.string.valAC);
            case BatteryManager.BATTERY_PLUGGED_USB:
                return context.getResources().getString(R.string.valUSB);
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return context.getResources().getString(R.string.valWireless);
            default:
                return context.getResources().getString(R.string.valUnknownValue);
        }
    }

    public String getVoltageText() {
        return String.valueOf(voltage/1000.0) + "V";
    }
    public String getTemperatureText() {
        return String.valueOf(temperature/10.0) + "°";
    }
    public String getTechnologyText() {
        return technology;
    }
    public String getCurrentText() {
        return String.valueOf(current) + " uA";
    }
    public String getCurrentAvgText() {
        return String.valueOf(currentAvg) + " uA";
    }
}
