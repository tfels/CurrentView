package de.felser_net.currentview;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OverlayInstrumentedTest {
    public static final String PACKAGE_NAME = "de.felser_net.currentview";
    public static final String TAG = "TF";

    private UiDevice mDevice;
    // the activity is to be launched before each test
    //@Rule
    //public ActivityScenarioRule<MainDataActivity> activityTestRule = new ActivityScenarioRule<>(MainDataActivity.class);

    // grant permission, alternatively:
    // adb shell pm grant de.felser_net.currentview android.permission.SYSTEM_ALERT_WINDOW
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.SYSTEM_ALERT_WINDOW);

    @Before
    public void init() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

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
        UiObject2 overlayWindow = getOverlayWindow();
        if(overlayWindow == null)
            throw new RuntimeException("Unable to find Overlay Window");
        assertThat(overlayWindow, notNullValue());

        // and close again
        buttonStartStop.check(matches(isDisplayed()));
        buttonStartStop.check(matches(withText("Stop")));
        buttonStartStop.perform(click());

        overlayWindow = getOverlayWindow();
        assertThat(overlayWindow, is(nullValue()));
    }

    private UiObject2 getOverlayWindow() {
        //mDevice.wait(Until.hasObject(By.desc("CurrentView Overlay Window")), 2*1000);

        UiObject2 myObject = mDevice.findObject(By.desc("CurrentView Overlay Window"));
        if(myObject != null)
            assertThat(myObject.getApplicationPackage(), is(equalTo(PACKAGE_NAME)));
        return myObject;
    }
}