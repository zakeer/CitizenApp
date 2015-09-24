package me.zakeer.startapp;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleDetails extends Fragment {

    View view;
    RelativeLayout relativeLayout;

    public VehicleDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if(view != null) {
            OfficialActivity activity = (OfficialActivity)getActivity();

            Display display = activity.getWindowManager().getDefaultDisplay();
            int h = display.getHeight() - activity.h;

            relativeLayout = (RelativeLayout) view.findViewById(R.id.vehicleLayout);

            ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = h;
            relativeLayout.requestLayout();
            relativeLayout.setPadding(0, 0, 0, (int) ((int) activity.h * 1.5));

            /*ServerCal serverCal = new ServerCal();
            serverCal.execute("http://citizen.turpymobileapps.com/gettracks.php");*/
        }

    }


}
