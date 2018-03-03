package com.manasoft.fireway;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Created by Manasoft on 09/01/2018.
 */
@IgnoreExtraProperties
public class ElectricityLog {
    private Map<String,String> timestampOn;
    private Map<String,String> timestampOff;

    public ElectricityLog() {
    }

    public Map<String, String> getTimestampOn() {
        return timestampOn;
    }

    public void setTimestampOn(Map<String, String> timestampOn) {
        this.timestampOn = timestampOn;
    }

    public Map<String, String> getTimestampOff() {
        return timestampOff;
    }

    public void setTimestampOff(Map<String, String> timestampOff) {
        this.timestampOff = timestampOff;
    }
}
