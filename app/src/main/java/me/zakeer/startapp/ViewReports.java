package me.zakeer.startapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class ViewReports extends Fragment {

    View view;
    ListView listView;
    LinearLayout linearLayout;
    double latitude, longitude;
    LatLng latLng;

    public ViewReports() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_reports, container, false);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if(view != null) {
            OfficialActivity activity = (OfficialActivity)getActivity();
            listView = (ListView) view.findViewById(R.id.viewReportsList);

            latitude = activity.latitue;
            longitude = activity.longitude;
            latLng = new LatLng(latitude, longitude);

            Display display = activity.getWindowManager().getDefaultDisplay();
            int h = display.getHeight() - activity.h;
            linearLayout = (LinearLayout) view.findViewById(R.id.viewReportsLayout);
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = h;
            linearLayout.requestLayout();
            listView.setPadding(0, 0, 0, (int) (activity.h * 1.5));

            ServerCal serverCal = new ServerCal();
            serverCal.execute("http://citizen.turpymobileapps.com/getreports.php");
        }
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
            HttpGet getRequest = new HttpGet(params[0]);

            try {
                HttpResponse responseGET = client.execute(getRequest);
                HttpEntity resEntity = responseGET.getEntity();
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
                JSONArray serverData = new JSONArray(s);
                int dataCount = serverData.length();

                Log.d("Server Data", serverData.getString(0));
                String[] data = new String[dataCount];

                for(int i=0; i < serverData.length(); i++) {
                    data[i] = serverData.getString(i);
                }
                //String status = serverData.getString("message");
                Log.d("Data", String.valueOf(data.length));

                if (data.length > 0 && latLng != null) {
                    listView.setAdapter(new CustomAdapter((OfficialActivity) getActivity(), data, latLng));
                }
                /*if (status.equals("Report successfully Submitted")) {
                    showDialog(status, "success");
                } else {
                    showDialog(status, "fail");
                    System.out.print("login failled");
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
