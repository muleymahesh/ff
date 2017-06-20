package com.maks.farmfresh24;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.maks.farmfresh24.ccavenuepayment.Utility.AvenuesParams;
import com.maks.farmfresh24.ccavenuepayment.Utility.ServiceUtility;
import com.maks.farmfresh24.ccavenuepayment.WebViewActivity;
import com.maks.farmfresh24.dbutils.SQLiteUtil;
import com.maks.farmfresh24.model.Address;
import com.maks.farmfresh24.model.CartList;
import com.maks.farmfresh24.model.DiscountItem;
import com.maks.farmfresh24.model.ShoppingCart;
import com.maks.farmfresh24.utils.AppPreferences;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.TypefaceSpan;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlaceOrderActivity extends AppCompatActivity {

    public static final String TAG = PlaceOrderActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TextView txtDate;
    private TextView txtAmt;
    private TextView txtDeliveryCharges, txtAmountToPay;
    Button orderBtn, btnAddr;
    DiscountItem discountItem;
    Spinner spnTimeSlot;
    //    ,spnPaymentType;
    ArrayList<ShoppingCart> list;
    String amount, selected_date;
    private boolean isOnline = false;
    private boolean isWallet = false;
    float discountedAmount = 0;
    String walletAmount = "0";

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;
    private RadioGroup radioGroupPaymentMode;


    ArrayList<Address> addresses = new ArrayList<>();

    /* Payment gateway*/
    private String merchantKey, userCredentials;
    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;
    // This sets the configuration
    private PayuConfig payuConfig;
    // Used when generating hash from SDK
    private PayUChecksum checksum;
    private AppPreferences pref;
    private LinearLayout layoutPayment;
    private TextView textViewWalletPayment;
    StringBuilder discountedApplied = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        initToolbar();
        initView();
        pref = new AppPreferences(PlaceOrderActivity.this);
        //getWalletAmount();
        getData();

        setFonts();

        setListeners();

        /*//TODO Must write this code if integrating One Tap payments
        OnetapCallback.setOneTapCallback(PlaceOrderActivity.this);
        //TODO Must write below code in your activity to set up initial context for PayU
        Payu.setInstance(this);
        // lets tell the people what version of sdk we are using
        PayUSdkDetails payUSdkDetails = new PayUSdkDetails();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getData();
        addresses.clear();
        addresses.addAll(new SQLiteUtil().getAddressList(this));
        ListView addrlist = (ListView) findViewById(R.id.listView);
        MyAdapter adapter = new MyAdapter(this, addresses);
        addrlist.setAdapter(adapter);
        //getWalletAmount();
    }

    private void getData() {
        list = CartList.getInstance().getArrayListCart();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        amount = bundle.getString("amount");
        amount = amount.substring(0, amount.length() - 3);

        txtAmt.setText(amount);
        discountedAmount = Integer.parseInt(amount);
        //getDiscounts();
        getWalletAmount();
        // discountedAmount = Integer.parseInt(amount);
        /*addresses.clear();
        addresses.addAll(new SQLiteUtil().getAddressList(this));
        ListView addrlist = (ListView) findViewById(R.id.listView);
        MyAdapter adapter = new MyAdapter(this, addresses);
        addrlist.setAdapter(adapter);*/
    }


    class MyAdapter extends ArrayAdapter<Address> {

        ArrayList<Address> arr;

        public MyAdapter(Context context, ArrayList<Address> arr) {
            super(context, R.layout.addr_item);
            this.arr = arr;
        }

        @Override
        public int getCount() {
            return arr.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.addr_item, null);

            }
            Address address = arr.get(position);
            String add = address.getFname() + " " + address.getLname() + ",\n" + address.getAddr() + ", " + address.getArea() + ", " + ",\n" + address.getPhone();

            TextView txtaddr = (TextView) convertView.findViewById(R.id.textView3);
            txtaddr.setText(add);
            //txtaddr.setTypeface(Utils.setLatoFontBold(MyOrdersActivity.this));
            return convertView;
        }
    }

    private void setFonts() {
        //  orderBtn.setTypeface(Utils.setLatoFontBold(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        orderBtn = (Button) findViewById(R.id.orderBtn);
        spnTimeSlot = (Spinner) findViewById(R.id.spnTimeslot);
//        spnPaymentType =(Spinner)findViewById(R.id.spnPaymentType);

/*        spnTimeSlot.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
                new String[]{"9 - 11 AM","11 - 1 PM","1 - 3 PM","3 - 5 PM","5 - 7 PM"}));*/
        spnTimeSlot.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Before 9:00 AM"}));

        btnAddr = (Button) findViewById(R.id.btnAddr);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtAmt = (TextView) findViewById(R.id.amt);
        txtDeliveryCharges = (TextView) findViewById(R.id.txtDeliveryCharges);
        txtAmountToPay = (TextView) findViewById(R.id.txtAmountToPay);
        radioGroupPaymentMode = (RadioGroup) findViewById(R.id.radioGroupMode);
        textViewWalletPayment = (TextView) findViewById(R.id.textViewWalletPayment);
        layoutPayment = (LinearLayout) findViewById(R.id.layoutPayment);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == txtDate) {
                    fromDatePickerDialog.show();
                }
            }
        });
        btnAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaceOrderActivity.this, AddressActivity.class));
            }
        });

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        setDateTimeField();
        ((RadioButton) findViewById(R.id.radioCOD)).setChecked(true);
        radioGroupPaymentMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                discountedApplied = new StringBuilder();

                if (i == R.id.radioOnline) {
                    isOnline = true;
                    isWallet = false;
                    findViewById(R.id.txtDiscount).setVisibility(View.VISIBLE);
                    if (discountItem != null) {
                        //discountedAmount = (int) (Integer.parseInt(amount) * (5.0f / 100.0f));
                        discountedAmount = (Integer.parseInt(amount)) - Float.valueOf(discountItem.getOnline_Transction_Discount());
                        ((TextView) findViewById(R.id.txtDiscount)).setText("5% Discount amount to pay: " + discountItem.getOnline_Transction_Discount());
                        discountedApplied.append("Online 5% Discount amount: " + discountItem.getOnline_Transction_Discount() + "\n");
                    }
                } else if (i == R.id.radioCOD) {
                    isOnline = false;
                    isWallet = false;
                    //findViewById(R.id.txtDiscount).setVisibility(View.GONE);
                    discountedAmount = Integer.parseInt(amount);
                } else {
                    isWallet = true;
                    isOnline = false;
                    discountedAmount = Integer.parseInt(amount);
                    if (Integer.parseInt(walletAmount) >= discountedAmount) {
                        textViewWalletPayment.setVisibility(View.VISIBLE);
                        textViewWalletPayment.setText("Payment from wallet:\n" +
                                "" + walletAmount + " - " + discountedAmount + " = " + (Integer.parseInt(walletAmount) - (int) discountedAmount));

                    } else {
                        showDialog();
                        textViewWalletPayment.setVisibility(View.GONE);
                    }
                }

                setDefaultData();
            }
        });
    }

    private void setDefaultData() {
        txtAmt.setText(amount);
        /*int firstOrderDiscount = 0;
        Log.d("Amount", pref.getFirstOrderAmount());
        if (pref.getOrderCount() == 1) {
            firstOrderDiscount = Integer.parseInt(pref.getFirstOrderAmount()) / 2;
            if (firstOrderDiscount >= 250) {
                amount = String.valueOf((Integer.parseInt(amount)) - 250);
            } else {
                amount = String.valueOf((Integer.parseInt(amount)) - firstOrderDiscount);
            }
            if (Integer.parseInt(amount) < 0) {
                amount = "0";
            }
        }*/
        if (discountItem != null) {
            if (!discountItem.getSecond_Order_Discount().equalsIgnoreCase("0")) {
                discountedAmount = discountedAmount - Integer.parseInt(discountItem.getSecond_Order_Discount());
                discountedApplied.append("Second Order Discount amount: " + discountItem.getSecond_Order_Discount() + "\n");
            }
            if (!discountItem.getMonthly_5000_Amount_Discount().equalsIgnoreCase("0")) {
                discountedAmount = discountedAmount - Integer.parseInt(discountItem.getMonthly_5000_Amount_Discount());
                discountedApplied.append("Monthly 5000 Discount amount: " + discountItem.getMonthly_5000_Amount_Discount() + "\n");
            }
            if (!discountItem.getMonthly_10000_Amount_Discount().equalsIgnoreCase("0")) {
                discountedAmount = discountedAmount - Integer.parseInt(discountItem.getMonthly_10000_Amount_Discount());
                discountedApplied.append("Monthly 10000 Discount amount: " + discountItem.getMonthly_10000_Amount_Discount() + "\n");
            }
            if (!discountItem.getMonthly_15000_Amount_Discount().equalsIgnoreCase("0")) {
                discountedAmount = discountedAmount - Integer.parseInt(discountItem.getMonthly_15000_Amount_Discount());
                discountedApplied.append("Monthly 15000 Discount amount: " + discountItem.getMonthly_15000_Amount_Discount() + "\n");
            }
        }
        if (discountedAmount > 250) {
            txtDeliveryCharges.setText("You are eligible for free delivery");
            //discountedAmount = Integer.parseInt(dataObject.getAmount_Payable());
        } else {
            txtDeliveryCharges.setText("Delivery charges is Rs.30 for order below Rs.250");
            discountedAmount = discountedAmount + Integer.parseInt(discountItem.getDelivery_Charges());
        }
        //amount = String.valueOf(discountedAmount);
        txtAmountToPay.setText("Total Amount: Rs." + discountedAmount);
        //    txtAmt.setText(amount);
        ((TextView) findViewById(R.id.txtDiscount)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.txtDiscount)).setText(discountedApplied);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            SpannableString s = new SpannableString("Delivery address");
            s.setSpan(new TypefaceSpan(this, "Jacquard.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            toolbar.setTitle(s);
            setSupportActionBar(toolbar);
        }
    }

    private void setDateTimeField() {

        final Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                Calendar cal = Calendar.getInstance();

                if (newDate.get(Calendar.DAY_OF_YEAR) > (cal.get(Calendar.DAY_OF_YEAR) + 10)) {
                    Toast.makeText(PlaceOrderActivity.this, "Please select delivery date within next ten days.", Toast.LENGTH_SHORT).show();
                    txtDate.setText("");
                } else if (newDate.after(newCalendar) && newDate.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
                    Toast.makeText(PlaceOrderActivity.this, "Please select future date for delivery.", Toast.LENGTH_SHORT).show();
                    txtDate.setText("");

                } else if (newDate.after(newCalendar) && newDate.get(Calendar.DAY_OF_YEAR) > cal.get(Calendar.DAY_OF_YEAR)) {
                    txtDate.setText(dateFormatter.format(newDate.getTime()));

                    spnTimeSlot.setAdapter(new ArrayAdapter<String>(PlaceOrderActivity.this, android.R.layout.simple_spinner_dropdown_item,
                            new String[]{"Before 9:00 AM"}));

                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(PlaceOrderActivity.this);
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
                    .url(ulr[0])
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
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (s != null) {
                Log.e("response->", s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (json.getString("result").equalsIgnoreCase("success")) {
                        new SQLiteUtil().emptyCart(PlaceOrderActivity.this);
//                        if (json.optInt("orderCount") > 1) {
//                            pref.setFirstOrderStatus(false);
//                        } else {
//                            pref.setFirstOrderStatus(true);
//                        }

                        pref.setOrderCount(json.optInt("orderCount"));
                        int baseamount = Integer.parseInt(txtAmt.getText().toString());
                        pref.setFirstOrderAmount("" + (baseamount));
                        AlertDialog.Builder alert = new AlertDialog.Builder(PlaceOrderActivity.this);
                        alert.setMessage("Your order placed successfully");
                        alert.setTitle("Thank you !");
                        alert.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PlaceOrderActivity.this.finish();

                            }
                        });
                        alert.show();

                        new SQLiteUtil().emptyCart(PlaceOrderActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }        // onPostExecute displays the results of the AsyncTask.

    }

    public static String POST(String data) {


//        return HttpUtils.requestWebService(Constants.WS_URL, "POST", data);

        Response response = null;
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(Constants.WS_URL)
                .post(body)
                .build();

        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    private void setListeners() {
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addresses == null || addresses.isEmpty()) {
                    Toast.makeText(PlaceOrderActivity.this, "Enter delivery address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (txtDate.getText().toString().isEmpty()) {
                    txtDate.setError("Required field");
                    txtDate.requestFocus();
                    return;
                }

                if (spnTimeSlot.getSelectedItem().toString().equals("9 - 11 AM")) {

                }
//                makePayment(v);
                if (isWallet) {
                    if (Integer.parseInt(walletAmount) < discountedAmount) {
                        showDialog();
                        return;
                    } else {
                        placeOrder("Wallet");
                    }
                } else {
                    if (isOnline) {
                        //navigateToBaseActivity();
                        String vAccessCode = ServiceUtility.chkNull("AVCT70ED45CC50TCCC").toString().trim();
                        String vMerchantId = ServiceUtility.chkNull("129307").toString().trim();
                        String vCurrency = ServiceUtility.chkNull("INR").toString().trim();
                        String vAmount = ServiceUtility.chkNull(discountedAmount).toString().trim();
                        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")) {
                            Intent intent = new Intent(PlaceOrderActivity.this, WebViewActivity.class);
                            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull("AVCT70ED45CC50TCCC").toString().trim());
                            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull("129307").toString().trim());
                            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(System.currentTimeMillis()).toString().trim());
                            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull("INR").toString().trim());
                            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(discountedAmount).toString().trim());

                            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull("http://farmfresh24.in/farmfresh24/admin/ccavenue/ccavResponseHandler.php").toString().trim());
                            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull("http://farmfresh24.in/farmfresh24/admin/ccavenue/ccavResponseHandler.php").toString().trim());
                            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull("http://farmfresh24.in/farmfresh24/admin/ccavenue/GetRSA.php").toString().trim());

                            startActivityForResult(intent, 1);
                        } else {
                            Toast.makeText(PlaceOrderActivity.this, "All parameters are mandatory.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        placeOrder("Cash on delivery");
                    }
                }
            }
        });
    }

    private void placeOrder(String OrderMode) {
        String data1 = "\"[";
        String sendWalletAmount = "0";
        if (isWallet) {
            sendWalletAmount = "" + discountedAmount;
        }
        String p_id = "", qty = "";
        String price = "";
        for (int i = 0; i < list.size(); i++) {
            p_id += list.get(i).getProduct_id() + ",";
            qty += list.get(i).getQuantity() + ",";
            price = list.get(i).getProduct().getMrp() + ",";
        }

        data1 += "]\"";
        String req = "{\"method\":\"add_order\"" +
                ",\"first_name\":\"" + addresses.get(0).getFname() + "\"" +
                ",\"last_name\":\"" + addresses.get(0).getLname() + "\"," +
                "\"gender\":\"Male\"" +
                ",\"email\":\"" + new AppPreferences(PlaceOrderActivity.this).getEmail() + "\"" +
                ",\"amount\":\"" + discountedAmount +
                "\",\"shipping_type\":\"" + OrderMode + "\"" +
                ",\"street\":\"" + addresses.get(0).getArea() + "\"" +
                ",\"city\":\"" + addresses.get(0).getAddr() + "\"" +
                ",\"state\":\"" + addresses.get(0).getLandmark() + "\"" +
                ",\"country\":\"India\"" +
                ",\"zipcode\":\"" + addresses.get(0).getZipcode() +
                "\",\"phone\":\"" + addresses.get(0).getPhone() + "\"" +
                ",\"order_detail\":\"Delivery Date " + txtDate.getText().toString() + "" +
                ", between " + spnTimeSlot.getSelectedItem().toString() + "\"" +
                ",\"user_id\":\"23\"" +
                ",\"p_id\":\"" + p_id + "\"" +
                ",\"qty\":\"" + qty + "\"," +
                "\"price\":\"" + price + "\"" +
                ",\"Second_Order_Discount\":\"" + discountItem.getSecond_Order_Discount() + "\"" +
                ",\"Monthly_5000_Amount_Discount\":\"" + discountItem.getMonthly_5000_Amount_Discount() + "\"" +
                ",\"Monthly_10000_Amount_Discount\":\"" + discountItem.getMonthly_10000_Amount_Discount() + "\"" +
                ",\"Monthly_15000_Amount_Discount\":\"" + discountItem.getMonthly_15000_Amount_Discount() + "\"" +
                ",\"Online_Transction_Discount\":\"" + discountItem.getOnline_Transction_Discount() + "\"" +
                ",\"Delivery_Charges\":\"" + discountItem.getDelivery_Charges() + "\"" +
                ",\"wallet\":\"" + sendWalletAmount + "\"" +
                "}";

        Log.e("request", req);

        new HttpAsyncTask().execute(Constants.WS_URL, req);

    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private double getAmount() {

        Double amount = 10.0;

        if (isDouble(txtAmt.getText().toString())) {
            amount = Double.parseDouble(this.amount);
            return amount;
        } else {
            Toast.makeText(getApplicationContext(), "Paying Default Amount ₹10", Toast.LENGTH_LONG).show();
            return amount;
        }
    }

    public void makePayment(View view) {

        String phone = "8882434664";
        String productName = "product_name";
        String firstName = "piyush";
        String txnId = "0nf7" + System.currentTimeMillis();
        String email = "piyush.jain@payu.in";
        String sUrl = "https://test.payumoney.com/mobileapp/payumoney/success.php";
        String fUrl = "https://test.payumoney.com/mobileapp/payumoney/failure.php";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        boolean isDebug = true;
        String key = "mJX31QtA";
        String merchantId = "5743923";
    }

    private void showDialogMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(TAG);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("transStatus");
                if (result.equalsIgnoreCase("Success")) {
                    Toast.makeText(this, "Transaction has been successful", Toast.LENGTH_SHORT).show();
                    placeOrder("Online");
                } else {
                    Toast.makeText(this, "Transaction has been failed. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                *//**
     * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
     * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
     *
     * PayU sends the same response to merchant server and in app. In response check the value of key "status"
     * for identifying status of transaction. There are two possible status like, success or failure
     * *//*

                try {
                    JSONObject response = new JSONObject(data.getStringExtra("payu_response"));
                    if (response.optString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        placeOrder("Online");
                    } else {
                        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                    *//*new android.app.AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setMessage("Your Payment has been " + response.optString("status"))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();

                                }
                            }).show();*//*
                } catch (Exception e) {
                    e.printStackTrace();
                }
*//*
                new android.app.AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*//*

            } else {
                Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
            }
        }
    }

    *//**
     * This method prepares all the payments params to be sent to PayuBaseActivity.java
     *//*
    public void navigateToBaseActivity() {

        //merchantKey = "gtKFFx"; //Testing
        merchantKey = "ImrH8w"; //Live
        //merchantKey = ((EditText) findViewById(R.id.editTextMerchantKey)).getText().toString();
        // String amount = ((EditText) findViewById(R.id.editTextAmount)).getText().toString();
        // String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();

        // String value = environmentSpinner.getSelectedItem().toString();
        int environment;
        // String TEST_ENVIRONMENT = getResources().getString(R.string.test);
        // if (value.equals(TEST_ENVIRONMENT))
        //environment = PayuConstants.MOBILE_STAGING_ENV;
        // else
        environment = PayuConstants.PRODUCTION_ENV;

        userCredentials = merchantKey + ":" + "anafali2000@gmail.com";// + new AppPreferences(PlaceOrderActivity.this).getEmail();

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        *//**
     * For Test Environment, merchantKey = "gtKFFx"
     * For Production Environment, merchantKey should be your live key or for testing in live you can use "0MQaQP"
     *//*
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(String.valueOf(discountedAmount));
        mPaymentParams.setProductInfo("FarmFresh24");
        mPaymentParams.setFirstName(new AppPreferences(PlaceOrderActivity.this).getFname());
        mPaymentParams.setEmail(new AppPreferences(PlaceOrderActivity.this).getEmail());

        *//*
        * Transaction Id should be kept unique for each transaction.
        * *//*
        mPaymentParams.setTxnId("" + System.currentTimeMillis());

        *//**
     * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
     * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
     *//*
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");

        *//*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * *//*
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        *//**
     * These are used for store card feature. If you are not using it then user_credentials = "default"
     * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
     * here merchant_key = your merchant key,
     * user_id = unique id related to user like, email, phone number, etc.
     * *//*
        mPaymentParams.setUserCredentials(userCredentials);

        //TODO Pass this param only if using offer key
        //mPaymentParams.setOfferKey("cardnumber@8370");

        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
        //generateHashFromServer(mPaymentParams);

        *//**
     * Below approach for generating hash is not recommended. However, this approach can be used to test in PRODUCTION_ENV
     * if your server side hash generation code is not completely setup. While going live this approach for hash generation
     * should not be used.
     * *//*
        String salt = "BBq8CTgS"; //Live
        // String salt = "eCwWELxi"; //Test
        generateHashFromSDK(mPaymentParams, salt);

    }

    *//******************************
     * Client hash generation
     ***********************************//*
    // Do not use this, you may use this only for testing.
    // lets generate hashes.
    // This should be done from server side..
    // Do not keep salt anywhere in app.
    public void generateHashFromSDK(PaymentParams mPaymentParams, String salt) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }

    *//**
     * This method generates hash from server.
     *
     * @param mPaymentParams payments params used for hash generation
     *//*
    public void generateHashFromServer(PaymentParams mPaymentParams) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    *//**
     * This AsyncTask generates hash from server.
     *//*
    private class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PlaceOrderActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected PayuHashes doInBackground(String... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {

                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        //TODO Below three hashes are mandatory for payment flow and needs to be generated at merchant server
                        *//**
     * Payment hash is one of the mandatory hashes that needs to be generated from merchant's server side
     * Below is formula for generating payment_hash -
     *
     * sha512(key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT)
     *
     *//*
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));
                            break;
                        *//**
     * vas_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
     * Below is formula for generating vas_for_mobile_sdk_hash -
     *
     * sha512(key|command|var1|salt)
     *
     * here, var1 will be "default"
     *
     *//*
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            break;
                        *//**
     * payment_related_details_for_mobile_sdk_hash is one of the mandatory hashes that needs to be generated from merchant's server side
     * Below is formula for generating payment_related_details_for_mobile_sdk_hash -
     *
     * sha512(key|command|var1|salt)
     *
     * here, var1 will be user credentials. If you are not using user_credentials then use "default"
     *
     *//*
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                            break;

                        //TODO Below hashes only needs to be generated if you are using Store card feature
                        *//**
     * delete_user_card_hash is used while deleting a stored card.
     * Below is formula for generating delete_user_card_hash -
     *
     * sha512(key|command|var1|salt)
     *
     * here, var1 will be user credentials. If you are not using user_credentials then use "default"
     *
     *//*
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        *//**
     * get_user_cards_hash is used while fetching all the cards corresponding to a user.
     * Below is formula for generating get_user_cards_hash -
     *
     * sha512(key|command|var1|salt)
     *
     * here, var1 will be user credentials. If you are not using user_credentials then use "default"
     *
     *//*
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        *//**
     * edit_user_card_hash is used while editing details of existing stored card.
     * Below is formula for generating edit_user_card_hash -
     *
     * sha512(key|command|var1|salt)
     *
     * here, var1 will be user credentials. If you are not using user_credentials then use "default"
     *
     *//*
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        *//**
     * save_user_card_hash is used while saving card to the vault
     * Below is formula for generating save_user_card_hash -
     *
     * sha512(key|command|var1|salt)
     *
     * here, var1 will be user credentials. If you are not using user_credentials then use "default"
     *
     *//*
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;

                        //TODO This hash needs to be generated if you are using any offer key
                        *//**
     * check_offer_status_hash is used while using check_offer_status api
     * Below is formula for generating check_offer_status_hash -
     *
     * sha512(key|command|var1|salt)
     *
     * here, var1 will be Offer Key.
     *
     *//*
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);

            progressDialog.dismiss();
            launchSdkUI(payuHashes);
        }
    }

    *//**
     * This method adds the Payuhashes and other required params to intent and launches the PayuBaseActivity.java
     *
     * @param payuHashes it contains all the hashes generated from merchant server
     *//*
    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        //Lets fetch all the one click card tokens first
        fetchMerchantHashes(intent);

    }

    //TODO This method is used if integrating One Tap Payments

    *//**
     * This method stores merchantHash and cardToken on merchant server.
     *
     * @param cardToken    card token received in transaction response
     * @param merchantHash merchantHash received in transaction response
     *//*
    private void storeMerchantHash(String cardToken, String merchantHash) {

        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials + "&card_token=" + cardToken + "&merchant_hash=" + merchantHash;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    //TODO Deploy a file on your server for storing cardToken and merchantHash nad replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/store_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }


    //TODO This method is used only if integrating One Tap Payments

    *//**
     * This method fetches merchantHash and cardToken already stored on merchant server.
     *//*
    private void fetchMerchantHashes(final Intent intent) {
        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials;
        final Intent baseActivityIntent = intent;
        new AsyncTask<Void, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());

                    HashMap<String, String> cardTokens = new HashMap<String, String>();
                    JSONArray oneClickCardsArray = response.getJSONArray("data");
                    int arrayLength;
                    if ((arrayLength = oneClickCardsArray.length()) >= 1) {
                        for (int i = 0; i < arrayLength; i++) {
                            cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                        }
                        return cardTokens;
                    }
                    // pass these to next activity

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> oneClickTokens) {
                super.onPostExecute(oneClickTokens);

                baseActivityIntent.putExtra(PayuConstants.ONE_CLICK_CARD_TOKENS, oneClickTokens);
                startActivityForResult(baseActivityIntent, PayuConstants.PAYU_REQUEST_CODE);
            }
        }.execute();
    }

    //TODO This method is used only if integrating One Tap Payments

    *//**
     * This method deletes merchantHash and cardToken from server side file.
     *
     * @param cardToken cardToken of card whose merchantHash and cardToken needs to be deleted from merchant server
     *//*
    private void deleteMerchantHash(String cardToken) {

        final String postParams = "card_token=" + cardToken;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/delete_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }

    //TODO This method is used only if integrating One Tap Payments

    *//**
     * This method prepares a HashMap of cardToken as key and merchantHash as value.
     *
     * @param merchantKey     merchant key used
     * @param userCredentials unique credentials of the user usually of the form key:userId
     *//*
    public HashMap<String, String> getAllOneClickHashHelper(String merchantKey, String userCredentials) {

        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials;
        HashMap<String, String> cardTokens = new HashMap<String, String>();

        try {
            //TODO Replace below url with your server side file url.
            URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

            byte[] postParamsByte = postParams.getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postParamsByte);

            InputStream responseInputStream = conn.getInputStream();
            StringBuffer responseStringBuffer = new StringBuffer();
            byte[] byteContainer = new byte[1024];
            for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                responseStringBuffer.append(new String(byteContainer, 0, i));
            }

            JSONObject response = new JSONObject(responseStringBuffer.toString());

            JSONArray oneClickCardsArray = response.getJSONArray("data");
            int arrayLength;
            if ((arrayLength = oneClickCardsArray.length()) >= 1) {
                for (int i = 0; i < arrayLength; i++) {
                    cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                }

            }
            // pass these to next activity

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardTokens;
    }

    //TODO This method is used only if integrating One Tap Payments

    *//**
     * Returns a HashMap object of cardToken and one click hash from merchant server.
     * <p>
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * The function should return a cardToken and corresponding one click hash as a hashMap.
     *
     * @param userCreds a string giving the user credentials of user.
     * @return the Hash Map of cardToken and one Click hash.
     **//*
    @Override
    public HashMap<String, String> getAllOneClickHash(String userCreds) {
        // 1. GET http request from your server
        // GET params - merchant_key, user_credentials.
        // 2. In response we get a
        // this is a sample code for fetching one click hash from merchant server.
        return getAllOneClickHashHelper(merchantKey, userCreds);
    }

    //TODO This method is used only if integrating One Tap Payments
    @Override
    public void getOneClickHash(String cardToken, String merchantKey, String userCredentials) {

    }


    //TODO This method is used only if integrating One Tap Payments

    *//**
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function save the oneClickHash corresponding to its cardToken
     *
     * @param cardToken    a string containing the card token
     * @param oneClickHash a string containing the one click hash.
     **//*

    @Override
    public void saveOneClickHash(String cardToken, String oneClickHash) {
        // 1. POST http request to your server
        // POST params - merchant_key, user_credentials,card_token,merchant_hash.
        // 2. In this POST method the oneclickhash is stored corresponding to card token in merchant server.
        // this is a sample code for storing one click hash on merchant server.

        storeMerchantHash(cardToken, oneClickHash);

    }

    //TODO This method is used only if integrating One Tap Payments

    */

    /**
     * This method will be called as a async task, regardless of merchant implementation.
     * Hence, not to call this function as async task.
     * This function delete’s the oneClickHash from the merchant server
     * <p>
     * //* @param cardToken       a string containing the card token
     * //* @param userCredentials a string containing the user credentials.
     **//*

    @Override
    public void deleteOneClickHash(String cardToken, String userCredentials) {

        // 1. POST http request to your server
        // POST params  - merchant_hash.
        // 2. In this POST method the oneclickhash is deleted in merchant server.
        // this is a sample code for deleting one click hash from merchant server.

        deleteMerchantHash(cardToken);

    }*/

