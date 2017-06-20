package com.maks.farmfresh24.adapter;

/**
 * Created by maks on 7/2/16.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maks.farmfresh24.MyCartActivity;
import com.maks.farmfresh24.R;
import com.maks.farmfresh24.model.ShoppingCart;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Belal on 11/9/2015.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private MyCartActivity context;
    OnItemClickListener mItemClickListener;
    //List of Category
    List<ShoppingCart> shoppingCart;
    Activity activity;

    public CartAdapter(List<ShoppingCart> shoppingCart, MyCartActivity context){
        super();
        //Getting all the Category
        this.shoppingCart = shoppingCart;
        this.context = context;
        activity = (Activity)context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_product_shoppingcart, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ShoppingCart cart =  shoppingCart.get(position);

        Picasso.with(context).load(Constants.PRODUCT_IMG_PATH+cart.getProduct().getImgs().get(0).getImg_url()).centerInside().resize(300,300).into(holder.imageView);
       holder.textViewName.setText(cart.getProduct().getProduct_name());

        SpannableString spannable = new SpannableString("Rs. "+cart.getProduct().getMrp()+" Rs. "+ Utils.discountPrice(cart.getProduct().getMrp(),cart.getProduct().getPer_discount()));
        spannable.setSpan(new StrikethroughSpan(),0,cart.getProduct().getMrp().length()+3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.textPrice.setText(spannable);

        Log.e("q",cart.getQuantity());
        holder.textQuantity.setText(cart.getQuantity());

    }

    @Override
    public int getItemCount() {
        return shoppingCart.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView,btnMinus,btnPlus;
        public TextView textViewName;
        public TextView textPrice;
        public TextView textQuantity;
        public TextView txtRs;
        public Button btnDel;

        public ViewHolder(View itemView) {
            super(itemView);
//            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            btnPlus=(ImageView)itemView.findViewById(R.id.btnPlus);
            btnMinus=(ImageView)itemView.findViewById(R.id.btnMinus);

            textViewName = (TextView) itemView.findViewById(R.id.title1);
            textPrice = (TextView) itemView.findViewById(R.id.price);
            textQuantity = (TextView) itemView.findViewById(R.id.quantity);
            txtRs = (TextView) itemView.findViewById(R.id.txtRs);
            btnDel = (Button) itemView.findViewById(R.id.btnDel);

            btnDel.setOnClickListener(this);
            btnPlus.setOnClickListener(this);
            btnMinus.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            context.onItemClick(v,getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
