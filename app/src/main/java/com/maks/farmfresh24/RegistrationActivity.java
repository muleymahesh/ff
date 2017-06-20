package com.maks.farmfresh24;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maks.farmfresh24.utils.ConnectionDetector;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.HttpUtils;
import com.maks.farmfresh24.utils.TypefaceSpan;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstname,lastname,email,password,confirmpass,mobileNo;
    private Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar    toolbar = (Toolbar) findViewById(R.id.toolbar);
        SpannableString s = new SpannableString("New user registration");
        s.setSpan(new TypefaceSpan(this, "Jacquard.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setTitle(s);
        setSupportActionBar(toolbar);


        initView();
        setListeners();
    }

    private void initView() {
        firstname=(EditText)findViewById(R.id.editTextFirstName);
        lastname=(EditText)findViewById(R.id.editTextLastName);
        email=(EditText)findViewById(R.id.editTextEmail);
        password=(EditText)findViewById(R.id.editTextPassword);
        confirmpass=(EditText)findViewById(R.id.etConfirmPassword);
        mobileNo=(EditText)findViewById(R.id.editTextMobile);
        btnRegister=(Button)findViewById(R.id.buttonRegister);
    }


    private void setListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    String fnameTxt,lnameTxt,emailTxt,pwdTxt,confirmTxt,mobileNoTxt;
                    fnameTxt=firstname.getText().toString();
                    lnameTxt=lastname.getText().toString();
                    emailTxt=email.getText().toString();
                    pwdTxt=password.getText().toString();
                    confirmTxt=confirmpass.getText().toString();
                    mobileNoTxt=mobileNo.getText().toString();
                    String deviceToken = "";

                    if(pwdTxt.equals(confirmTxt)) {
                        String data = "{\"method\":\"signup\",\"fname\":\"" + fnameTxt + "\",\"lname\":\"" + lnameTxt +
                                        "\",\"email\":\"" + emailTxt + "\",\"mobile\":\"" + mobileNoTxt + "\",\"device_token\":\"" +
                                        deviceToken + "\",\"password\":\"" + pwdTxt + "\"}";
if(new ConnectionDetector(RegistrationActivity.this).isConnectingToInternet()) {
    new HttpAsyncTask().execute(data);
}else{
    Toast.makeText(RegistrationActivity.this, "You are offline!.", Toast.LENGTH_SHORT).show();

}
                    }else{
                        Toast.makeText(RegistrationActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistrationActivity.this, "All fields are mandetory.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean validation() {
        if(firstname.getText().toString().trim().equals(""))
            return false;
        else if(lastname.getText().toString().trim().equals(""))
            return false;
        else if(email.getText().toString().trim().equals(""))
            return false;
        else if(password.getText().toString().trim().equals(""))
            return false;
        else if(confirmpass.getText().toString().trim().equals(""))
            return false;
        else return !mobileNo.getText().toString().trim().equals("");
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog d;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = new ProgressDialog(RegistrationActivity.this);
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
            if(d!=null && d.isShowing()){
                d.dismiss();
            }
            try {
                Log.e("", result);
                JSONObject json = new JSONObject(result);
                if(json.getString("result").equalsIgnoreCase("success")){

                    AlertDialog.Builder alert = new AlertDialog.Builder(RegistrationActivity.this);
                    alert.setMessage(json.getString("responseMessage"));
                    alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RegistrationActivity.this.finish();
                        }
                    });
                    alert.show();
                }else {

                    Toast.makeText(RegistrationActivity.this, ""+json.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
