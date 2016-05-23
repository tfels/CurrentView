package de.felser_net.currentview;

import android.widget.TextView;

/**
 * Created by tf on 23.05.2016.
 */
public class DataValue<T> {

    protected String displayName = "";
    protected String valuePostfix = "";
    protected T value = null;
    protected TextView txtView = null;

    // **********
    // a lot of setters
    // **********
    public DataValue setValue(T iVal) {
        value = iVal;
        if(txtView != null)
            txtView.setText(valueText());
        return this;
    }
    public DataValue setDisplayName(String text) {
        displayName = text;
        return this;
    }
    public DataValue setPostfix(String text) {
        valuePostfix = text;
        return this;
    }
    public DataValue setTextView(TextView view) {
        txtView = view;
        return this;
    }


    // **********
    // a lot of getters
    // **********
    public T value() {
        return value;
    }
    public String displayName() {
        return displayName;
    }
    public TextView TextView() {
        return txtView;
    }

    public String valueText() {
        if(value == null)
            return "";
        return value.toString() + valuePostfix;
    }
}
