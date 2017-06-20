package com.maks.farmfresh24;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.maks.farmfresh24.utils.AppPreferences;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRegister;
    private TextView link_signup, link_forgotpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmail= (EditText) findViewById(R.id.editTextEmail);
        link_signup= (TextView) findViewById(R.id.link_signup);
        link_forgotpass= (TextView) findViewById(R.id.link_forgotpass);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(this);
        link_signup.setOnClickListener(this);
        link_forgotpass.setOnClickListener(this);

    }

    private void registerUser(){

        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        new HttpAsyncTask().execute("{\"method\":\"login\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");

    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            if(validate()) {
                registerUser();
            }else{
                Toast.makeText(LoginActivity.this, "Username/password should not be empty.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v==link_signup){
            registerMe();
        }
        else if(v==link_forgotpass){
            forgotPassword();
        }
    }


    private void forgotPassword() {
        Intent intent=new Intent(getApplicationContext(),ForgotPassActivity.class);
        startActivity(intent);

    }

    private void registerMe() {
        Intent intent=new Intent(getApplicationContext(),RegistrationActivity.class);
        startActivity(intent);

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog d;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = new ProgressDialog(LoginActivity.this);
            d.setMessage("please wait...");
            d.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            return HttpUtils.requestWebService(Constants.WS_URL, "POST",urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(d!=null && d.isShowing()){
                d.dismiss();
            }

if(result!=null) {
    try {
        Log.e("", result);
        JSONObject json = new JSONObject(result);
        if (json.getString("result").equalsIgnoreCase("success")) {
            AppPreferences app = new AppPreferences(LoginActivity.this);
            Log.e("email", json.getString("user_email"));
            app.setEmail(json.getString("user_email"));
            app.setLogin(true);
            Log.e("name", json.getString("fname"));
            app.setFname(json.getString("fname"));
            app.setLogin(true);

            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(mainIntent);
            LoginActivity.this.finish();

        } else {
            Toast.makeText(LoginActivity.this, "" + json.getString("responseMessage"), Toast.LENGTH_SHORT).show();
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }
}
        }
    }

    private boolean validate(){
        if(editTextEmail.getText().toString().trim().equals(""))
            return false;
        else return !editTextPassword.getText().toString().trim().equals("");
    }
}