/*
    private void getData() {
        String street = "", city = "", state = "", country = "", zipcode = "", phone = "", fname = "", lname = "", email = "";
        List<Address> address = new SQLiteUtil().getAddressList(MyCartActivity.this);
        if (address != null && address.size() > 0) {
            street = address.get(0).getArea();
            city = address.get(0).getLandmark();
            zipcode = address.get(0).getZipcode();
            fname = address.get(0).getFname();
            lname = address.get(0).getLname();
            phone = address.get(0).getPhone();
        }
        String p_id = "", qty = "";
        String price = "";
        for (int i = 0; i < list.size(); i++) {
            p_id += list.get(i).getProduct_id() + ",";
            qty += list.get(i).getQuantity() + ",";
            price = list.get(i).getProduct().getMrp() + ",";
        }
        String request;
        request = "{\"method\":\"add_order\"" +
                ",\"first_name\":\"" + fname + "\"" +
                ",\"last_name\":\"" + lname + "\"" +
                ",\"gender\":\"Male\"" +
                ",\"email\":\"" + email + "\"" +
                ",\"amount\":\"" + grandTotal + "\"" +
                ",\"shipping_type\":\"Cash on delivery\"" +
                ",\"street\":\"" + street + "\"" +
                ",\"city\":\"" + city + "\"" +
                ",\"state\":\"Maharashtra\"" + "\"" +
                ",\"country\":\"India\"" + "\"" +
                ",\"zipcode\":\"" + zipcode + "\"" +
                ",\"phone\":\"" + phone + "\"" +
                ",\"order_detail\":\"Delivery Date 31/03/2017" +
                ", between Before 9:00AM\"" +
                ",\"user_id\":\"23\"" +
                ",\"p_id\":\"" + p_id + "\"" +
                ",\"qty\":\"" + qty + "\"" +
                ",\"price\":\"" + price + "\"" +
                ",\"Second_Order_Discount\":\"0\"" +
                ",\"Monthly_5000_Amount_Discount\":\"0\"" +
                ",\"Monthly_10000_Amount_Discount\":\"0\"" +
                ",\"Monthly_15000_Amount_Discount\":\"0\"" +
                ",\"Online_Transction_Discount\":\"0\"" +
                ",\"Delivery_Charges\":\"30\"" +
                ",\"wallet\":\"0\"}";
        new ProductTask().execute(Constants.WS_URL, request);
    }
*/
    private void getDiscounts() {
        String p_id = "";
        for (int i = 0; i < list.size(); i++) {
            p_id += list.get(i).getProduct_id() + ",";
        }
        String request;
        request = "{\"method\":\"get_discount_from_server\"" +
                ",\"email\":\"" + new AppPreferences(PlaceOrderActivity.this).getEmail() + "\"" +
                ",\"amount\":\"" + amount + "\"" +
                "}";

        new ProductTask().execute(Constants.WS_URL, request);
    }

    class ProductTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(PlaceOrderActivity.this);
            pd.setMessage("Loading...");
            pd.show();
            pd.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... ulr) {
            Response response = null;
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            Log.e("request", ulr[1]);
            RequestBody body = RequestBody.create(JSON, ulr[1]);
            Request request = new Request.Builder()
                    .url(ulr[0])
                    .post(body)
                    .build();

            try {
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "error";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (s != null) {
                if (s.equals("error")) {
                    Toast.makeText(PlaceOrderActivity.this, "Error occurred...try again ", Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    try {
                        Log.e("response", s);
                        JSONArray jsonArray = new JSONObject(s).optJSONArray("data");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                discountItem = new DiscountItem();
                                discountItem.setAmount_Payable(dataObject.optString("Amount_Payable"));
                                discountItem.setDelivery_Charges(dataObject.optString("Delivery_Charges"));
                                discountItem.setMonthly_5000_Amount_Discount(dataObject.optString("Monthly_5000_Amount_Discount"));
                                discountItem.setMonthly_10000_Amount_Discount(dataObject.optString("Monthly_10000_Amount_Discount"));
                                discountItem.setMonthly_15000_Amount_Discount(dataObject.optString("Monthly_15000_Amount_Discount"));
                                discountItem.setOnline_Transction_Discount(dataObject.optString("Online_Transction_Discount"));
                                discountItem.setSecond_Order_Discount(dataObject.optString("Second_Order_Discount"));
                                setDefaultData();
                            }
                        } else {
                            showErrorDialog();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showErrorDialog();
                    }
            }
        }
    }

    private void getWalletAmount() {
        String req = "{\"method\":\"get_wallet_amount\",\"email\":\"" + new AppPreferences(PlaceOrderActivity.this).getEmail() + "\"}";

        Log.e("request", req);

        new HttpAsyncTaskGet().execute(Constants.WS_URL, req);

    }

    private class HttpAsyncTaskGet extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(PlaceOrderActivity.this);
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
                    .url(ulr[0])
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
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (s != null) {
                Log.e("response", s);
                try {
                    JSONObject json = new JSONObject(s);
                    if (json.getString("result").equalsIgnoreCase("success")) {
                        AppPreferences app = new AppPreferences(PlaceOrderActivity.this);
                        app.setWalletAmount(json.optString("wallet_amount"));
                        walletAmount = json.optString("wallet_amount");
                        getDiscounts();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(PlaceOrderActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(PlaceOrderActivity.this);
        }
        builder.setTitle(getString(R.string.app_name))
                .setMessage("Insufficient balance, please add amount in your wallet")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = new Intent(PlaceOrderActivity.this, MyWalletActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(PlaceOrderActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(PlaceOrderActivity.this);
        }
        builder.setTitle(getString(R.string.app_name))
                .setMessage("Error in Fetching data")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

