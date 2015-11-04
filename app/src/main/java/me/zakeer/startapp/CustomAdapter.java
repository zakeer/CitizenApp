package me.zakeer.startapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomAdapter extends BaseAdapter{
    final static String URL = "http://citizen.turpymobileapps.com/";
    private static LayoutInflater inflater = null;
    String[] result;
    Context context;
    ImageLoader imageLoader;
    double longitude, latitude;

    public CustomAdapter(OfficialActivity officialActivity, String[] prgmNameList, LatLng latLng) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=officialActivity;
        inflater = ( LayoutInflater )context.
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
            Log.d("Tile " + position, (String) singleRowData.get("title"));
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.view_report_list, null);
            holder.tvTitle =(TextView) rowView.findViewById(R.id.report_title);
            holder.tvLocation =(TextView) rowView.findViewById(R.id.report_location);
            holder.img= (ImageView) rowView.findViewById(R.id.report_image);
            holder.cb = (CheckBox) rowView.findViewById(R.id.cb);
            holder.cb.setTag(singleRowData.get("id"));
            holder.cb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

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
            rowView.setOnClickListener(new OnClickListener() {
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