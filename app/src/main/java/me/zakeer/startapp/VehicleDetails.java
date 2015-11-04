package me.zakeer.startapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


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
            final EditText input = (EditText) view.findViewById(R.id.etInput);
            Button btn = (Button) view.findViewById(R.id.button);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (input.getText() != null) {
                        Intent resultIntent = new Intent(getActivity(), ResultActivity.class);
                        resultIntent.putExtra("activity", "view_vehicle");
                        startActivityForResult(resultIntent, 1);
                        /*Intent intent = new Intent(getActivity(), VehicleReport.class);
                        startActivity(intent);*/
                    } else {
                        Toast.makeText(getActivity(), "All Fields Required", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Display display = activity.getWindowManager().getDefaultDisplay();
            int h = display.getHeight() - activity.h;

            relativeLayout = (RelativeLayout) view.findViewById(R.id.vehicleLayout);

            ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = h;
            relativeLayout.requestLayout();
            relativeLayout.setPadding(0, 0, 0, (int) (activity.h * 1.5));

            /*ServerCal serverCal = new ServerCal();
            serverCal.execute("http://citizen.turpymobileapps.com/gettracks.php");*/

            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
            });
        }

    }


}
