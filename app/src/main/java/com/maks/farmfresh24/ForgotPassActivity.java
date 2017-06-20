package com.maks.farmfresh24;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.maks.farmfresh24.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

    }

    public void onClick(View v){
    String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();

        String mobile = ((EditText) findViewById(R.id.editTextMobile)).getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email id required!", Toast.LENGTH_SHORT).show();
       return;
        }
        if(!email.contains("@")){
            Toast.makeText(this, "Email id not valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(mobile)){
            Toast.makeText(this, "Mobile number required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!TextUtils.isDigitsOnly(mobile) || mobile.length()<10){
            Toast.makeText(this, "Mobile number not valid", Toast.LENGTH_SHORT).show();
            return;
        }

        new ProductTask().execute(Constants.WS_URL,"{\"method\":\"forgot_password\",\"email\":\""+email+"\",\"mobile\":\""+mobile+"\"}");
    }

    class ProductTask extends AsyncTask<String, Void,String> {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd= new ProgressDialog(ForgotPassActivity.this);
            pd.setMessage("Loading...");
            pd.show();
            pd.setCancelable(false);
        }

       @Override
        protected String doInBackground(String... ulr) {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Log.e("request",ulr[1]);
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }

            if(result!=null) {
                try {
                    Log.e("", result);
                    JSONObject json = new JSONObject(result);
                    if (json.getString("result").equalsIgnoreCase("success")) {

                        AlertDialog.Builder  alert = new AlertDialog.Builder(ForgotPassActivity.this);
                        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
                            }
                        });
                        alert.setMessage("New password sent to your email id.");
                        alert.setTitle("FarmFresh24");
                        alert.show();
                    } else {
                        Toast.makeText(ForgotPassActivity.this, "" + json.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }        }
    }

}
}