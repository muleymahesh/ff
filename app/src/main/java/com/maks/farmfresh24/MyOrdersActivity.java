package com.maks.farmfresh24;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.maks.farmfresh24.adapter.MyOrderListAdapter;
import com.maks.farmfresh24.adapter.ProductAdapter;
import com.maks.farmfresh24.model.OrderDTO;
import com.maks.farmfresh24.model.OrderPojo;
import com.maks.farmfresh24.utils.AppPreferences;
import com.maks.farmfresh24.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyOrdersActivity  extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

    private Toolbar toolbar;
    //Creating a List of Category
    private List<OrderPojo> listCategory = new ArrayList<OrderPojo>();

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        initToolbar();
        initView();

        getData();

        adapter = new MyOrderListAdapter(listCategory, MyOrdersActivity.this);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class ProductTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MyOrdersActivity.this);
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
                    parseData(s);//new JSONObject(s).getJSONArray("data")

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getData() {
        new ProductTask().execute(Constants.WS_URL, "{\"method\":\"get_order\",\"user_id\":\"" + new AppPreferences(MyOrdersActivity.this).getEmail() + "\"}");
    }


    //This method will parse json data
    private void parseData(String array) {

        OrderDTO arr = new Gson().fromJson(array.toString(), OrderDTO.class);

        listCategory.addAll(arr.getOrders());
        //Finally initializig our adapter
        adapter.notifyDataSetChanged();
    }


    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("My orders");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public void onItemClick(View view, int position) {
//        OrderDTO product = listCategory.get(position);
//        Intent intent=new Intent(this,ProductDetailScreenActivity.class);
//        intent.putExtra("product", product);
//        startActivity(intent);

    }
}
