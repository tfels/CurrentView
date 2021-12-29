package de.felser_net.currentview;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import android.Manifest;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class OverlayInstrumentedTest {
    public static final String PACKAGE_NAME = "de.felser_net.currentview";
    public static final String TAG = "TF";

    // the activity is to be launched before each test
    //@Rule
    //public ActivityScenarioRule<MainDataActivity> activityTestRule = new ActivityScenarioRule<>(MainDataActivity.class);

    // grant permission, alternatively:
    // adb shell pm grant de.felser_net.currentview android.permission.SYSTEM_ALERT_WINDOW
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.SYSTEM_ALERT_WINDOW);

    @Test
    public void startOverlay(){
        // start activity ...
        final ActivityScenario<MainDataActivity> scenario = ActivityScenario.launch(MainDataActivity.class);

        // ... and click "Start" button
        ViewInteraction buttonStartStop = onView(withId(R.id.buttonOverlayStartStop));
        buttonStartStop.check(matches(isDisplayed()));
        buttonStartStop.check(matches(withText("Start")));
        buttonStartStop.perform(click());

        // now let's search the overlay window
        //listDeviceObjects();
        UiObject2 overlayWindow = getOverlayWindow();

        if(overlayWindow == null)
            throw new RuntimeException("Unable to find Overlay Window");
        //assertThat(overlayWindow, notNullValue());
        //assertNotNull(overlayWindow);
    }

    private UiObject2 getOverlayWindow() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        List<UiObject2> list = device.findObjects(By.pkg(PACKAGE_NAME));

        for(UiObject2 item: list) {
            String text = item.getContentDescription();
            if(text != null && text.equals("CurrentView Overlay Window"))
                return item;
        }
        return null;
    }

    // some test dump functions
    private void listDeviceObjects() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        List<UiObject2> list = device.findObjects(By.pkg(PACKAGE_NAME));

        for(UiObject2 item: list) {
            String text = item.getText();
            if (text != null && text.equals("Not Charging")) {
                Log.d(TAG, "found: parent=");
                dumpItemInfoPath(item, null);
            }
        }
    }

    private void dumpItemInfoPath(UiObject2 item, String prefix) {

        if (prefix == null)
            prefix= "";

        String info = prefix;
        info += " classname="+ item.getClassName();
        //info += " text="+ item.getText();
        info += " desc="+ item.getContentDescription();
        //info += " res="+ item.getResourceName();
        //info += " pkg="+ item.getApplicationPackage();
        UiObject2 parent = item.getParent();
        //info += " parent="+ parent;
        Log.d(TAG, info);
        if(parent != null) {
            dumpItemInfoPath(parent, prefix + "   ");
        }
    }
}