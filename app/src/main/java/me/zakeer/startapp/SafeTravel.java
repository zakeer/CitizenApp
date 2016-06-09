package me.zakeer.startapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


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
    Button btnSubmit, btnStop;
    EditText editText;
    FragmentActivity myContext;
    Boolean isTracking = false;

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
            //supportMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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
            final MainActivity activity = (MainActivity) getActivity();
            latitue = activity.latitue;
            longitude = activity.longitude;
            address = (address != null) ? activity.address : "";
            isTracking = activity.isTracking;


            editText = (EditText) view.findViewById(R.id.etText);
            btnSubmit = (Button) view.findViewById(R.id.saveButton);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = editText.getText().toString();
                    if (title.equals("")) {
                        Toast.makeText(getActivity(), "All Fields Required", Toast.LENGTH_SHORT).show();
                    } else {
                        ServerCal serverCal = new ServerCal();
                        serverCal.execute("http://citizen.turpymobileapps.com/savetrack.php", title);
                    }
                }
            });

            btnStop = (Button) view.findViewById(R.id.stopButton);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTracking = false;
                    activity.isTracking = false;
                    changeButtons();
                }
            });
            changeButtons();

            LatLng latLng = new LatLng(latitue, longitude);
            googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        }

    }

    public void changeButtons() {
        if(isTracking) {
            btnStop.setClickable(true);
            Drawable image = getResources().getDrawable(R.drawable.bg_report);
            btnStop.setBackground(image);

            Drawable imageDisable = getResources().getDrawable(R.drawable.disable_bg);
            btnSubmit.setBackground(imageDisable);
            btnSubmit.setClickable(false);
        } else {
            btnSubmit.setClickable(true);
            Drawable image = getResources().getDrawable(R.drawable.bg_report);
            btnSubmit.setBackground(image);

            Drawable imageDisable = getResources().getDrawable(R.drawable.disable_bg);
            btnStop.setBackground(imageDisable);
            btnStop.setClickable(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(latitue, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    public void showDialog(String msg, String type) {
        AlertDialog.Builder dialog;
        String title = msg;
        dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);

        if (type.equals("fail")) {
            dialog.setIcon(R.drawable.alert);
            dialog.setCancelable(false);
            dialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        } else {
            dialog.setIcon(R.drawable.success);
            dialog.setCancelable(true);
            dialog.setPositiveButton("Success", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    public class ServerCal extends AsyncTask<String, String, String> {

        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(params[0]);
            HttpResponse response = null;

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            try {
                multipartEntity.addPart("vehicle", new StringBody(params[1]));
                multipartEntity.addPart("phone", new StringBody("9533222116"));
                multipartEntity.addPart("start_lat", new StringBody(String.valueOf(latitue)));
                multipartEntity.addPart("start_long", new StringBody(String.valueOf(longitude)));
                multipartEntity.addPart("place", new StringBody(String.valueOf(address)));
                //multipartEntity.addPart("vehicle", new StringBody(String.valueOf("AP07 2478")));
                Log.d("Multipar", "" + multipartEntity);
                postRequest.setEntity(multipartEntity);
                HttpResponse responsePOST = client.execute(postRequest);
                HttpEntity resEntity = responsePOST.getEntity();
                String _response = EntityUtils.toString(resEntity); // content will be consume only once
                return (_response != null) ? _response : null;


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Execute String", s);
            dialog.dismiss();
            try {
                JSONObject serverData = new JSONObject(s);
                String status = serverData.getString("message");
                Log.d("Server Data", s);
                if (status.equals("successfully Device Track")) {
                    showDialog(status, "success");
                    MainActivity activity = (MainActivity) getActivity();
                    activity.isTracking = true;
                    isTracking = true;
                    changeButtons();
                    activity.trackId = serverData.getInt("id");
                } else {
                    showDialog(status, "fail");
                    System.out.print("login failled");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


}
