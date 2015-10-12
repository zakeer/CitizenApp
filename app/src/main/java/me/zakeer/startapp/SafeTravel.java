package me.zakeer.startapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class SafeTravel extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "Log";
    android.support.v4.app.FragmentManager fragManager;
    View view;
    GoogleMap googleMap;
    SupportMapFragment supportMapFragment;
    Double latitue, longitude;
    String address = "";
    private FragmentActivity myContext;

    public SafeTravel() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        fragManager = myContext.getSupportFragmentManager();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.fragment_safe_travel, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return true;
            }
        });

        return view;
        //return inflater.inflate(R.layout.fragment_safe_travel, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT < 21) {
            supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        } else {
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //this.view = getView();
        if (view != null) {
            MainActivity activity = (MainActivity) getActivity();
            latitue = activity.latitue;
            longitude = activity.longitude;
            address = activity.address.trim();


        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();

        getFragmentManager().beginTransaction().remove(fragment).commit();*/
    }


    @Override
    public void onPause() {
        super.onPause();
        /*Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();

        getFragmentManager().beginTransaction().remove(fragment).commit();*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(latitue, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }


}
