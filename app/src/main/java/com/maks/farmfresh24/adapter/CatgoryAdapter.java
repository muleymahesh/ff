
package com.maks.farmfresh24.adapter;

/**
 * Created by maks on 7/2/16.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.maks.farmfresh24.MainActivity;
import com.maks.farmfresh24.R;
import com.maks.farmfresh24.model.Category;
import com.maks.farmfresh24.utils.Constants;
import com.maks.farmfresh24.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Belal on 11/9/2015.
 */
public class CatgoryAdapter extends RecyclerView.Adapter<CatgoryAdapter.ViewHolder> {

   // private CategoryActivity context;
   private MainActivity context;
    OnItemClickListener mItemClickListener;
    //List of Category
    List<Category> Category;
    Activity activity;

    public CatgoryAdapter(List<com.maks.farmfresh24.model.Category> Category, MainActivity context){
        super();
        //Getting all the Category
        this.Category = Category;
        this.context = context;
        activity = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Category category =  Category.get(position);
        Picasso.with(context).load(Constants.PRODUCT_IMG_PATH+category.getCat_img()).resize(400,220).centerCrop().into(holder.imageView);
        holder.textViewName.setText(category.getCat_name());
    }

    @Override
    public int getItemCount() {
        return Category.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        public TextView textViewName;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            textViewName = (TextView) itemView.findViewById(R.id.title1);
            textViewName.setTypeface(Utils.setLatoFontBold(activity));
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
