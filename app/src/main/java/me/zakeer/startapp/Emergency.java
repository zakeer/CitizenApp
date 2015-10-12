package me.zakeer.startapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * A simple {@link Fragment} subclass.
 */
public class Emergency extends Fragment implements View.OnClickListener {

    Double latitue, longitude;
    View view;

    TextView etPs, etPsNum, etAcp, etDcp, etPatrolNum, etHospital, etHospitalNum, etFire;

    public Emergency() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if(view != null) {
            MainActivity activity = (MainActivity)getActivity();
            latitue = activity.latitue;
            longitude = activity.longitude;
            etPs = (TextView) view.findViewById(R.id.policestationText);
            etPsNum = (TextView) view.findViewById(R.id.psNumberText);
            etAcp = (TextView) view.findViewById(R.id.acpText);
            etDcp = (TextView) view.findViewById(R.id.dcpText);
            etPatrolNum = (TextView) view.findViewById(R.id.dcpText);
            etHospital = (TextView) view.findViewById(R.id.hospitalText);
            etHospitalNum = (TextView) view.findViewById(R.id.hsText);
            etFire = (TextView) view.findViewById(R.id.fireText);

            etPsNum.setOnClickListener(this);
            etAcp.setOnClickListener(this);
            etDcp.setOnClickListener(this);
            etPatrolNum.setOnClickListener(this);
            etHospitalNum.setOnClickListener(this);
            etFire.setOnClickListener(this);

            ServerCal serverCal = new ServerCal();
            serverCal.execute("http://citizen.turpymobileapps.com/getemergency.php");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.isClickable()) {
            TextView textView = (TextView) view.findViewById(v.getId());
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+91" + textView.getText().toString().replace(" ", "").trim()));
            startActivity(callIntent);
        }
    }

    public void showDialog(String msg, String type) {
        AlertDialog.Builder dialog;
        String title = msg;
        dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);

        if (type.equals("fail")) {
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

    public class ServerCal extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(params[0]);
            HttpResponse response = null;

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            try {
                multipartEntity.addPart("lat", new StringBody(String.valueOf(latitue)));
                multipartEntity.addPart("long", new StringBody(String.valueOf(longitude)));

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
            try {
                JSONArray serverData = new JSONArray(s);
                if(serverData.length() > 0) {
                    JSONObject data = serverData.getJSONObject(0);

                    etPs.setText(data.getString("ps_name"));
                    etPsNum.setText(data.getString("std_code") + data.getString("ps_phone"));
                    etAcp.setText(data.getString("acp"));
                    etDcp.setText(data.getString("dcp"));
                    etPatrolNum.setText(data.getString("patrol"));
                    etHospital.setText(data.getString("hospital"));
                    etHospitalNum.setText(data.getString("std_code") + data.getString("hospital_phone"));
                    etFire.setText(data.getString("fire"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*try {
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
            }*/
        }

    }


}
