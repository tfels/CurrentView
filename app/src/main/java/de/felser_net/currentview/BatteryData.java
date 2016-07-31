package de.felser_net.currentview;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tf on 30.04.2016.
 */
public class BatteryData {

    public static final boolean SHOW_VALUE_DEFAULT_VALUE = true;

    private Context context = null;
    ArrayList<DataValue> values;

    public BatteryData(Context iContext) {
        context = iContext;
        values = new ArrayList<DataValue>();

        // setup the value list
        // overwrite some output functions
        values.add(new DataValue<Integer>("current")
                .setValue(0)
                .setDisplayName(context.getResources().getString(R.string.txtCurrent))
                .setPostfix(" uA")
        );
        values.add(new DataValue<Integer>("avgCurrent")
                .setValue(0)
                .setDisplayName(context.getResources().getString(R.string.txtCurrentAvg))
                .setPostfix(" uA")
        );
        values.add(
                new DataValue<Integer>(BatteryManager.EXTRA_STATUS) {
                    public String valueText() {
                        return getStatusText(value);
                    }
                }
                        .setValue(-1)
                        .setDisplayName(context.getResources().getString(R.string.txtStatus))
        );
        values.add(
                new DataValue<Integer>(BatteryManager.EXTRA_HEALTH) {
                    public String valueText() {
                        return getHealthText(value);
                    }
                }
                        .setValue(-1)
                        .setDisplayName(context.getResources().getString(R.string.txtHealth))
        );
        values.add(new DataValue<Boolean>(BatteryManager.EXTRA_PRESENT)
                .setValue(false)
                .setDisplayName(context.getResources().getString(R.string.txtPresent))
        );
        values.add(
                new DataValue<Integer>(BatteryManager.EXTRA_LEVEL) {
                    public String valueText() {
                        int scale = (Integer)(values.get(6).value());
                        return String.valueOf(value * 100 / scale) + "%";
                    }
                }
                        .setValue(-1)
                        .setDisplayName(context.getResources().getString(R.string.txtLevel))
        );
        values.add(new DataValue<Integer>(BatteryManager.EXTRA_SCALE)
                .setValue(-1)
                .setDisplayName(context.getResources().getString(R.string.txtScale))
                .setPostfix("%")
        );
        values.add(
                new DataValue<Integer>(BatteryManager.EXTRA_PLUGGED) {
                    public String valueText() {
                        return getPluggedText(value);
                    }
                }
                        .setValue(-1)
                        .setDisplayName(context.getResources().getString(R.string.txtPlugged))
        );
        values.add(
                new DataValue<Integer>(BatteryManager.EXTRA_VOLTAGE) {
                    public String valueText() {
                        return String.valueOf(value / 1000.0) + "V";
                    }
                }
                        .setValue(-1)
                        .setDisplayName(context.getResources().getString(R.string.txtVoltage))
                        .setPostfix("V")
        );
        values.add(
                new DataValue<Integer>(BatteryManager.EXTRA_TEMPERATURE) {
                    public String valueText() {
                        return String.valueOf(value / 10.0) + "°";
                    }
                }
                        .setValue(-1)
                        .setDisplayName(context.getResources().getString(R.string.txtTemperature))
        );
        values.add(new DataValue<String>(BatteryManager.EXTRA_TECHNOLOGY)
                .setValue("")
                .setDisplayName(context.getResources().getString(R.string.txtTechnology))
        );

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        LoadSavedViewSettings(sharedPref);
    }

    public void updateData() {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        extractIntentData(batteryStatus);

        // additional data which is also available in the intent, but this is the official API
        BatteryManager mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        values.get(0).setValue(mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW));
        values.get(1).setValue(mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE));
    }

    public void extractIntentData(Intent batteryStatus) {
        values.get(2).setValue(batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1));
        values.get(3).setValue(batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
        values.get(4).setValue(batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false));
        values.get(5).setValue(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        values.get(6).setValue(batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
        values.get(7).setValue(batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1));
        values.get(8).setValue(batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
        values.get(9).setValue(batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
        values.get(10).setValue(batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));
    }

    public ArrayList<DataValue> getValues() {
        return values;
    }

    public void LoadSavedViewSettings(SharedPreferences prefs) {
        String prefix = context.getResources().getString(R.string.pref_key_prefix_main);
        for (DataValue val: values) {
            boolean show = prefs.getBoolean(prefix + val.key(), SHOW_VALUE_DEFAULT_VALUE);
            val.setShowInMainView(show);
        }

        prefix = context.getResources().getString(R.string.pref_key_prefix_overlay);
        for (DataValue val: values) {
            boolean show = prefs.getBoolean(prefix + val.key(), SHOW_VALUE_DEFAULT_VALUE);
            val.setShowInOverlay(show);
        }
    }


    public GridLayout createValueGrid(boolean forOverlay, boolean withNames) {
        GridLayout valueGrid = new GridLayout(context);

        int valueColumn = 0;
        if(withNames)
            valueColumn = 1;

        valueGrid.setColumnCount(valueColumn+1);

        // creating the values in the grid layout
        int rowCount = 0;
        GridLayout.Spec columnSpecName = GridLayout.spec(0, 1.0f);
        GridLayout.Spec columnSpecVal  = GridLayout.spec(valueColumn, GridLayout.END);

        for(DataValue val : getValues()) {

            boolean show = forOverlay ? val.showInOverlay() : val.showInMainView();
            if(!show)
                continue;

            GridLayout.Spec rowSpec = GridLayout.spec(rowCount);
            GridLayout.LayoutParams layoutParams;

            // create textView for the name
            if(withNames) {
                TextView txtName = new TextView(context);
                txtName.setText(val.displayName());
                layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpecName);
                valueGrid.addView(txtName, layoutParams);
            }

            // create textView for the value
            TextView txtVal = new TextView(context);
            txtVal.setText(val.valueText());
            layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpecVal);
            valueGrid.addView(txtVal, layoutParams);

            //viewElements.add(txtV);
            val.setTextView(txtVal);

            rowCount++;
        }
        return valueGrid;
    }

    private String getStatusText(Integer status) {
        switch (status) {
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

    private String getHealthText(Integer health) {
        switch (health) {
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

    private String getPluggedText(Integer plugged) {
        switch (plugged) {
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
}
