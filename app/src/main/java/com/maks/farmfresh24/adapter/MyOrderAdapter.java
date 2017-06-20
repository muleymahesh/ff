
package com.maks.farmfresh24.adapter;

/**
 * Created by maks on 7/2/16.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maks.farmfresh24.R;
import com.maks.farmfresh24.model.OrderDetail;
import com.maks.farmfresh24.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Belal on 11/9/2015.
 */
public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    // private CategoryActivity context;
    private Context context;
    OnItemClickListener mItemClickListener;
    //List of Category
    List<OrderDetail> Category;
    Activity activity;

    public MyOrderAdapter(List<OrderDetail> Category, Context context) {
        super();
        this.context = context;
        //Getting all the Category
        this.Category = Category;
        activity = (Activity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_myorder_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        OrderDetail category = Category.get(position);

        Picasso.with(context).load(Constants.PRODUCT_IMG_PATH + category.getImgUrl()).resize(400, 220).centerCrop().into(holder.imageView);
        holder.txtName.setText(category.getProductName());
        holder.txtPrice.setText("Rs. " + category.getMrp());
        holder.txtQuantity.setText("Quantity: " + category.getQty());

        //holder.txtStatus.setText(category.getOrder_status());
        //holder.txtID.setText(category.getOId() + " Date: " + category.getDate());
    }

    @Override
    public int getItemCount() {
        return Category.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView txtName, txtStatus, txtPrice, txtID, txtQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.imgProduct);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
//            txtOrderDate.setTypeface(Utils.setLatoFontBold(activity));
            txtQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
//            txtOrderId.setTypeface(Utils.setLatoFontBold(activity));
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
//            txtOrderPrice.setTypeface(Utils.setLatoFontBold(activity));
            txtID = (TextView) itemView.findViewById(R.id.txtID);
//            txtID.setTypeface(Utils.setLatoFontBold(activity));
        }

        @Override
        public void onClick(View v) {
            //context.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
