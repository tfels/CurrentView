package de.felser_net.currentview;

import android.content.Context;
import android.os.Handler;

/**
 * Created by tf on 23.05.2016.
 */

/** This class controls the periodic update of the TextViews.
 * In the future it will completely contain the BatteryData object
 * and create/control the TextView elements.
 * */
public class PeriodicUiControl implements Runnable {
    public interface DataListUiView {
        public void refreshUi(BatteryData batData);
    }

    private Handler updateHandler;
    private BatteryData batData;
    private DataListUiView masterClass;

    PeriodicUiControl(DataListUiView myMaster, Context iContext) {
        masterClass = myMaster;
        batData = new BatteryData(iContext);
        updateHandler = new Handler();
    }

    public void Start() {
        updateHandler.post(this);
    }
    public void Stop() {
        updateHandler.removeCallbacks(this);
    }

    @Override
    public void run(){
        batData.updateData();
        masterClass.refreshUi(batData);
        updateHandler.postDelayed(this, 1*1000);
    }

}
