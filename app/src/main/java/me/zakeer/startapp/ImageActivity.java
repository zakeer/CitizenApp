package me.zakeer.startapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends Activity {

    ImageView imageView;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Bundle resultBunlde = getIntent().getExtras();
        imageLoader = new ImageLoader(getApplicationContext());

        if (resultBunlde != null) {
            String url = resultBunlde.getString("image");
            imageView = (ImageView) findViewById(R.id.imageView);
            imageLoader.DisplayImage(url, imageView);

        } else {
            finish();
        }


    }
}
