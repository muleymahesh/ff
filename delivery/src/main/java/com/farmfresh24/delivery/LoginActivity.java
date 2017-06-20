package com.farmfresh24.delivery;

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

import com.farmfresh24.delivery.utils.AppPreferences;
import com.farmfresh24.delivery.utils.Constants;
import com.farmfresh24.delivery.utils.HttpUtils;

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
        AppPreferences app = new AppPreferences(LoginActivity.this);
        boolean status = true;
        if (app.getEmail() != null) {
            if (app.getEmail().length() > 0) {
                status = false;
            } else {
                status = true;
            }
        }
        if (!status) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            setContentView(R.layout.activity_login);

            editTextPassword = (EditText) findViewById(R.id.editTextPassword);
            editTextEmail = (EditText) findViewById(R.id.editTextEmail);
            link_signup = (TextView) findViewById(R.id.link_signup);
            link_forgotpass = (TextView) findViewById(R.id.link_forgotpass);

            buttonRegister = (Button) findViewById(R.id.buttonRegister);

            buttonRegister.setOnClickListener(this);
            link_signup.setOnClickListener(this);
            link_forgotpass.setOnClickListener(this);
        }
    }

    private void registerUser() {

        final String password = editTextPassword.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();

        new HttpAsyncTask().execute("{\"method\":\"login\",\"phone\":\"" + email + "\",\"password\":\"" + password + "\"}");

    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            if (validate()) {
                registerUser();
            } else {
                Toast.makeText(LoginActivity.this, "Username/password should not be empty.", Toast.LENGTH_SHORT).show();
            }
        } else if (v == link_signup) {
            registerMe();
        } else if (v == link_forgotpass) {
            forgotPassword();
        }
    }


    private void forgotPassword() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    private void registerMe() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog d;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = new ProgressDialog(LoginActivity.this);
            d.setMessage("please wait...");
            if (d.isShowing()) {
                d.dismiss();
            } else {
                d.show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {

            return HttpUtils.requestWebService(Constants.WS_URL, "POST", urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (d != null && d.isShowing()) {
                d.dismiss();
            }

            if (result != null) {
                try {
                    Log.e("", result);
                    JSONObject json = new JSONObject(result);
                    if (json.getString("result").equalsIgnoreCase("success")) {
                        AppPreferences app = new AppPreferences(LoginActivity.this);
                        Log.e("email", json.getString("del_boy"));
                        app.setEmail(json.getString("del_boy"));
                        app.setLogin(true);
                        Log.e("name", json.getString("phone"));
                        app.setFname(json.getString("phone"));
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

    private boolean validate() {
        if (editTextEmail.getText().toString().trim().equals(""))
            return false;
        else return !editTextPassword.getText().toString().trim().equals("");
    }
}
