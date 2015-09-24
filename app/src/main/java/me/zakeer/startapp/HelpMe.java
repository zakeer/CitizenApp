package me.zakeer.startapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
public class HelpMe extends Fragment implements View.OnClickListener {

    Double latitue, longitude;
    String address = "";

    View view;
    ImageView btnSend;

    public HelpMe() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_me, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if (view != null) {
            MainActivity activity = (MainActivity)getActivity();
            latitue = activity.latitue;
            longitude = activity.longitude;
            address = activity.address.trim();

            btnSend = (ImageView) view.findViewById(R.id.sendBtn);
            btnSend.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sendBtn) {
            ServerCal serverCal = new ServerCal();
            Toast.makeText(getActivity(), address, Toast.LENGTH_LONG).show();
            serverCal.execute("http://citizen.turpymobileapps.com/report.php");
        }
    }

    public class ServerCal extends AsyncTask<String, String, String> {

        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Sending...");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(params[0]);
            HttpResponse response = null;

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            try {
                multipartEntity.addPart("phone", new StringBody(String.valueOf("9533222116")));
                multipartEntity.addPart("place", new StringBody(String.valueOf(address)));
                multipartEntity.addPart("latitude", new StringBody(String.valueOf(latitue)));
                multipartEntity.addPart("longitude", new StringBody(String.valueOf(longitude)));
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
                if (status.equals("successfully Shared Emergency")) {
                    showDialog(status, "success");
                } else {
                    showDialog(status, "fail");
                    System.out.print("Fail to sharing Emergency");
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }
}
