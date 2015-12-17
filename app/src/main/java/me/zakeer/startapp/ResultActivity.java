package me.zakeer.startapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class ResultActivity extends FragmentActivity implements OnMapReadyCallback, DirectionApiAccessResponse {

    final static String URL = "http://citizen.turpymobileapps.com/";
    public int h;
    int position = 0;
    String intentServerData = null;
    String intentActivity = null;
    ImageLoader imageLoader;

    LatLng startLatLng, latLng, currLatLng;
    String address, updatedAddress;

    GMapV2Direction md;
    GoogleMap mMap;


    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putInt("PAGE_POSITON", position);

        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);
        setResult(RESULT_OK, mIntent);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Bundle resultBunlde = getIntent().getExtras();
        imageLoader = new ImageLoader(getApplicationContext());

        if (resultBunlde != null) {
            intentActivity = resultBunlde.getString("activity");
            intentServerData = resultBunlde.getString("server_data");

            if(resultBunlde.get("currentLocation") != null) {
                currLatLng = (LatLng) resultBunlde.get("currentLocation");
            }

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
                case "view_vehicle":
                    vehicleActivity();
                    break;
            }
        }
    }

    private void vehicleActivity() {
        setContentView(R.layout.activity_vehicle_report);
        setMenuImgs();
    }

    private void travelActivity() {
        if (intentServerData != null) {
            setContentView(R.layout.travel_detail);
            setMenuImgs();
            Button btnGetDirection = (Button) findViewById(R.id.btnGetDirection);
            TextView tvPhoneNo = (TextView) findViewById(R.id.tvPhoneNo);
            TextView tvVehicleNo = (TextView) findViewById(R.id.tvVehicleNo);

            try {
                JSONObject reportData = new JSONObject(intentServerData);
                startLatLng = new LatLng(reportData.getDouble("start_lat"), reportData.getDouble("start_long"));
                latLng = new LatLng(reportData.getDouble("updated_lat"), reportData.getDouble("updated_long"));
                address = (String) reportData.get("place");
                updatedAddress = (String) reportData.get("updated_place");
                tvPhoneNo.setText((CharSequence) reportData.get("mobile_no"));
                tvVehicleNo.setText((CharSequence) reportData.get("vehicle"));
               //setMap(mapFragment, latLng);



                btnGetDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent getDirectionIntent = new Intent(getApplicationContext(), DirectionActivity.class);
                        getDirectionIntent.putExtra("latitude", latLng.latitude);
                        getDirectionIntent.putExtra("longitude", latLng.longitude);
                        getDirectionIntent.putExtra("address", updatedAddress);
                        startActivity(getDirectionIntent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);//setMap(mapFragment, latLng);
            mMap = ((SupportMapFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            md = new GMapV2Direction(getApplicationContext());
            md.delegate = this;
            md.getDocument(startLatLng, latLng, GMapV2Direction.MODE_WALKING);

            Log.d("Server Activity Data : ", intentServerData);
        }
    }

    private void helpMeActivity() {
        if (intentServerData != null) {
            setContentView(R.layout.travel_detail);
            setMenuImgs();
            ImageButton imgBtnhelpMe = (ImageButton) findViewById(R.id.helpMe);
            Drawable image = getResources().getDrawable(R.drawable.device_tracking_white);
            imgBtnhelpMe.setBackground(image);

            ImageButton imgBtnTravel = (ImageButton) findViewById(R.id.safeTravel);
            Drawable travelImage = getResources().getDrawable(R.drawable.view_location);
            imgBtnTravel.setBackground(travelImage);


            TextView title = (TextView) findViewById(R.id.report_title);
            title.setText("Help Me Tracking");

            TextView tvPhoneNo = (TextView) findViewById(R.id.tvPhoneNo);
            TextView tvPlace = (TextView) findViewById(R.id.tvVehicleNo);
            TextView tvVehicleText = (TextView) findViewById(R.id.tvVehicleText);
            tvVehicleText.setText("Place :");

            Button btnGetDirection = (Button) findViewById(R.id.btnGetDirection);

            try {
                JSONObject reportData = new JSONObject(intentServerData);
                latLng = new LatLng(reportData.getDouble("latitude"), reportData.getDouble("longitude"));
                address = (String) reportData.get("place");

                tvPhoneNo.setText((CharSequence) reportData.get("phone"));
                tvPlace.setText(address);


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

            } catch (JSONException e) {
                e.printStackTrace();
            }

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);//setMap(mapFragment, latLng);
            setMap(mapFragment, latLng);

            Log.d("Server Activity Data : ", intentServerData);
        }
    }

    private void reportActivity() {
        if (intentServerData != null) {
            setContentView(R.layout.report_detail);
            setMenuImgs();
            TextView reportTitle = (TextView) findViewById(R.id.report_title);
            TextView reportDescription = (TextView) findViewById(R.id.report_description);
            TextView reportAt = (TextView) findViewById(R.id.report_at);
            Button btnGetDirection = (Button) findViewById(R.id.btnGetDirection);

            ImageView reportImage = (ImageView) findViewById(R.id.report_image);


            try {
                final JSONObject reportData = new JSONObject(intentServerData);

                reportTitle.setText((CharSequence) reportData.get("title"));
                reportDescription.setText((CharSequence) reportData.get("description"));
                reportAt.setText((CharSequence) reportData.get("report_at"));
                final String url = URL + reportData.get("image");
                imageLoader.DisplayImage(URL + reportData.get("image"), reportImage);

                reportImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent imageIntent = new Intent(getApplicationContext(), ImageActivity.class);
                        imageIntent.putExtra("image", url);
                        startActivity(imageIntent);
                    }
                });

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
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            setMap(mapFragment, latLng);

            Log.d("Server Activity Data : ", intentServerData);
        }
    }

    public void setMap(SupportMapFragment mapObject, LatLng latLng) {
        GoogleMap map = mapObject.getMap();
        map.addMarker(new MarkerOptions().position(latLng).title(address));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        //Toast.makeText(getApplication(), latLng.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_result, menu);
        return false;
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

        if(updatedAddress !=null && startLatLng !=null && currLatLng !=null) {
            googleMap.addMarker(new MarkerOptions()
                            .position(currLatLng)
                            .title("Police Location:")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.police_man_icon))
            );
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Updated Location:")
                    .snippet(updatedAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.updatedlocation_icon))
            );
            googleMap.addMarker(new MarkerOptions()
                            .position(startLatLng)
                            .title("Started Location:")
                            .snippet(address)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.startlocation_icon))
            );

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(startLatLng);
            builder.include(latLng);
            builder.include(currLatLng);
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        } else {
            //Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
            googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }


    }

    public void setRatioDimensions(ImageButton imgBtn, LinearLayout.LayoutParams params) {
        imgBtn.setLayoutParams(params);
    }

    public void setMenuImgs() {
        BitmapDrawable img = (BitmapDrawable) this.getResources().getDrawable(R.drawable.view_reports);
        int imgW = img.getBitmap().getWidth();
        int imgH = img.getBitmap().getHeight();
        Display display = getWindowManager().getDefaultDisplay();
        int w = display.getWidth() / 4;
        h = w * imgH / imgW;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);

        LinearLayout menuLayout = (LinearLayout) findViewById(R.id.menu_layout);
        for (int i = 0; i < menuLayout.getChildCount(); i++) {
            setRatioDimensions((ImageButton) menuLayout.getChildAt(i), params);
        }
