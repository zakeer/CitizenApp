package me.zakeer.startapp;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SafeTravel extends SupportMapFragment implements OnMapReadyCallback {

    private static final String TAG = "Log";
    private FragmentActivity myContext;
    android.support.v4.app.FragmentManager fragManager;
    View view;
    private GoogleMap googleMap;
    SupportMapFragment supportMapFragment;

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        fragManager = myContext.getSupportFragmentManager();
        super.onAttach(activity);
    }

    public SafeTravel() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT < 21) {
            //supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        } else {
            //supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        //mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        //GoogleMap mMap = supportMapFragment.getMap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_safe_travel, container, false);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.view = getView();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.commit();

        getFragmentManager().beginTransaction().remove(fragment).commit();*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }


}
