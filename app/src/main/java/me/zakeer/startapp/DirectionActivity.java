package me.zakeer.startapp;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class DirectionActivity extends FragmentActivity implements
        DirectionApiAccessResponse,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public double latitue, longitude;
    public String toAddress, fromAddress;
    GoogleMap mMap;
    GMapV2Direction md;
    LatLng fromPosition, toPosition;
    WebView webview;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_map);
        webview = (WebView) findViewById(R.id.mapView);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);


        Bundle resultBunlde = getIntent().getExtras();
        if(resultBunlde != null) {
            GPSTracker gps = new GPSTracker(this);
            if(gps.canGetLocation()){
                latitue = gps.getLatitude();
                longitude = gps.getLongitude();
                fromAddress = gps.getAddress();
                //Toast.makeText(getApplicationContext(), address , Toast.LENGTH_LONG).show();

                fromPosition = new LatLng(latitue, longitude);
                toPosition = new LatLng(resultBunlde.getDouble("latitude"), resultBunlde.getDouble("longitude"));
                toAddress = resultBunlde.getString("address");

                Double toLat = resultBunlde.getDouble("latitude");
                Double toLong = resultBunlde.getDouble("longitude");

                webview.loadUrl("http://maps.google.com/maps?saddr=" + latitue + "," + longitude + "&daddr=" + toLat + "," + toLong);

                /*mMap = ((SupportMapFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
                md = new GMapV2Direction(getApplicationContext());
                md.delegate = this;
                md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);*/
            } else {
                gps.showSettingsAlert();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_direction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void postResult(Document asyncresult) {
        ArrayList<LatLng> directionPoint = md.getDirection(asyncresult);
        PolylineOptions rectLine = new PolylineOptions().width(20).color(Color.BLUE);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }

        Log.d("rectLine : " , rectLine.toString());
        /*mMap.addPolyline(rectLine);
        mMap.addMarker(new MarkerOptions()
                .position(fromPosition)
                .title(fromAddress)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.police_man_icon)) );
        mMap.addMarker(new MarkerOptions().position(toPosition).title(toAddress));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(toPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));*/
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
