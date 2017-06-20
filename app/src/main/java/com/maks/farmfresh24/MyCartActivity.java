package com.maks.farmfresh24;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.maks.farmfresh24.adapter.CartAdapter;
import com.maks.farmfresh24.dbutils.SQLiteUtil;
import com.maks.farmfresh24.model.CartList;
import com.maks.farmfresh24.model.ShoppingCart;
import com.maks.farmfresh24.utils.AppPreferences;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.TypefaceSpan;
import com.maks.farmfresh24.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyCartActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    private TextView txtTotal, txtRs;
    private Button btnCheckout;
    private float totalAmt, mrp, qty, grandTotal = 0;
    ArrayList<ShoppingCart> list;
    String strGrandTotal = "0";
    SQLiteUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        initToolbar();
        initView();
//        setFonts();
        dbUtil = new SQLiteUtil();
        Log.e("TAG", "Inside My Cart ");
        setListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        list = dbUtil.getData(MyCartActivity.this);
        if (list.size() == 0) {
            btnCheckout.setText("Start Shopping");
            grandTotal = 0;
            txtRs.setText(String.format("Rs. %.2f", grandTotal));
        } else {
            grandTotal = 0;
            for (int i = 0; i < list.size(); i++) {
                try {
                    mrp = Integer.parseInt(Utils.discountPrice(list.get(i).getProduct().getMrp(), list.get(i).getProduct().getPer_discount()));

                    totalAmt = mrp * Integer.parseInt(list.get(i).getQuantity());
                    grandTotal = grandTotal + totalAmt;

                    String item = list.get(i).getProduct().getShort_desc().toString();
                    Log.e("TAG", "My Cart:::Item: " + item);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

       /* adapter = new CartAdapter(list, MyCartActivity.this);
        recyclerView.setAdapter(adapter);
*/
        txtRs.setText(String.format("Rs. %.2f", grandTotal));
        if (list.size() > 0) {
            getActualPrizes();
        } else {
            adapter = new CartAdapter(list, MyCartActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }

    public void onItemClick(View v, int position) {
        ShoppingCart sh = list.get(position);
        if (v.getId() == R.id.btnDel) {
            dbUtil.deleteCartItem(sh.getId(), this);
        }
        if (v.getId() == R.id.btnMinus) {
            int q = Integer.parseInt(sh.getQuantity());

            if (q > 1) {

                dbUtil.deleteCartItem(sh.getId(), this);
                sh.setQuantity("" + (q - 1));
                dbUtil.insert(sh, this);

            }
        }

        if (v.getId() == R.id.btnPlus) {
            int q = Integer.parseInt(sh.getQuantity());

            dbUtil.deleteCartItem(sh.getId(), this);
            sh.setQuantity("" + (q + 1));
            dbUtil.insert(sh, this);
        }
        loadData();
    }

    private void setListener() {
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCheckout.getText().equals("Start Shopping")) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    if (new AppPreferences(MyCartActivity.this).isLogin()) {
                        Intent intent = new Intent(getApplicationContext(), PlaceOrderActivity.class);
                        intent.putExtra("amount", txtRs.getText().toString().substring(4));
                        CartList.getInstance().setArrayListCart(list);
                        startActivity(intent);
                    } else {
                        Intent intent4 = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent4);
                    }

                }
            }
        });
    }

    private void setFonts() {
        txtTotal.setTypeface(Utils.setLatoFontBold(this));
        txtTotal.setTypeface(Utils.setLatoFontBold(this));
        txtRs.setTypeface(Utils.setLatoFontBold(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            SpannableString s = new SpannableString("My shopping cart ");
            s.setSpan(new TypefaceSpan(this, "Jacquard.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toolbar.setTitle(s);
            setSupportActionBar(toolbar);
        }

    }

    private void initView() {

        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtRs = (TextView) findViewById(R.id.txtRs);
        btnCheckout = (Button) findViewById(R.id.btnCheckOut);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void getActualPrizes() {
        String p_id = "";
        for (int i = 0; i < list.size(); i++) {
            p_id += list.get(i).getProduct_id() + ",";
        }
        String request;
        request = "{\"method\":\"get_product_detail_by_ID\"" +
                ",\"email\":\"" + new AppPreferences(MyCartActivity.this).getEmail() + "\"" +
                ",\"p_id\":\"" + p_id + "\"" +
                "}";
        new ProductTask().execute(Constants.WS_URL, request);
    }

    class ProductTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MyCartActivity.this);
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
                try {
                    Log.e("response", s);
                    JSONArray jsonArray = new JSONObject(s).optJSONArray("data");
                    if (jsonArray != null) {
                        for (int j = 0; j < list.size(); j++) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                if (list.get(j).getId().equalsIgnoreCase(dataObject.optString("p_id"))) {
                                    list.get(j).getProduct().setMrp(dataObject.optString("mrp"));
                                }
                            }
                        }
                    } else {
                        showErrorDialog();
                    }
                } catch (Exception e) {
                    showErrorDialog();
                    e.printStackTrace();
                }
            }
            adapter = new CartAdapter(list, MyCartActivity.this);
            recyclerView.setAdapter(adapter);
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MyCartActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MyCartActivity.this);
        }
        builder.setTitle(getString(R.string.app_name))
                .setMessage("Error in Fetching data")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        getActualPrizes();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
