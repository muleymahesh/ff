package com.maks.farmfresh24;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.maks.farmfresh24.dbutils.SQLiteUtil;
import com.maks.farmfresh24.model.Address;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.TypefaceSpan;
import com.maks.farmfresh24.utils.Utils;


public class AddressActivity extends AppCompatActivity {


    private EditText txtFName,txtLName, txtCountry,txtAddr,txtMobile,txtState;
    Button orderBtn;
    Spinner spnArea;//,spnPincode;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

initView();
    }

    private void initView() {

        txtFName=(EditText)findViewById(R.id.txtFname);
        txtLName=(EditText)findViewById(R.id.txtLname);
//        txtemail=(EditText)findViewById(R.id.txtEmail);
        txtMobile=(EditText)findViewById(R.id.txtPhone);
        txtAddr=(EditText)findViewById(R.id.txtAddr);
        txtState=(EditText)findViewById(R.id.txtAddr);
        txtCountry=(EditText)findViewById(R.id.txtLandmark);
        //spnPincode=(Spinner)findViewById(R.id.txtZipcode);
        orderBtn=(Button)findViewById(R.id.btnSave);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        spnArea=(Spinner)findViewById(R.id.spinner);
        spnArea.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, Constants.inclusions));
        //spnPincode.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, Constants.pincodes));
        initToolbar();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            SpannableString s = new SpannableString("Add new address");
            s.setSpan(new TypefaceSpan(this, "Jacquard.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            toolbar.setTitle(s);
            setSupportActionBar(toolbar);
        }
        }


        private void setFonts() {
//        txtemail.setTypeface(Utils.setLatoFontBold(this));
        txtFName.setTypeface(Utils.setLatoFontBold(this));
        txtLName.setTypeface(Utils.setLatoFontBold(this));
        txtState.setTypeface(Utils.setLatoFontBold(this));
        txtCountry.setTypeface(Utils.setLatoFontBold(this));
//        txtPincode.setTypeface(Utils.setLatoFontBold(this));
        txtMobile.setTypeface(Utils.setLatoFontBold(this));
        orderBtn.setTypeface(Utils.setLatoFontBold(this));
    }

    public void validate(){


        final String fName = txtFName.getText().toString();
        final String lname = txtLName.getText().toString();
//        final String email = txtemail.getText().toString();
        final String landmark = txtCountry.getText().toString();
        final String area = spnArea.getSelectedItem().toString();
        final String mobile = txtMobile.getText().toString();
        final String addr = txtAddr.getText().toString();
        //final String zipcode = spnPincode.getSelectedItem().toString();

        if(fName.isEmpty()){
            txtFName.requestFocus();
            txtFName.setError("First name required");
            return;
        }
        if(lname.isEmpty()){
            txtLName.requestFocus();
            txtLName.setError("Last name required");
            return;
        }

        if(addr.isEmpty()){
            txtAddr.requestFocus();
            txtAddr.setError("Address details required");
            return;
        }
        if(mobile.isEmpty()){
            txtMobile.requestFocus();
            txtMobile.setError("Mobile name required");

            return;
        }
        /*if(zipcode.isEmpty() ){
            txtPincode.requestFocus();
            txtPincode.setError("Pincode required");

            return;
        }
        if(!TextUtils.isDigitsOnly(zipcode) || TextUtils.isEmpty(zipcode)){
            txtPincode.requestFocus();
            txtPincode.setError("Invalid Pincode");

            return;
        }*/

        Address address = new Address();
        address.setFname(fName);
        address.setLname(lname);
//        address.setEmail();
        address.setPhone(mobile);
        address.setArea(area);
        address.setAddr(addr);
        address.setLandmark(landmark);
        address.setZipcode("0");

        try {

            new SQLiteUtil().addAddress(address, this);
            Toast.makeText(AddressActivity.this, "Address added!!!", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){

            Toast.makeText(AddressActivity.this, "Error occured!", Toast.LENGTH_SHORT).show();

        }

    }
}
