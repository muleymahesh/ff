package com.farmfresh24.delivery.adapter;

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

import com.farmfresh24.delivery.MainActivity;
import com.farmfresh24.delivery.R;
import com.farmfresh24.delivery.model.Product;
import com.farmfresh24.delivery.utils.Utils;

import java.util.List;

/**
 * Created by Belal on 11/9/2015.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private MainActivity context;
    OnItemClickListener mItemClickListener;
    //List of Category
    List<Product> Category;
    Activity activity;

    public ProductAdapter(List<Product> Category, MainActivity context) {
        super();
        //Getting all the Category
        this.Category = Category;
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Product product = Category.get(position);

        if (product.getImgs() != null && !product.getImgs().isEmpty())
            //Picasso.with(context).load(Constants.PRODUCT_IMG_PATH + product.getImgs().get(0).getImg_url()).centerInside().resize(300, 300).placeholder(R.drawable.logo1_grey).into(holder.imageView);
            holder.textViewName.setText(product.getProduct_name());

        holder.textDisc.setText(product.getOffer_name());

        holder.textUnit.setText(product.getWeight());

        if (product.getOffer_name().equalsIgnoreCase("no offer")) {
            holder.textPrice.setText("Rs. " + product.getMrp());

            holder.textDisc.setVisibility(View.INVISIBLE);
        } else {
            holder.textDisc.setVisibility(View.VISIBLE);
//            holder.textDisc.setText(product.getPer_discount() + "%");

            try {

                SpannableString spannable = new SpannableString("Rs. " + product.getMrp() + " Rs. " + Utils.discountPrice(product.getMrp(), product.getPer_discount()));
                spannable.setSpan(new StrikethroughSpan(), 0, product.getMrp().length() + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.textPrice.setText(spannable);
            } catch (Exception e) {
            }
        }


        /*holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.txtQuantity.getText().toString().trim().equals("0")) {
                    holder.txtQuantity.setText("" + (Integer.parseInt(holder.txtQuantity.getText().toString()) - 1));

                    SQLiteUtil dbUtil = new SQLiteUtil();

                    ShoppingCart cart = dbUtil.getCartItem(context, Category.get(position).getP_id());
                    if (cart != null && cart.getQuantity().equals("1")) {
                        dbUtil.deleteCartItem(cart.getId(), context);

                    } else if (cart != null) {
                        cart = new ShoppingCart();
                        cart.setProduct_id(Category.get(position).getP_id());
                        cart.setProduct(Category.get(position));
                        cart.setQuantity(holder.txtQuantity.getText().toString());


                        cart.setQuantity("" + (Integer.parseInt(cart.getQuantity()) - 1));

                        dbUtil.deleteCartItem(cart.getId(), context);
                        dbUtil.insert(cart, context);
                    }


                }
            }
        });*/

        /*holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.txtQuantity.setText("" + (Integer.parseInt(holder.txtQuantity.getText().toString()) + 1));
                SQLiteUtil dbUtil = new SQLiteUtil();

                ShoppingCart cart = dbUtil.getCartItem(context, Category.get(position).getP_id());
                if (cart == null) {
                    cart = new ShoppingCart();
                    cart.setProduct_id(Category.get(position).getP_id());
                    cart.setProduct(Category.get(position));
                    cart.setQuantity(holder.txtQuantity.getText().toString());
                } else {

                    cart.setQuantity("" + (Integer.parseInt(cart.getQuantity()) + 1));
                }
                dbUtil.deleteCartItem(cart.getId(), context);
                dbUtil.insert(cart, context);


            }
        });
*/
    }

    @Override
    public int getItemCount() {
        return Category.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView textViewName;
        public TextView textPrice;
        public TextView textDisc;
        public TextView textUnit;
        public TextView txtQuantity;
        public ImageView btnPlus, btnMinus;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            textViewName = (TextView) itemView.findViewById(R.id.title1);
            textDisc = (TextView) itemView.findViewById(R.id.discount);
            textUnit = (TextView) itemView.findViewById(R.id.txtunit);
            textPrice = (TextView) itemView.findViewById(R.id.price);
//            textViewName.setTypeface(Utils.setLatoFontBold(activity));
            btnPlus = (ImageView) itemView.findViewById(R.id.btnPlus);
            btnMinus = (ImageView) itemView.findViewById(R.id.btnMinus);
            txtQuantity = (TextView) itemView.findViewById(R.id.quantity);

        }

        @Override
        public void onClick(View v) {
            //context.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
