package de.felser_net.currentview;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

/**
 * Created by tf on 23.05.2016.
 */
public class DataValue<T> implements Parcelable {

    protected String key = null;
    protected String displayName = "";
    protected String valuePostfix = "";
    protected T value = null;
    protected TextView txtView = null;

    protected boolean showInMainView = true;
    protected boolean showInOverlay = true;

    // **********
    // public constructor
    // **********
    public DataValue(String key) {
        this.key = key;
    }

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
    public String key() {
        return key;
    }
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

    // **********
    // simple getter and setter
    // **********

    public boolean showInMainView() {
        return showInMainView;
    }

    public void setShowInMainView(boolean showInMainView) {
        this.showInMainView = showInMainView;
    }

    public boolean showInOverlay() {
        return showInOverlay;
    }

    public void setShowInOverlay(boolean showInOverlay) {
        this.showInOverlay = showInOverlay;
    }

    // **********
    // Parcelable interface
    // **********
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(key);
        out.writeString(displayName);
        out.writeInt(showInMainView ? 1 : 0);
        out.writeInt(showInOverlay ? 1 : 0);
    }

    public static final Parcelable.Creator<DataValue> CREATOR = new Parcelable.Creator<DataValue>() {
        public DataValue createFromParcel(Parcel in) {
            return new DataValue(in);
        }
        public DataValue[] newArray(int size) {
            return new DataValue[size];
        }
    };

    private DataValue(Parcel in) {
        key            = in.readString();
        displayName    = in.readString();
        showInMainView = in.readInt() == 1;
        showInOverlay  = in.readInt() == 1;
    }
}
