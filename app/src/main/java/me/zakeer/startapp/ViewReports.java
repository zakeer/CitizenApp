package me.zakeer.startapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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


public class ViewReports extends Fragment {

    View view;
    ListView listView;
    LinearLayout linearLayout;
    double latitude, longitude;
    LatLng latLng;
    String reports = "";

    int reportLoads = 0;
    String[] reportsData = new String[reportLoads + 10];

    Button btnSubmit, btnLoadMore;

    public ViewReports() {
        // Required empty public constructor
    }

    public void checkReports(String tag) {

        if (reports.indexOf(tag) == -1) {
            reports += "," + tag;
        } else {
            //Log.d("Index of Tag", String.valueOf(reports.indexOf(tag)));
            reports = reports.replace("," + tag, "");
        }

        if (!reports.equals("") && btnSubmit != null) {
            btnSubmit.setClickable(true);
            Drawable image = getResources().getDrawable(R.drawable.bg_report);
            btnSubmit.setBackground(image);
        } else {
            btnSubmit.setClickable(false);
            Drawable image = getResources().getDrawable(R.drawable.disable_bg);
            btnSubmit.setBackground(image);

        }

        //Log.d("Report String : ", reports);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reportLoads = 0;
        reportsData = new String[reportLoads + 10];
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
        reportLoads = 0;
        reportsData = new String[reportLoads + 10];
        if(view != null) {
            OfficialActivity activity = (OfficialActivity)getActivity();
            btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
            btnSubmit.setClickable(false);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!reports.equals("")) {
                        ServerCal saveReports = new ServerCal();
                        saveReports.execute("POST", "http://citizen.turpymobileapps.com/report.php?");
                    }
                }
            });

            btnLoadMore = (Button) view.findViewById(R.id.btnLoadMore);
            btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportLoads += 10;
                    ServerCal loadReports = new ServerCal();
                    loadReports .execute("GET", "http://citizen.turpymobileapps.com/getreports.php?start=" + reportLoads);
                }
            });

            listView = (ListView) view.findViewById(R.id.viewReportsList);

            latitude = activity.latitue;
            longitude = activity.longitude;
            latLng = new LatLng(latitude, longitude);

            Display display = activity.getWindowManager().getDefaultDisplay();
            int h = display.getHeight() - activity.h;
            linearLayout = (LinearLayout) view.findViewById(R.id.viewReportsLayout);
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            //params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            //params.height = h;
            linearLayout.requestLayout();
            listView.setPadding(0, 0, 0, (int) (activity.h * 1.5));

            ServerCal serverCal = new ServerCal();
            serverCal.execute("GET", "http://citizen.turpymobileapps.com/getreports.php?start=" + reportLoads);
        }
    }



    @Override
    public void onPause() {
        reportLoads = 0;
        reportsData = new String[reportLoads + 10];
        super.onPause();
    }

    public class ServerCal extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        private String request = "GET";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet(params[1]);
            HttpPost postRequest = new HttpPost(params[1]);
            HttpResponse responseGET = null;
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            Log.d("URl:", params[1]);


            try {
                if (params[0].equals("GET")) {
                    responseGET = client.execute(getRequest);
                }

                if (params[0].equals("POST")) {
                    request = "POST";
                    multipartEntity.addPart("user", new StringBody("7898"));
                    multipartEntity.addPart("report_id", new StringBody(reports));
                    multipartEntity.addPart("submit", new StringBody("1"));

                    postRequest.setEntity(multipartEntity);
                    responseGET = client.execute(postRequest);
                }

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

            if (request.equals("POST")) {
                try {
                    JSONObject serverData = new JSONObject(s);
                    if (serverData.get("message").equals("ok")) {
                        int saveItems = (int) serverData.get("total");
                        Toast.makeText(getActivity(), "" + saveItems + " reports are Saved", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "Fail to Save reports ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONArray serverData = new JSONArray(s);
                    int dataCount = serverData.length();
                    Log.d("Server Data", dataCount+"");

                    String[] oldData = new String[reportLoads];
                    for (int i = 0; i < oldData.length; i++) {
                        oldData[i] = reportsData[i];
                    }
                    reportsData = new String[reportLoads + dataCount];
                    Log.d("reports Data", reportsData.length +"");

                    if(oldData.length > 0) {
                        for (int i = 0; i < oldData.length; i++) {
                            reportsData[i] = oldData[i];
                        }
                    }

                    if(reportLoads == 0) {
                        for (int j = reportLoads; j < serverData.length(); j++) {
                            reportsData[j] = serverData.getString(j);
                        }
                    } else {
                        for (int j = reportLoads; j < reportLoads + dataCount; j++) {
                            //Log.d("J : -- ", String.valueOf(j));
                            reportsData[j] = serverData.getString(j - reportLoads);
                        }
                    }


                    //String status = serverData.getString("message");
                    //Log.d("Data", String.valueOf(data.length));

                    if (reportsData.length > 0 && latLng != null) {
                        listView.setAdapter(new ReportAdapter((OfficialActivity) getActivity(), reportsData, latLng));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public class ReportAdapter extends BaseAdapter {
        final static String URL = "http://citizen.turpymobileapps.com/";
        String[] result;
        Context context;
        ImageLoader imageLoader;
        double longitude, latitude;
        private LayoutInflater inflater = null;

        public ReportAdapter(OfficialActivity officialActivity, String[] prgmNameList, LatLng latLng) {
            // TODO Auto-generated constructor stub
            result = prgmNameList;
            context = officialActivity;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ImageLoader(context);
            this.latitude = latLng.latitude;
            this.longitude = latLng.longitude;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            try {
                final JSONObject singleRowData = new JSONObject(result[position]);
                Holder holder = new Holder();
                View rowView;
                rowView = inflater.inflate(R.layout.view_report_list, null);
                holder.tvTitle = (TextView) rowView.findViewById(R.id.report_title);
                holder.tvLocation = (TextView) rowView.findViewById(R.id.report_location);
                holder.img = (ImageView) rowView.findViewById(R.id.report_image);
                holder.cb = (CheckBox) rowView.findViewById(R.id.cb);
                holder.cb.setTag(singleRowData.get("id"));

                holder.cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = (String) v.getTag();
                        //Log.d("Click Tag is : " ,tag);
                        checkReports(tag);
                    }
                });

                if (singleRowData.get("checked").equals("1")) {
                    holder.cb.setChecked(true);
                    holder.cb.setClickable(false);
                }

                holder.tvTitle.setText((String) singleRowData.get("title"));
                holder.tvLocation.setText((String) singleRowData.get("address"));

                imageLoader.DisplayImage(URL + singleRowData.get("image"), holder.img);
                //holder.img.setImageResource();
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent resultIntent = new Intent(context, ResultActivity.class);
                        resultIntent.putExtra("longitude", longitude);
                        resultIntent.putExtra("latitude", latitude);
                        resultIntent.putExtra("activity", "view_reports");
                        resultIntent.putExtra("server_data", singleRowData.toString());
                        ((Activity) context).startActivityForResult(resultIntent, 1);
                    }
                });
                return rowView;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return inflater.inflate(R.layout.view_report_list, null);

        }

        public class Holder {
            TextView tvTitle;
            TextView tvLocation;
            ImageView img;
            CheckBox cb;
        }
    }
}
