package me.zakeer.startapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public double latitue, longitude;
    public String address;

    ViewPager viewPager;
    ViewPager.OnPageChangeListener onPageChangeListener;

    GoogleApiClient googleApiClient;
    Location lastLocation;


    AlertDialog.Builder alertDialog;

//    protected synchronized void buildGoogleApiClient() {
//        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMenuImgs();

        viewPager = (ViewPager) findViewById(R.id.layout_container);
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getApplicationContext(), 0);
        viewPager.setAdapter(pagerAdapter);

        //buildGoogleApiClient();

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            latitue = gps.getLatitude();
            longitude = gps.getLongitude();
            address = gps.getAddress();
            //Toast.makeText(getApplicationContext(), address , Toast.LENGTH_LONG).show();
        } else {
            gps.showSettingsAlert();
        }


        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setMenuActive(null, position);
            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("Fragment" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };


        viewPager.setOnPageChangeListener(onPageChangeListener);
        pagerAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewPager.getCurrentItem() > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
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
        int h = w * imgH / imgW;
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
        String str = (String) v.getTag();
        if (str.equals("report_incident")) {
            viewPager.setCurrentItem(0, true);
            setMenuActive(v, 0);
        } else if (str.equals("location")) {
            viewPager.setCurrentItem(1, true);
            setMenuActive(v, 1);
        } else if (str.equals("emergency")) {
            viewPager.setCurrentItem(2, true);
            setMenuActive(v, 2);
        } else if (str.equals("help_me")) {
            viewPager.setCurrentItem(3, true);
            setMenuActive(v, 3);
        }
    }

    public void showDialog(){
        System.out.println("ShowDialog...");
        alertDialog = new AlertDialog.Builder(getApplicationContext());
        alertDialog.setMessage("Nothing to Show");
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.official_logout) {
            finish();
        }

        if (id == R.id.official_login) {
            Intent intent = new Intent(getApplicationContext(), UserLogin.class);
            intent.putExtra("login", 1);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        String txt = String.valueOf(lastLocation.getLatitude()) + " : " + String.valueOf(lastLocation.getLongitude());
        Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "onConnectionFailed : " + connectionResult.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(), "onLocationChanged " + location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(getApplicationContext(), "onStatusChanged " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(), "onProviderEnabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(), provider, Toast.LENGTH_SHORT).show();
        showDialog();
    }



}



