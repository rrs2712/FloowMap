package com.thefloow.floowmap.view.history;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.thefloow.floowmap.R;
import com.thefloow.floowmap.model.bo.DBJourney;
import com.thefloow.floowmap.model.bo.Journey;
import com.thefloow.floowmap.model.db.DBHelper;
import com.thefloow.floowmap.presenter.util.Util;

import java.util.ArrayList;
import java.util.List;

public class JourneyHistoryActivity extends AppCompatActivity {

    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_history);

        this.dbHelper = new DBHelper(this.getApplicationContext());

        List<Journey> journeys = getFakeJourney();

        if(journeys!=null){
            setWidgets(journeys);
        }else {
            Toast.makeText(this,"No records to show",Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    private List<Journey> getFakeJourney() {
        Log.d(TAG,"getFakeJourney");

        List<Journey> journeys = new ArrayList<>();

        // Get DBJourneys with status of finished
        Cursor cursor = dbHelper.getJourneysCompleted();
        if(Util.isCursorEmpty(cursor)){ return null; }

        // Get Journeys from DBJourneys with status of finished
        journeys = getJourneys(cursor);

        return journeys;
    }

    private List<Journey> getJourneys(Cursor cursor) {
        if(Util.isCursorEmpty(cursor)){return null;}

        List<Journey> journeys = new ArrayList<>();

        Log.d(TAG,"Cursor elements count = " + cursor.getCount());
        while (cursor.moveToNext()){
            DBJourney dbJourney = Util.getDBJourneyFrom(cursor);

            if(dbJourney!=null){

                Log.d(TAG,dbJourney.toString());
                Journey journey = getJourney(dbJourney.getJourneyId());

                if(journey!=null){
                    Log.d(TAG,journey.toString());
                    journeys.add(journey);
                }
            }
        }
        return journeys;
    }

    private Journey getJourney(int journeyID){
        Log.d(TAG,"getJourney " + journeyID);
        Cursor cIni = dbHelper.getDBJourney(journeyID,DBHelper.JOURNEY_BEGINS);
        if(Util.isCursorEmpty(cIni)){
            Log.d(TAG,"cIni is null");
            return null;}

        Cursor cEnd = dbHelper.getDBJourney(journeyID,DBHelper.JOURNEY_ENDS);
        if(Util.isCursorEmpty(cEnd)){
            Log.d(TAG,"cEnd is null");
            return null;}

        DBJourney ini = Util.getDBJourneyFrom(cIni);
        DBJourney end = Util.getDBJourneyFrom(cEnd);

        return new Journey(ini,end);
    }

    private List<DBJourney> getJourneysList(Cursor completedJourneys) {
        List<DBJourney> dbJourneys = Util.getDBJourneyListFrom(completedJourneys);
        return dbJourneys;
    }

    private void setWidgets(List<Journey> journeys) {
        Log.d(TAG,"setWidgets");
        ListView journeyListView = (ListView) findViewById(R.id.list);

        final ArrayAdapter<Journey> adapter = new JourneyAdapter(this, journeys);
        journeyListView.setAdapter(adapter);

    }
}