//        Toast.makeText(getApplication(), ""+ menuLayout.getChildCount(), Toast.LENGTH_SHORT).show();
    }


    public void setMenuActive(View v, int position) {
        int id = (v != null) ? v.getId() : position;
        LinearLayout menuLayout = (LinearLayout) findViewById(R.id.menu_layout);
        for (int i = 0; i < menuLayout.getChildCount(); i++) {
            int menuId = menuLayout.getChildAt(i).getId();
            String imageLabel = (String) menuLayout.getChildAt(i).getTag();
            if (id == menuId || i == position) {
                int resID = getResources().getIdentifier(imageLabel + "_white", "drawable", getPackageName());
                //Toast.makeText(getApplication(), imageLabel, Toast.LENGTH_SHORT).show();
                Drawable image = getResources().getDrawable(resID);
                menuLayout.getChildAt(i).setBackground(image);
            } else {
                int resID = getResources().getIdentifier(imageLabel, "drawable", getPackageName());
                Drawable image = getResources().getDrawable(resID);
                menuLayout.getChildAt(i).setBackground(image);
            }
        }
    }

    public void getLayout(View v) {
        Intent intent = new Intent(getApplicationContext(), OfficialActivity.class);
        String str = (String) v.getTag();
        if (str.equals("view_reports")) {
            position = 0;
            onBackPressed();
        } else if (str.equals("view_location")) {
            position = 1;
            onBackPressed();
        } else if (str.equals("vehicle_details")) {
            position = 2;
            onBackPressed();
        } else if (str.equals("device_tracking")) {
            position = 3;
            onBackPressed();
        }
    }

    @Override
    public void postResult(Document asyncresult, String color) {
        ArrayList<LatLng> directionPoint = md.getDirection(asyncresult);
        PolylineOptions rectLine = new PolylineOptions().width(12).color(Color.BLUE);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }

        Log.d("rectLine : ", rectLine.toString());
        mMap.addPolyline(rectLine);
        /*mMap.addMarker(new MarkerOptions()
                .position(startLatLng)
                .title("Starting Location :")
                .snippet(address));
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(latLng).title("Last Updated Location :").snippet(updatedAddress));*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startLatLng);
        builder.include(latLng);
        builder.include(currLatLng);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }
}
