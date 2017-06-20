package com.maks.farmfresh24.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maks.farmfresh24.R;


/**
 * Created by Csl-016 on 08-09-2015.
 */
public class DrawerListAdapter extends BaseAdapter {
    /**
     * The drawer text.
     */
    private String[] drawerText;

    /**
     * The drawerimage.
     */
    private int[] drawerimage;

    /**
     * The context.
     */
    private Context context;


    /**
     * Instantiates a new drawer list adapter.
     *
     * @param mDrawerText the m drawer text
     * @param mContext    the m context
     */
  /*  public DrawerListAdapter(Context mContext, String[] mDrawerText, int[] drawerimage, String tag) {
        super();
        this.drawerText = mDrawerText;
        this.drawerimage = drawerimage;
        this.context = mContext;
        this.tag=tag;
    }*/
    public DrawerListAdapter(Context mContext, String[] mDrawerText) {
        super();
        this.drawerText = mDrawerText;
        this.drawerimage = drawerimage;
        this.context = mContext;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return drawerText.length;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * The Class ViewHolder.
     */
    private class ViewHolder {

        /**
         * The txt_listitems.
         */
        public TextView txt_Drawerlistitems;
        // public ImageView img_Icon;
        public View mDivider;

        /** The divider. */
        // public View divider;

        /**
         * The relative.
         */
        public RelativeLayout relative;
        //  public TextView textView;
        public RelativeLayout linearLayout;

    }


    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        //   TextView textView=(TextView)convertView.findViewById(R.id.textView);


        holder = new ViewHolder();
        holder.txt_Drawerlistitems = (TextView) convertView
                .findViewById(R.id.txt_Drawerlistitems);
        //   holder.img_Icon = (ImageView) convertView
        //           .findViewById(R.id.img_Icon);

        holder.mDivider = convertView
                .findViewById(R.id.drawer_divider);

        //  holder.divider = convertView.findViewById(R.id.divider);
        // holder.divider.setBackgroundColor(Color.WHITE);
        holder.relative = (RelativeLayout) convertView
                .findViewById(R.id.relative_Drawer);
        // holder.textView=(TextView)convertView.findViewById(R.id.textView);
        holder.linearLayout = (RelativeLayout) convertView.findViewById(R.id.Drawer_Header);
        holder.relative.setClickable(false);
        convertView.setTag(holder);

       /* if (position == 0) {
            holder.img_Icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_drawer_stats));
        } else if (position == 1) {
            holder.img_Icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_drawer_amc));
        } else if (position == 2) {
            holder.img_Icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_drawer_employee));
        } else if (position == 3) {
            holder.img_Icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_drawer_spare));
            holder.mDivider.setVisibility(View.VISIBLE);
        }*/

      /*  if (position == 6 && tag.equals(context.getResources().getString(R.string.radioBusiness))) {
            holder.mDivider.setVisibility(View.VISIBLE);
        }
*/
        holder.txt_Drawerlistitems.setText(drawerText[position]);
        //       holder.img_Icon.setImageResource(drawerimage[position]);

       /* if(position!=0){
            holder.linearLayout.setVisibility(View.GONE);
        }*/
        return convertView;
    }

}


