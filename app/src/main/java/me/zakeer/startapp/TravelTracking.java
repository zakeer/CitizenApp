package me.zakeer.startapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
public class TravelTracking extends Fragment {

    View view;

    ListView listView;
    LinearLayout linearLayout;

    public TravelTracking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_travel_tracking, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view = getView();
        if(view != null) {
            OfficialActivity activity = (OfficialActivity)getActivity();
            listView = (ListView) view.findViewById(R.id.viewTravelList);

            Display display = activity.getWindowManager().getDefaultDisplay();
            int h = display.getHeight() - activity.h;

            linearLayout = (LinearLayout) view.findViewById(R.id.viewTravelLayout);

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = h;
            linearLayout.requestLayout();
            listView.setPadding(0, 0, 0, (int) ((int) activity.h * 1.5));

            ServerCal serverCal = new ServerCal();
            serverCal.execute("http://citizen.turpymobileapps.com/gettracks.php");
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

                if(data.length > 0) {
                    listView.setAdapter(new MyAdapter((OfficialActivity) getActivity(), data));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public class MyAdapter extends BaseAdapter {
        String[] result;
        Context context;
        private LayoutInflater inflater=null;
        ImageLoader imageLoader;

        final static String URL = "http://citizen.turpymobileapps.com/";

        public MyAdapter(OfficialActivity officialActivity, String[] prgmNameList) {
            // TODO Auto-generated constructor stub
            result=prgmNameList;
            context=officialActivity;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ImageLoader(context);
        }

        public class Holder
        {
            TextView tvPhone;
            TextView tvVehicle;
            TextView tvStartLocation;
            TextView tvUpdateLocation;
            TextView tvUpdateTime;
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
                JSONObject singleRowData = new JSONObject(result[position]);
                //Log.d("Tile " + position, (String) singleRowData.get("title"));
                Holder holder=new Holder();
                View rowView;
                rowView = inflater.inflate(R.layout.view_travel_list, null);
                holder.tvPhone =(TextView) rowView.findViewById(R.id.tvPhone);
                holder.tvVehicle =(TextView) rowView.findViewById(R.id.tvVehicle);
                holder.tvStartLocation= (TextView) rowView.findViewById(R.id.tvStartLocation);
                holder.tvUpdateLocation= (TextView) rowView.findViewById(R.id.tvUpdateLocation);
                holder.tvUpdateTime= (TextView) rowView.findViewById(R.id.tvUpdateTime);

                holder.tvPhone.setText((String) singleRowData.get("mobile_no"));
                holder.tvVehicle.setText((String) singleRowData.get("vehicle"));
                holder.tvStartLocation.setText(((String) singleRowData.get("place")).replace("\n", " "));
                holder.tvUpdateLocation.setText(((String) singleRowData.get("updated_place")).replace("\n", " "));
                holder.tvUpdateTime.setText((String) singleRowData.get("updated_at"));

                //imageLoader.DisplayImage(URL + singleRowData.get("image"), holder.img);
                //holder.img.setImageResource();
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(context, "You Clicked ", Toast.LENGTH_LONG).show();
                    }
                });
                return rowView;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return inflater.inflate(R.layout.view_travel_list, null);

        }

    }

}
