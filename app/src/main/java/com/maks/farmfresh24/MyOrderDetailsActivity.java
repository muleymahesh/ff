package com.maks.farmfresh24;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.maks.farmfresh24.adapter.MyOrderAdapter;
import com.maks.farmfresh24.adapter.ProductAdapter;
import com.maks.farmfresh24.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deva on 09/03/2017.
 */

public class MyOrderDetailsActivity extends AppCompatActivity implements ProductAdapter.OnItemClickListener {

    private Toolbar toolbar;
    //Creating a List of Category
    private List<OrderDetail> listCategory = new ArrayList<OrderDetail>();

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private TextView txtAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        initToolbar();
        initView();
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listCategory = (List<OrderDetail>) extras.getSerializable("OrderList");
            txtAmount.setVisibility(View.VISIBLE);
            txtAmount.setText("Total: Rs." + extras.getString("TotalAmount"));
            adapter = new MyOrderAdapter(listCategory, MyOrderDetailsActivity.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
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