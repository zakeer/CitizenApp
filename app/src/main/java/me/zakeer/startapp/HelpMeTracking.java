package me.zakeer.startapp;


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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpMeTracking extends Fragment {

    View view;

    ListView listView;
    LinearLayout linearLayout;

    Button btnSubmit;
    String reports = "";

    public HelpMeTracking() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_me_tracking, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if(view != null) {
            OfficialActivity activity = (OfficialActivity)getActivity();
            btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
            btnSubmit.setClickable(false);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!reports.equals("")) {
                        ServerCal saveReports = new ServerCal();
                        saveReports.execute("POST", "http://citizen.turpymobileapps.com/sos.php");
                    }
                }
            });
            listView = (ListView) view.findViewById(R.id.viewHelpList);

            Display display = activity.getWindowManager().getDefaultDisplay();
            int h = display.getHeight() - activity.h;

            linearLayout = (LinearLayout) view.findViewById(R.id.viewHelpLayout);

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            //params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            //params.height = h;
            linearLayout.requestLayout();
            listView.setPadding(0, 0, 0, (int) (activity.h * 1.5));

            ServerCal serverCal = new ServerCal();
            serverCal.execute("GET", "http://citizen.turpymobileapps.com/getsos.php");
        }

    }

    public class ServerCal extends AsyncTask<String, String, String> {

        String request = "GET";
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
            HttpGet getRequest = new HttpGet(params[1]);
            HttpPost postRequest = new HttpPost(params[1]);
            HttpResponse responseGET = null;
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

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
                        Toast.makeText(getActivity(), "" + saveItems + " Tracks are Saved", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "Fail to Save Tracks ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONArray serverData = new JSONArray(s);
                    int dataCount = serverData.length();

                    Log.d("Server Data", serverData.getString(0));
                    String[] data = new String[dataCount];

                    for (int i = 0; i < serverData.length(); i++) {
                        data[i] = serverData.getString(i);
                    }
                    //String status = serverData.getString("message");
                    Log.d("Data", String.valueOf(data.length));

                    if (data.length > 0) {
                        listView.setAdapter(new MyAdapter((OfficialActivity) getActivity(), data));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public class MyAdapter extends BaseAdapter {
        final static String URL = "http://citizen.turpymobileapps.com/";
        String[] result;
        Context context;
        ImageLoader imageLoader;
        private LayoutInflater inflater = null;

        public MyAdapter(OfficialActivity officialActivity, String[] prgmNameList) {
            // TODO Auto-generated constructor stub
            result=prgmNameList;
            context=officialActivity;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ImageLoader(context);
        }

        @Override
        public int getCount() {
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            try {
                final JSONObject singleRowData = new JSONObject(result[position]);
                //Log.d("Tile " + position, (String) singleRowData.get("title"));
                Holder holder=new Holder();
                View rowView;
                rowView = inflater.inflate(R.layout.view_help_me_list, null);
                holder.tvPhone =(TextView) rowView.findViewById(R.id.tvPhone);
                holder.tvPlace =(TextView) rowView.findViewById(R.id.tvPlace);

                holder.tvPhone.setText((String) singleRowData.get("phone"));
                holder.tvPlace.setText(((String) singleRowData.get("place")).replace("\n", " "));

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


                //imageLoader.DisplayImage(URL + singleRowData.get("image"), holder.img);
                //holder.img.setImageResource();
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent resultIntent = new Intent(context, ResultActivity.class);
                        resultIntent.putExtra("activity", "view_help");
                        resultIntent.putExtra("server_data", singleRowData.toString());
                        startActivityForResult(resultIntent, 1);
                    }
                });
                return rowView;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return inflater.inflate(R.layout.view_travel_list, null);

        }

        public class Holder {
            TextView tvPhone;
            TextView tvPlace;
            CheckBox cb;
        }

    }


}
