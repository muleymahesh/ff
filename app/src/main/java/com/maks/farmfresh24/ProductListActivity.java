package com.maks.farmfresh24;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.maks.farmfresh24.adapter.ProductAdapter;
import com.maks.farmfresh24.model.Product;
import com.maks.farmfresh24.model.ProductDTO;
import com.maks.farmfresh24.utils.AppPreferences;
import com.maks.farmfresh24.utils.ConnectionDetector;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.TypefaceSpan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductListActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

    private Toolbar toolbar;
    //Creating a List of Category
    private List<Product> listCategory = new ArrayList<>();

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    AppController appController;
    LinearLayout llMain;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        appController = AppController.getInstance();
        llMain = (LinearLayout)findViewById(R.id.llMain);

        initToolbar();
        initView();


        adapter = new ProductAdapter(listCategory, ProductListActivity.this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,MyCartActivity.class));
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if((id==R.id.action_sort)){

            final CharSequence[] items = {
                    "Sort by price", "Sort by discount", "Sort by name"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make your selection");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    if(item==0) {
                        Collections.sort(listCategory, new Comparator<Product>() {
                            @Override
                            public int compare(Product lhs, Product rhs) {
                               try {
                                   if (Double.parseDouble(lhs.getMrp()) > Double.parseDouble(rhs.getMrp())) {
                                       return 1;
                                   } else if (Double.parseDouble(lhs.getMrp()) < Double.parseDouble(rhs.getMrp())) {
                                       return -1;
                                   }
                               }catch (Exception e){

                               }
                                return 0;
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }

                    if(item==1) {
                        Collections.sort(listCategory, new Comparator<Product>() {
                            @Override
                            public int compare(Product lhs, Product rhs) {
                                if (Double.parseDouble(lhs.getPer_discount()) > Double.parseDouble(rhs.getPer_discount())) {
                                    return -1;
                                } else if (Double.parseDouble(lhs.getPer_discount()) < Double.parseDouble(rhs.getPer_discount())) {
                                    return 1;
                                }

                                return 0;
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }

                    if(item==2) {
                        Collections.sort(listCategory, new Comparator<Product>() {
                            @Override
                            public int compare(Product lhs, Product rhs) {

                                return lhs.getProduct_name().compareTo(rhs.getProduct_name());
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_list, menu);
        return true;
    }

    private void getData(){
        if(getIntent().hasExtra("brand_id")){
            makeNetworkCall("{\"method\":\"get_product_by_brand\",\"brand_id\":\""+getIntent().getStringExtra("brand_id")+"\"}");

        }else if(getIntent().hasExtra("getfav")){
            makeNetworkCall("{\"method\":\"get_fav\",\"user_id\":\""+ new AppPreferences(ProductListActivity.this).getEmail()+"\"}");
        }
        else{
            makeNetworkCall("{\"method\":\"get_product_by_cat\",\"cat_id\":\""+getIntent().getStringExtra("cat_id")+"\"}");
        }
    }

   public void makeNetworkCall(String json){
        if (new ConnectionDetector(this).isConnectingToInternet()) {
            pd =new ProgressDialog(this);
            pd.setMessage("loading data...");
            pd.show();

            Log.e("request",json);
            appController.addToRequestQueue(appController.generateStringRequest(json,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(pd !=null && pd.isShowing()){
                                pd.dismiss();
                            }

                            parseData(response);
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(pd !=null && pd.isShowing()){
                                pd.dismiss();
                            }

                            Snackbar snackbar = Snackbar
                                    .make(llMain, "Error loading data...", Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getData();
                                        }
                                    });
                            snackbar.setActionTextColor(Color.GREEN);
                            snackbar.show();
                        }
                    }));

        } else {
            Snackbar snackbar = Snackbar
                    .make(llMain, "Error loading data...", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    }

    //This method will parse json data
    private void parseData(String array){

        try {
            ProductDTO arr = new Gson().fromJson(array.toString(), ProductDTO.class);

            listCategory.clear();
            listCategory.addAll(arr.getData());


        }catch (Exception e){e.printStackTrace();}//Finally initializig our adapter
        adapter.notifyDataSetChanged();
    }


    private void initView() {

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
                String title="";
            if(getIntent().hasExtra("brand_id")){
                title=""+getIntent().getStringExtra("name");
            }else if(getIntent().hasExtra("getfav")){
                title="My Favorites";
            }else{
                title=""+getIntent().getStringExtra("cat_name");

            }
            SpannableString s = new SpannableString(title);
            s.setSpan(new TypefaceSpan(this, "Jacquard.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toolbar.setTitle(s);
            setSupportActionBar(toolbar);

        }

    }

    @Override
    public void onItemClick(View view, int position) {
        if(view.getId()==R.id.btnMinus){

        }else
        if(view.getId()==R.id.btnPlus){

        }else {

            Product product = listCategory.get(position);
            Intent intent = new Intent(this, ProductDetailScreenActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        }
    }
}
