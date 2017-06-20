package com.maks.farmfresh24;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.maks.farmfresh24.dbutils.SQLiteUtil;
import com.maks.farmfresh24.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedbackActivity extends AppCompatActivity {

    TextView TextViewTitle;

    EditText etname,etemail,etfeedback;
    Button btnsendfeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        etname = (EditText)findViewById(R.id.EditTextName);
        etemail =(EditText)findViewById(R.id.EditTextEmail);
        etfeedback=(EditText)findViewById(R.id.EditTextFeedbackBody);

        btnsendfeedback = (Button)findViewById(R.id.ButtonSendFeedback);

            initToolbar();
        // Button click Listener

        
    }

    private void initToolbar() {
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Feedback");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }



public void sendFeedback(View v){

    if(etname.getText().toString().isEmpty()){
        etname.setError("Required field");
        etname.requestFocus();
        return;
    }

    if(etemail.getText().toString().isEmpty()){
        etemail.setError("Required field");
        etemail.requestFocus();
        return;
    }

    if(etfeedback.getText().toString().isEmpty()){
        etfeedback.setError("Required field");
        etfeedback.requestFocus();
        return;
    }

    String name = etname.getText().toString();
    String email = etemail.getText().toString();
    String feedback = etfeedback.getText().toString();


    String req="{\"method\":\"add_feedback\",\"name\":\""+name+"\",\"email\":\""+email+"\",\"detail\":\""+feedback+"\"}";


    Log.e("request",req);

    new HttpAsyncTask().execute(Constants.WS_URL,req);

}

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(FeedbackActivity.this);
            pd.setMessage("Loading...");
            pd.show();
        }


        @Override
        protected String doInBackground(String... ulr) {
            Response response = null;

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(JSON, ulr[1]);
            Request request = new Request.Builder()
                    .url (ulr[0])
                    .post(body)
                    .build();

            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            if(s!=null){
                Log.e("response", s);
                try {
                    JSONObject json = new JSONObject(s);
                    if(json.getString("result").equalsIgnoreCase("success")){
                        new SQLiteUtil().emptyCart(FeedbackActivity.this);

                        AlertDialog.Builder alert = new AlertDialog.Builder(FeedbackActivity.this);
                        alert.setMessage("Your feedback submitted successfully");
                        alert.setTitle("Thank you !");
                        alert.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FeedbackActivity.this.finish();

                            }
                        });
                        alert.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }        // onPostExecute displays the results of the AsyncTask.

    }


}
