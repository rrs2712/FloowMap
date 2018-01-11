package com.thefloow.floowmap.model.bo;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by rrs27 on 2018-01-11.
 */

public class Journey {
    private int journeyID;
    private List<LatLng> LatLngs;

    public int getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(int journeyID) {
        this.journeyID = journeyID;
    }

    public List<LatLng> getLatLngs() {
        return LatLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        LatLngs = latLngs;
    }

    public Journey(int journeyID, List<LatLng> latLngs) {
        this.journeyID = journeyID;
        LatLngs = latLngs;
    }

    @Override
    public String toString() {
        return "Journey{" +
                "journeyID=" + journeyID +
                ", LatLngs=" + LatLngs +
                '}';
    }
}
