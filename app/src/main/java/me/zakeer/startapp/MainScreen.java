package me.zakeer.startapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        setMenuImgs();
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
            Intent userIntent = new Intent(getApplicationContext(), UserLogin.class);
            startActivity(userIntent);
            finish();
        }

        if (id == R.id.official_login) {
            Intent intent = new Intent(getApplicationContext(), UserLogin.class);
            intent.putExtra("login", 1);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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

    public void setRatioDimensions(ImageButton imgBtn, LinearLayout.LayoutParams params) {
        imgBtn.setLayoutParams(params);
    }

    public void getLayout(View v) {
        String str = (String) v.getTag();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (str.equals("report_incident")) {
            intent.putExtra("position", 0);
        } else if (str.equals("location")) {
            intent.putExtra("position", 1);
        } else if (str.equals("emergency")) {
            intent.putExtra("position", 2);
        } else if (str.equals("help_me")) {
            intent.putExtra("position", 3);
        }
        startActivity(intent);
    }
}
