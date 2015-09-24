package me.zakeer.startapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class UserLogin extends Activity {

    Menu menu;

    Button loginBtn;
    ImageView logo;
    EditText userName, password;
    TextView reg, tvLoginText;

    AlertDialog.Builder dialog;

    int loginState = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_citizen);
        tvLoginText = (TextView) findViewById(R.id.tvLoginText);
        logo = (ImageView) findViewById(R.id.login_logo);

        if(isNetworkAvailable(this)) {
            //showDialog();
        } else {
            showDialog("Internet Data Required", "net");
        }

        userName = (EditText) findViewById(R.id.etusername);
        password = (EditText) findViewById(R.id.etPassword);


        loginBtn = (Button) findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userName.getText().toString();
                String paswrd = password.getText().toString();
                loginCheck login = new loginCheck();
                if(username.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                } else {
                    login.execute("http://citizen.turpymobileapps.com/login.php", "police", "password");
                }

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (loginState == 1) {
                changeLayout();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_user_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.official_login) {
            changeLayout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeLayout(){
        loginState = (loginState == 1) ? 0 : 1;
        if(loginState == 1) {
            tvLoginText.setText("Official Login");
            Drawable image = getResources().getDrawable(R.drawable.police_login);
            logo.setImageDrawable(image);
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.user_login));
        } else {
            tvLoginText.setText("User Login");
            Drawable image = getResources().getDrawable(R.drawable.citizen_logo);
            logo.setImageDrawable(image);
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.official_login));
        }
    }

    public class loginCheck extends AsyncTask<String, String, String> {

        private ProgressDialog dialog = new ProgressDialog(UserLogin.this);

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

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("username", params[1]));
            param.add(new BasicNameValuePair("password", params[2]));
            try {
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(param, HTTP.UTF_8);
                //postRequest.setHeader("host",params[1]);
                postRequest.setEntity(ent);
                HttpResponse responsePOST = client.execute(postRequest);
                HttpEntity resEntity = responsePOST.getEntity();
                return (resEntity != null) ? EntityUtils.toString(resEntity) : null;

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
            dialog.dismiss();
            try {
                JSONObject serverData = new JSONObject(s);
                String status = serverData.getString("message");
                if (status.equals("login successful")) {
                    if(loginState == 1) {
                        Intent intent = new Intent(getApplicationContext(), OfficialActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                } else {
                    showDialog("login failled", "fail");
                    System.out.print("login failled");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showDialog(String msg, String type){
        String title = msg;
        dialog = new AlertDialog.Builder(UserLogin.this);
        dialog.setIcon(R.drawable.alert);
        dialog.setTitle(title);

        if(type.equals("net")) {
            dialog.setCancelable(false);
            dialog.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setPositiveButton("Enabled Network", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            });
        } else {
            dialog.setCancelable(true);
            dialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }



    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobileNetState = connectivityManager.getNetworkInfo(0).getState();
        NetworkInfo.State wifiState = connectivityManager.getNetworkInfo(1).getState();

        if (wifiState == NetworkInfo.State.CONNECTED) {
            return true;
        } else if ((wifiState == NetworkInfo.State.DISCONNECTED || wifiState == NetworkInfo.State.UNKNOWN)
                && mobileNetState == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }
}
