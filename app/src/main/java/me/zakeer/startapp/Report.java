package me.zakeer.startapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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

    static final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_VIDEO_CAPTURE = 3;
    File imageFile, videoFile;
    Double latitue, longitude;
    String address = "";
    EditText etTitle, etDescription;
    Button btnSubmit;

    ImageView btnGetPhoto, btnGetVideo;
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

            btnGetVideo = (ImageView) view.findViewById(R.id.btnGetVideo);
            btnGetVideo.setOnClickListener(this);

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

        if (v.getId() == R.id.btnGetVideo) {
            System.out.print(v.toString());
            final String[] items = {"Record Video", "Choose from Gallery"};
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setTitle("Get Image From");
            ad.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (items[which].equals("Record Video")) {
                        Intent takeVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        String fName = "VideoFileName.mp4";
                        File f = new File(fName);
                        takeVideo.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(takeVideo, REQUEST_VIDEO_CAPTURE);
                    } else if (items[which].equals("Choose from Gallery")) {
                        Intent takePicture = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(takePicture, 4);
                    }
                }
            });
            ad.show();
        }

        if(v.getId() == R.id.btnSubmit) {
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            if (title.equals("") || description.equals("") || (imageFile == null && videoFile == null)) {
                if (title.equals("")) {
                    Toast.makeText(getActivity(), "Title Must be Required", Toast.LENGTH_SHORT).show();
                } else if (description.equals("")) {
                    Toast.makeText(getActivity(), "Password Must be Required", Toast.LENGTH_SHORT).show();
                } else if (imageFile == null && videoFile == null) {
                    Toast.makeText(getActivity(), "Attach Image or Video File", Toast.LENGTH_SHORT).show();
                }

            } else {
                ServerCal serverCal = new ServerCal();

                if (imageFile != null && videoFile != null) {
                    serverCal.execute("http://citizen.turpymobileapps.com/report.php", title, description, address, "image/video");
                } else if (imageFile != null) {
                    serverCal.execute("http://citizen.turpymobileapps.com/report.php", title, description, address, "image");
                } else if (videoFile != null) {
                    serverCal.execute("http://citizen.turpymobileapps.com/report.php", title, description, address, "video");
                }
                //serverCal.execute("http://citizen.turpymobileapps.com/report.php", title, description, address);
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

        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            String filePath = getVideoRealPathFromURI(data.getData());
            Bitmap tempUri = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
            setVideoThumbnail(tempUri);
            //Log.i("BitMap : ", String.valueOf(tempUri));
        }

        if (requestCode == 4) {
            Uri vid = data.getData();
            String videoPath = getVideoRealPathFromURI(vid);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
            setVideoThumbnail(bitmap);
            // Log.i("Video id", String.valueOf(videoFile));


        }

        if(requestCode == 2) {
            Uri selectedImageUri = data.getData();
            setImage(getRealPathFromURI(selectedImageUri));
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public String getVideoRealPathFromURI(Uri uri) {

        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            if (path != null) {
                videoFile = new File(path).getAbsoluteFile();
            }
            Log.i("Video Path", String.valueOf(videoFile));
            return path;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void setVideoThumbnail(Bitmap thumbnail) {
        btnGetVideo = (ImageView) view.findViewById(R.id.btnGetVideo);
        btnGetVideo.setImageBitmap(thumbnail);
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
        dialog = new AlertDialog.Builder((Report.this).getActivity());
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
                    videoFile = null;
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    public class ServerCal extends AsyncTask<String, String, String> {

        private ProgressDialog dialog = new ProgressDialog((Report.this).getActivity());

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
                if (params[4].equals("image/video")) {
                    multipartEntity.addPart("image", new FileBody(imageFile));
                    multipartEntity.addPart("video", new FileBody(videoFile));
                } else if (params[4].equals("image")) {
                    multipartEntity.addPart("image", new FileBody(imageFile));
                } else if (params[4].equals("video")) {
                    multipartEntity.addPart("video", new FileBody(videoFile));
                }

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
