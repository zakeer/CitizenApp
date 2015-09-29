package me.zakeer.startapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
                    resultActivity();
            }
        }
    }

    private void resultActivity() {
        if (intentServerData != null) {
            setContentView(R.layout.report_detail);
            TextView reportTitle = (TextView) findViewById(R.id.report_title);
            TextView reportDescription = (TextView) findViewById(R.id.report_description);
            TextView reportAt = (TextView) findViewById(R.id.report_at);

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

                //setMap(mapFragment, latLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Server Activity Data : ", intentServerData);
        }
    }

    public void setMap(SupportMapFragment mapObject, LatLng latLng) {
        GoogleMap map = mapObject.getMap();
        map.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
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
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }
}
