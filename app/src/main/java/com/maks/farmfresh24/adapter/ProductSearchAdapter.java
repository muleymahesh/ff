package com.maks.farmfresh24.adapter;

/**
 * Created by maks on 7/2/16.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maks.farmfresh24.R;
import com.maks.farmfresh24.SearchResultsActivity;
import com.maks.farmfresh24.model.Product;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Belal on 11/9/2015.
 */
public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.ViewHolder> {

    private SearchResultsActivity context;
    OnItemClickListener mItemClickListener;
    //List of productList
    List<Product> productList;
    Activity activity;

    public ProductSearchAdapter(List<Product> productList, SearchResultsActivity context){
        super();
        //Getting all the productList
        this.productList = productList;
        this.context = context;
        activity = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_product_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Product product =  productList.get(position);
try {
    if (product.getImgs() != null)
        Picasso.with(context).load(Constants.PRODUCT_IMG_PATH + product.getImgs().get(0).getImg_url()).centerInside().resize(300, 300).error(R.drawable.logo1_grey).into(holder.imageView);
}catch (Exception e){e.printStackTrace();}

        holder.textViewName.setText(product.getProduct_name());

        holder.textDisc.setText(product.getOffer_name());

        if(product.getOffer_name().equalsIgnoreCase("no offer") ){
            holder.textPrice.setText("Rs. " +product.getMrp());

            holder.textDisc.setVisibility(View.INVISIBLE);
        } else {
            holder.textDisc.setVisibility(View.VISIBLE);
            try {

                SpannableString spannable = new SpannableString("Rs. " + product.getMrp() + " Rs. " + Utils.discountPrice(product.getMrp(), product.getPer_discount()));
                spannable.setSpan(new StrikethroughSpan(), 0, product.getMrp().length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.textPrice.setText(spannable);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        public TextView textViewName;
        public TextView textPrice;
        public TextView textDisc;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            textViewName = (TextView) itemView.findViewById(R.id.title1);
            textDisc = (TextView) itemView.findViewById(R.id.discount);
            textPrice = (TextView) itemView.findViewById(R.id.price);
//            textViewName.setTypeface(Utils.setLatoFontBold(activity));

        }

        @Override
        public void onClick(View v) {
            context.onItemClick(v,getPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
