package com.thefloow.floowmap.model.bo;

/**
 * Created by rrs27 on 2018-01-11.
 */

public class Journey {
    private DBJourney journeyStarted;
    private DBJourney journeyFinished;

    public DBJourney getJourneyStarted() {
        return journeyStarted;
    }

    public void setJourneyStarted(DBJourney journeyStarted) {
        this.journeyStarted = journeyStarted;
    }

    public DBJourney getJourneyFinished() {
        return journeyFinished;
    }

    public void setJourneyFinished(DBJourney journeyFinished) {
        this.journeyFinished = journeyFinished;
    }

    public Journey(DBJourney journeyStarted, DBJourney journeyFinished) {
        this.journeyStarted = journeyStarted;
        this.journeyFinished = journeyFinished;
    }

    @Override
    public String toString() {
        return "Journey{" +
                "journeyStarted=" + journeyStarted.toString() +
                ", journeyFinished=" + journeyFinished.toString() +
                '}';
    }
}
