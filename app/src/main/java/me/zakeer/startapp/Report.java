package me.zakeer.startapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class Report extends Fragment implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    File imageFile;
    Double latitue, longitude;
    String address = "";
    EditText etTitle, etDescription;
    Button btnSubmit;

    ImageView btnGetPhoto;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if(view != null) {
            MainActivity activity = (MainActivity)getActivity();
            latitue = activity.latitue;
            longitude = activity.longitude;
            address = (activity.address != null) ? activity.address.trim() : "";

            etTitle = (EditText) view.findViewById(R.id.etTitle);
            etDescription = (EditText) view.findViewById(R.id.etDescription);

            btnGetPhoto = (ImageView) view.findViewById(R.id.btnGetPhoto);
            btnGetPhoto.setOnClickListener(this);

            btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
            btnSubmit.setOnClickListener(this);
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return true;
            }
        });
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnGetPhoto) {
            System.out.print(v.toString());
            final String[] items = {"Capture Image", "Choose from Gallery"};
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("Get Image From");
            ad.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(items[which].equals("Capture Image")) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                    } else if(items[which].equals("Choose from Gallery")) {
                        Intent takePicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(takePicture, 2);
                    }
                }
            });
            ad.show();
        }

        if(v.getId() == R.id.btnSubmit) {
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            if(title.equals("") && description.equals("") && imageFile == null) {
                Toast.makeText(getActivity(), "All Fields Required", Toast.LENGTH_SHORT).show();
            } else {
                ServerCal serverCal = new ServerCal();
                serverCal.execute("http://citizen.turpymobileapps.com/report.php", title, description, address);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            Bundle extra = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extra.get("data");
            Uri tempUri = getImageUri(getActivity(), imageBitmap);
            setImage(getRealPathFromURI(tempUri));
        }
        if(requestCode == 2) {
            Uri selectedImageUri = data.getData();
            setImage(getRealPathFromURI(selectedImageUri));
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void setImage(String image) {
        Log.d("Image Path", image);
        imageFile = new File(image).getAbsoluteFile();
        view = getView();
        Log.d("View", "" + view);
        if(imageFile.exists()){
            if(view != null) {
                btnGetPhoto = (ImageView) view.findViewById(R.id.btnGetPhoto);
                Bitmap imagePath = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                btnGetPhoto.setImageBitmap(imagePath);
            } else {
                Log.d("View is", "Not Found...");
            }
        }
    }

    public void showDialog(String msg, String type){
        AlertDialog.Builder dialog;
        String title = msg;
        dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);

        if(type.equals("fail")) {
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
                    etTitle.setText("");
                    etDescription.setText("");
                    btnGetPhoto.setImageDrawable(getResources().getDrawable(R.drawable.photo_button));
                    imageFile = null;
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
                multipartEntity.addPart("title", new StringBody(params[1]));
                multipartEntity.addPart("description", new StringBody(params[2]));
                multipartEntity.addPart("lat", new StringBody(String.valueOf(latitue)));
                multipartEntity.addPart("long", new StringBody(String.valueOf(longitude)));
                multipartEntity.addPart("addr", new StringBody(String.valueOf(params[3])));
                multipartEntity.addPart("image", new FileBody(imageFile));
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
                if (status.equals("Report successfully Submitted")) {
                    showDialog(status, "success");
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
