package me.zakeer.startapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends FragmentActivity implements OnMapReadyCallback {

    final static String URL = "http://citizen.turpymobileapps.com/";

    String intentServerData = null;
    String intentActivity = null;
    ImageLoader imageLoader;

    LatLng latLng;
    String address;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle resultBunlde = getIntent().getExtras();
        imageLoader = new ImageLoader(getApplicationContext());

        if (resultBunlde != null) {
            intentActivity = resultBunlde.getString("activity");
            intentServerData = resultBunlde.getString("server_data");

            switch (intentActivity) {
                case "view_reports":
                    reportActivity();
                    break;
                case "view_travel":
                    travelActivity();
                    break;
                case "view_help":
                    helpMeActivity();
                    break;
            }
        }
    }

    private void travelActivity() {
        if (intentServerData != null) {
            setContentView(R.layout.travel_detail);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            try {
                JSONObject reportData = new JSONObject(intentServerData);
                latLng = new LatLng(reportData.getDouble("updated_lat"), reportData.getDouble("updated_long"));
                address = (String) reportData.get("place");
                mapFragment.getMapAsync(this);                //setMap(mapFragment, latLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Server Activity Data : ", intentServerData);
        }
    }

    private void helpMeActivity() {
        if (intentServerData != null) {
            setContentView(R.layout.travel_detail);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            TextView title = (TextView) findViewById(R.id.report_title);
            title.setText("Help Me Tracking");

            try {
                JSONObject reportData = new JSONObject(intentServerData);
                latLng = new LatLng(reportData.getDouble("latitude"), reportData.getDouble("longitude"));
                address = (String) reportData.get("place");
                mapFragment.getMapAsync(this);                //setMap(mapFragment, latLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Server Activity Data : ", intentServerData);
        }
    }

    private void reportActivity() {
        if (intentServerData != null) {
            setContentView(R.layout.report_detail);
            TextView reportTitle = (TextView) findViewById(R.id.report_title);
            TextView reportDescription = (TextView) findViewById(R.id.report_description);
            TextView reportAt = (TextView) findViewById(R.id.report_at);
            Button btnGetDirection = (Button) findViewById(R.id.btnGetDirection);

            ImageView reportImage = (ImageView) findViewById(R.id.report_image);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            try {
                JSONObject reportData = new JSONObject(intentServerData);

                reportTitle.setText((CharSequence) reportData.get("title"));
                reportDescription.setText((CharSequence) reportData.get("description"));
                reportAt.setText((CharSequence) reportData.get("report_at"));
                imageLoader.DisplayImage(URL + reportData.get("image"), reportImage);

                latLng = new LatLng(reportData.getDouble("lat"), reportData.getDouble("lon"));
                address = (String) reportData.get("address");

                btnGetDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent getDirectionIntent = new Intent(getApplicationContext(), DirectionActivity.class);
                        getDirectionIntent.putExtra("latitude", latLng.latitude);
                        getDirectionIntent.putExtra("longitude", latLng.longitude);
                        getDirectionIntent.putExtra("address", address);
                        startActivity(getDirectionIntent);
                    }
                });

                //setMap(mapFragment, latLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Server Activity Data : ", intentServerData);
        }
    }

    public void setMap(SupportMapFragment mapObject, LatLng latLng) {
        GoogleMap map = mapObject.getMap();
        map.addMarker(new MarkerOptions().position(latLng).title(address));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Toast.makeText(getApplication(), latLng.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}
