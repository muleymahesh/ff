package com.maks.farmfresh24;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maks.farmfresh24.utils.AppPreferences;
import com.maks.farmfresh24.utils.ConnectionDetector;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText firstname, lastname, oldPass, password, confirmpass, mobileNo;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Change password");
        setSupportActionBar(toolbar);

        initView();
        setListeners();
    }

    private void initView() {
        oldPass = (EditText) findViewById(R.id.editTextOldPass);
        password = (EditText) findViewById(R.id.editTextPassword);
        confirmpass = (EditText) findViewById(R.id.editTextConfirmPassword);
        btnRegister = (Button) findViewById(R.id.buttonRegister);
    }


    private void setListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    String oldPwdTxt, emailTxt, pwdTxt, confirmTxt;
                    emailTxt = new AppPreferences(PasswordResetActivity.this).getEmail();
                    oldPwdTxt = oldPass.getText().toString();
                    pwdTxt = password.getText().toString();
                    confirmTxt = confirmpass.getText().toString();

                    if (pwdTxt.equals(confirmTxt)) {
                        String data = "{\"method\":\"change_password\",\"email\":\"" + emailTxt + "\",\"oldpass\":\"" + oldPwdTxt + "\",\"newpass\":\"" + confirmTxt + "\"}";
                        if (new ConnectionDetector(PasswordResetActivity.this).isConnectingToInternet()) {
                            new HttpAsyncTask().execute(data);
                        } else {
                            Toast.makeText(PasswordResetActivity.this, "You are offline!.", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(PasswordResetActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PasswordResetActivity.this, "All fields are mandetory.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validation() {

        if (password.getText().toString().trim().equals(""))
            return false;
        else return !confirmpass.getText().toString().trim().equals("");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog d;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = new ProgressDialog(PasswordResetActivity.this);
            d.setMessage("please wait...");
            d.show();
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
            try {
                Log.e("", result);
                JSONObject json = new JSONObject(result);
                if (json.getString("result").equalsIgnoreCase("success")) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(PasswordResetActivity.this);
                    alert.setMessage(json.getString("responseMessage"));
                    alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert.show();
                } else {

                    Toast.makeText(PasswordResetActivity.this, "" + json.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}

