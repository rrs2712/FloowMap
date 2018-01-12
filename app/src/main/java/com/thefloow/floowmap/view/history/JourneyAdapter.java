package com.thefloow.floowmap.view.history;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thefloow.floowmap.R;
import com.thefloow.floowmap.model.bo.Journey;

import java.util.List;

/**
 * Created by rrs27 on 2018-01-12.
 */

public class JourneyAdapter extends ArrayAdapter {

    private final String DEV = "RRS";
    private final String TAG = DEV + ":" + this.getClass().getSimpleName();

    /**
     * Class constructor.
     *
     * @param context - {@link Activity}
     * @param journeys - {@link List<com.thefloow.floowmap.model.bo.Journey>}
     */
    public JourneyAdapter(Activity context, List<Journey> journeys) {
        super(context, 0, journeys);
        Log.d(TAG, "JourneyAdapter");
    }

    /**
     * Provides a View for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item View.
     * @param convertView The recycled View to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing View is being reused, otherwise inflate the View
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.history_list_item, parent, false);
        }

        Journey journey = (Journey) getItem(position);

        TextView name = (TextView) listItemView.findViewById(R.id.j_name);
        name.setText(journey.getJourneyStarted().getJourneyIdUserReadable());

        TextView initial = (TextView) listItemView.findViewById(R.id.j_ini);
        initial.setText(R.string.journey_started_on);

        TextView end = (TextView) listItemView.findViewById(R.id.j_end);
        end.setText(R.string.journey_finished_on);

        TextView date_ini = (TextView) listItemView.findViewById(R.id.j_ini);
        date_ini.setText(journey.getJourneyStarted().getTimeStampUserReadable());

        TextView date_end = (TextView) listItemView.findViewById(R.id.j_end);
        date_end.setText(journey.getJourneyFinished().getTimeStampUserReadable());

        return listItemView;
    }
}
