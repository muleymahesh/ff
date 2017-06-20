package com.maks.farmfresh24;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.maks.farmfresh24.dbutils.SQLiteUtil;
import com.maks.farmfresh24.model.Product;
import com.maks.farmfresh24.model.ProductDTO;
import com.maks.farmfresh24.model.ShoppingCart;
import com.maks.farmfresh24.utils.AppPreferences;
import com.maks.farmfresh24.utils.ConnectionDetector;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.TypefaceSpan;
import com.maks.farmfresh24.utils.Utils;
import com.maks.farmfresh24.utils.ZoomableImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductDetailScreenActivity extends AppCompatActivity {
    private Product product;
    private ImageView imgViewq, btnPlus, btnMinus;
    private TextView txtName, txtShortDesc, txtLongDesc, mrp, offer, size, brand;
    private TextView txtQuantity, stock, expiry;
    Button btnAddToCart, add_to_fav;
    private Toolbar toolbar;
    CustomPagerAdapter adapter;
    ViewPager viewPager;
    boolean isFav = false;
    AppController appController;
    LinearLayout llMain;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_screen);
        appController = AppController.getInstance();
        llMain = (LinearLayout)findViewById(R.id.llMain);

        initToolbar();
        initView();
//        setFont();

    }

    @Override
    protected void onResume() {
        super.onResume();
        makeNetworkCall("{\"method\":\"get_product_detail\",\"p_id\":\""+getIntentData()+"\", \"email\":\""+ new AppPreferences(ProductDetailScreenActivity.this).getEmail()+"\"}");
    }

    public void makeNetworkCall(String json){

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            pd =new ProgressDialog(this);
            pd.setMessage("loading data...");
            pd.show();
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
                                            makeNetworkCall("{\"method\":\"get_product_detail\",\"p_id\":\""+getIntentData()+"\", \"email\":\""+ new AppPreferences(ProductDetailScreenActivity.this).getEmail()+"\"}");
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

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.pager_item, collection, false);
            ImageView img  = (ImageView) layout.findViewById(R.id.imgPager);
            Picasso.with(ProductDetailScreenActivity.this).load(Constants.PRODUCT_IMG_PATH+product.getImgs().get(position).getImg_url()).placeholder(R.drawable.logo1_grey).placeholder(R.drawable.logo1_grey).error(R.drawable.logo1_grey).into(img);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return product.getImgs().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    public static class Tab1 extends Fragment {

        //Overriden method onCreateView
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            //Returning the layout file after inflating
            //Change R.layout.tab1 in you classes
            ImageView v = (ImageView) inflater.inflate(R.layout.pager_item, container, false);
            v.setBackgroundColor(Color.WHITE);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog d = new Dialog(getActivity());

                    d.setContentView(R.layout.zoom_img);
                    Button close = (Button) d.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d.dismiss();
                        }
                    });
                    ZoomableImageView imgZoom = (ZoomableImageView) d.findViewById(R.id.imgZoom);
                    Picasso.with(getContext()).load(Constants.PRODUCT_IMG_PATH + getArguments().getString("url")).resize(getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth(), getActivity().getWindow().getWindowManager().getDefaultDisplay().getHeight()).centerInside().placeholder(R.drawable.logo1_grey).error(R.drawable.logo1_grey).into(imgZoom);
                    d.show();
                }
            });
            Picasso.with(getContext()).load(Constants.PRODUCT_IMG_PATH + getArguments().getString("url")).resize(400, 400).centerInside().placeholder(R.drawable.logo1_grey).error(R.drawable.logo1_grey).into(v);

            return v;
        }

    }

    private void parseData(String array){

        try {
            ProductDTO arr = new Gson().fromJson(array.toString(), ProductDTO.class);
            product = arr.getData().get(0);
            isFav = product.getIsFav().equals("true");
            setView();
            setListeners();

        }catch (Exception e){e.printStackTrace();}//Finally initializig our adapter

    }




    private void setListeners() {

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtQuantity.getText().toString().isEmpty() || !txtQuantity.getText().toString().equals("0")) {
                    txtQuantity.setText("" + (Integer.parseInt(txtQuantity.getText().toString()) - 1));

                    SQLiteUtil dbUtil = new SQLiteUtil();

                    ShoppingCart cart = dbUtil.getCartItem(ProductDetailScreenActivity.this,product.getP_id());
                    if(cart!=null && cart.getQuantity().equals("1")) {
                        dbUtil.deleteCartItem(cart.getId(),ProductDetailScreenActivity.this);

                    }else if(cart!=null){
                        cart = new ShoppingCart();
                        cart.setProduct_id(product.getP_id());
                        cart.setProduct(product);
                        cart.setQuantity(txtQuantity.getText().toString());


                        cart.setQuantity(""+(Integer.parseInt(cart.getQuantity())-1));

                        dbUtil.deleteCartItem(cart.getId(),ProductDetailScreenActivity.this);
                        dbUtil.insert(cart,ProductDetailScreenActivity.this);
                    }


                }
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtQuantity.setText("" + (Integer.parseInt(txtQuantity.getText().toString()) + 1));
                SQLiteUtil dbUtil = new SQLiteUtil();

                ShoppingCart cart = dbUtil.getCartItem(ProductDetailScreenActivity.this,product.getP_id());
                if(cart==null) {
                    cart = new ShoppingCart();
                    cart.setProduct_id(product.getP_id());
                    cart.setProduct(product);
                    cart.setQuantity(txtQuantity.getText().toString());
                }
                else{

                    cart.setQuantity(""+(Integer.parseInt(cart.getQuantity())+1));
                }
                dbUtil.deleteCartItem(cart.getId(),ProductDetailScreenActivity.this);
                dbUtil.insert(cart,ProductDetailScreenActivity.this);

            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), MyCartActivity.class);
                startActivity(intent);
                finish();

            }
        });


        if (!isFav) {
            add_to_fav.setBackgroundResource(R.drawable.fav_unselected);
        } else {
            add_to_fav.setBackgroundResource(R.drawable.fav_selected);
        }


        add_to_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new AppPreferences(ProductDetailScreenActivity.this).isLogin()) {

                    if (!isFav) {
                        new AddToFavTask(ProductDetailScreenActivity.this).execute(Constants.WS_URL, "{\"method\":\"add_fav\",\"user_id\":\"" + new AppPreferences(ProductDetailScreenActivity.this).getEmail() + "\",\"p_id\":\"" + product.getP_id() + "\"}");
                    } else {
                        new RemoveFromFavTask(ProductDetailScreenActivity.this).execute(Constants.WS_URL, "{\"method\":\"delete_fav\",\"user_id\":\"" + new AppPreferences(ProductDetailScreenActivity.this).getEmail() + "\",\"p_id\":\"" + product.getP_id() + "\"}");
                    }
                } else {
                    Intent intent4 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent4);

                }
            }
        });
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        imgView=(ImageView)findViewById(R.id.img);
        btnPlus = (ImageView) findViewById(R.id.btnPlus);
        btnMinus = (ImageView) findViewById(R.id.btnMinus);
        txtName = (TextView) findViewById(R.id.title1);
        txtShortDesc = (TextView) findViewById(R.id.short_desc);
        txtLongDesc = (TextView) findViewById(R.id.long_desc);
        offer = (TextView) findViewById(R.id.offer);
        brand = (TextView) findViewById(R.id.txtBrand);
        size = (TextView) findViewById(R.id.txtSize);
        mrp = (TextView) findViewById(R.id.txtMrp);
        stock = (TextView) findViewById(R.id.txtStock);
        expiry = (TextView) findViewById(R.id.txtExpiry);
        add_to_fav = (Button) findViewById(R.id.add_to_fav);
        txtQuantity = (TextView) findViewById(R.id.quantity);
        btnAddToCart = (Button) findViewById(R.id.addToCartButton);
    }

    private void setFont() {
        txtName.setTypeface(Utils.setLatoFont(this));
        mrp.setTypeface(Utils.setLatoFont(this));
        size.setTypeface(Utils.setLatoFont(this));
        brand.setTypeface(Utils.setLatoFont(this));
        offer.setTypeface(Utils.setLatoFont(this));
        txtShortDesc.setTypeface(Utils.setLatoFont(this));
        txtLongDesc.setTypeface(Utils.setLatoFont(this));
        stock.setTypeface(Utils.setLatoFont(this));
        btnAddToCart.setTypeface(Utils.setLatoFont(this));
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            SpannableString s = new SpannableString("Product Detail");
            s.setSpan(new TypefaceSpan(this, "Jacquard.ttf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toolbar.setTitle(s);
            setSupportActionBar(toolbar);

        }
    }

    private void setView() {

        try {

            //     Picasso.with(getBaseContext()).load(Constants.PRODUCT_IMG_PATH + product.getImgs().get(0).getImg_url()).resize(400, 300).error(R.drawable.logo1_grey).centerInside().into(imgView);
            adapter = new CustomPagerAdapter(ProductDetailScreenActivity.this);
            viewPager.setAdapter(adapter);



        } catch (Exception e) {
            Log.e("", Constants.PRODUCT_IMG_PATH + product.getImgs().toString());
            e.printStackTrace();
        }

        txtName.setText(product.getProduct_name());
        txtShortDesc.setText(product.getShort_desc());
        txtLongDesc.setText(product.getShort_desc() + "\n" + product.getLong_desc());
        brand.setText("Brand : " + product.getBrand_name());
        size.setText("Size : " + product.getSize());
        if (product.getSize().contains("NA")) {
            size.setVisibility(View.GONE);
        }
//
//        SpannableString spannable = new SpannableString("Rs. "+product.getMrp()+" Rs. "+Utils.discountPrice(product.getMrp(),product.getPer_discount()));
//        spannable.setSpan(new StrikethroughSpan(),0,product.getMrp().length()+3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        mrp.setText("Price : ");
//        mrp.append(spannable);

        if (product.getOffer_name().equalsIgnoreCase("no offer")) {
            offer.setVisibility(View.INVISIBLE);
            mrp.setText("Price : Rs. " + product.getMrp());

        } else {
            offer.setText(product.getPer_discount() + "%");

            try {

                SpannableString spannable = new SpannableString("Rs. " + product.getMrp() + " Rs. " + Utils.discountPrice(product.getMrp(), product.getPer_discount()));
                spannable.setSpan(new StrikethroughSpan(), 0, product.getMrp().length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mrp.setText("Price : ");
                mrp.append(spannable);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //offer.setText(""+product.getOffer_name());

//        if (product.getStock().equals("0")) {
//
//            stock.setText("In stock : Out of stock \n call Us to arrange to for this item");
//            btnMinus.setVisibility(View.INVISIBLE);
//            btnPlus.setVisibility(View.INVISIBLE);
//            txtQuantity.setVisibility(View.INVISIBLE);
//
//        } else {
//            stock.setText("In stock : " + product.getStock());

//        }
        if (product.getExpiry_date().equals("NA")) expiry.setVisibility(View.GONE);
        else expiry.setText("Expiry date : " + product.getExpiry_date());

//        if(product.getFav.equals("NA") )  expiry.setVisibility(View.GONE);
//        else expiry.setText("Expiry date : "+product.getExpiry_date());

//        offer.setText(""+(product.getPer_discount())+"%");


    }

    public String getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Product product = (Product) bundle.getSerializable("product");
        return product.getP_id();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_settings: {
                startActivity(new Intent(this, MyCartActivity.class));
                return true;
            }

        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }


    class AddToFavTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        public AddToFavTask(Context context) {
            this.pd = pd;
            pd = new ProgressDialog(context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    Toast.makeText(ProductDetailScreenActivity.this, "" + new JSONObject(s).getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    if (s.contains("already")) {

                        isFav = true;
                        add_to_fav.setBackgroundResource(R.drawable.fav_selected);
                    } else {
                        isFav = true;
                        add_to_fav.setBackgroundResource(R.drawable.fav_selected);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class RemoveFromFavTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        public RemoveFromFavTask(Context context) {
            this.pd = pd;
            pd = new ProgressDialog(context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    Toast.makeText(ProductDetailScreenActivity.this, "" + new JSONObject(s).getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    if (s.contains("success")) {
                        isFav = false;
                        add_to_fav.setBackgroundResource(R.drawable.fav_unselected);
                    } else {
                        isFav = false;
                        add_to_fav.setBackgroundResource(R.drawable.fav_unselected);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
