package com.farmfresh24.delivery.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.farmfresh24.delivery.MainActivity;
import com.farmfresh24.delivery.R;
import com.farmfresh24.delivery.model.OrderPojo;
import com.farmfresh24.delivery.utils.Constants;
import com.farmfresh24.delivery.utils.HttpUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Deva on 08/03/2017.
 */

public class MyOrderListAdapter extends RecyclerView.Adapter<MyOrderListAdapter.ViewHolder> {

    // private CategoryActivity context;
    Context context;
    OnItemClickListener mItemClickListener;
    //List of Category
    List<OrderPojo> Category;
    Activity activity;

    public MyOrderListAdapter(List<OrderPojo> Category, Context context) {
        super();
        this.context = context;
        //Getting all the Category
        this.Category = Category;
        activity = (Activity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_my_orders, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final OrderPojo category = Category.get(position);

        holder.txtOrderDate.setText("Date: " + category.getDate());
        holder.txtOrderPrice.setText("Amount Rs. " + category.getAmount());
        holder.txtOrderId.setText("Order ID: " + category.getOId());
        holder.buttonStatusUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(category.getOId());
            }
        });
        holder.txtAddress.setText("Address: " + Html.fromHtml(category.getAddress()));
        /*holder.layoutMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("OrderList", (Serializable) category.getDetails());
                intent.putExtra("TotalAmount", category.getAmount());
                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return Category.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //public ImageView imageView;
        public TextView txtOrderDate, txtOrderId, txtOrderPrice;
        public LinearLayout layoutMyOrder;
        public EditText edtStatus;
        public Button buttonStatusUpdate;
        TextView txtAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            layoutMyOrder = (LinearLayout) itemView.findViewById(R.id.layoutMyOrder);
            txtOrderDate = (TextView) itemView.findViewById(R.id.txtMyOrderDate);
            txtOrderId = (TextView) itemView.findViewById(R.id.txtMyOrderOrderId);
            txtOrderPrice = (TextView) itemView.findViewById(R.id.txtMyOrderRupee);
            edtStatus = (EditText) itemView.findViewById(R.id.editTextStatus);
            buttonStatusUpdate = (Button) itemView.findViewById(R.id.buttonUpdate);
            txtAddress = (TextView) itemView.findViewById(R.id.textViewAddress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                }
            });
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private void changeStatus(String orderId) {

        new HttpAsyncTask().execute("{\"method\":\"update_order\",\"o_id\":\"" + orderId + "\"}");

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog d;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            d = new ProgressDialog(context);
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
                    JSONObject object = new JSONObject(result);
                    if (object.optString("result").equalsIgnoreCase("success")) {
                        Toast.makeText(context, object.optString("responseMessage"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, object.optString("responseMessage"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}