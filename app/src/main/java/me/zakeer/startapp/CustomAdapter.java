package me.zakeer.startapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomAdapter extends BaseAdapter{
    String[] result;
    Context context;
    private static LayoutInflater inflater=null;
    ImageLoader imageLoader;

    final static String URL = "http://citizen.turpymobileapps.com/";

    public CustomAdapter(OfficialActivity officialActivity, String[] prgmNameList) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=officialActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(context);
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

    public class Holder
    {
        TextView tvTitle;
        TextView tvLocation;
        ImageView img;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        try {
            JSONObject singleRowData = new JSONObject(result[position]);
            Log.d("Tile " + position, (String) singleRowData.get("title"));
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.view_report_list, null);
            holder.tvTitle =(TextView) rowView.findViewById(R.id.report_title);
            holder.tvLocation =(TextView) rowView.findViewById(R.id.report_location);
            holder.img= (ImageView) rowView.findViewById(R.id.report_image);

            holder.tvTitle.setText((String) singleRowData.get("title"));
            holder.tvLocation.setText((String) singleRowData.get("address"));

            imageLoader.DisplayImage(URL + singleRowData.get("image"), holder.img);
            //holder.img.setImageResource();
            rowView.setOnClickListener(new OnClickListener() {
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

        return inflater.inflate(R.layout.view_report_list, null);

    }

}