package com.thefloow.floowmap.model.bo;

/**
 * Created by rrs27 on 2018-01-11.
 */

public class DBJourney {
    private int id;
    private int journeyId;
    private String status;
    private long timeStamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(int journeyId) {
        this.journeyId = journeyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public DBJourney(int id, int journeyId, String status, long timeStamp) {
        this.id = id;
        this.journeyId = journeyId;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "DBJourney{" +
                "id=" + id +
                ", journeyId=" + journeyId +
                ", status='" + status + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
