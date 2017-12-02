package com.ecommerce.dell.mcommerce.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.models.ProductData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList pDataArrayList;
    private static LayoutInflater inflater = null;

    ProductData pDetails;
    ArrayList <ProductData> productDetailsArr;

    public ProductAdapter(Context c, ArrayList al) {
        this.context = c;
        this.pDataArrayList = al;
    }

    public void setProductDetailsArr(ArrayList<ProductData> productDetailsArr) {
        this.productDetailsArr = productDetailsArr;
    }

    @Override
    public int getCount() {
        if (pDataArrayList.size() <= 0)
            return 0;
        return pDataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productRealPrice;
        TextView productDiscountPrice;
        TextView productSale;
        RelativeLayout relativeLayout_off;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.categories_products, parent, false);//get items from xml
            holder = new ViewHolder();
            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.productName = (TextView) convertView.findViewById(R.id.product_name);
            holder.productRealPrice = (TextView) convertView.findViewById(R.id.product_rprice);
            holder.productDiscountPrice = (TextView) convertView.findViewById(R.id.product_dprice);
            holder.productSale = (TextView) convertView.findViewById(R.id.product_sale);
            holder.relativeLayout_off = (RelativeLayout) convertView.findViewById(R.id.relativeLayout_off);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            pDetails = (ProductData) pDataArrayList.get(position);
        } catch (Exception e) {
            Log.d("ResultJsonParsing", e.toString());
        }
        Picasso.with(context).load(pDetails.getProductImage()).into(holder.productImage);
        holder.productName.setText(pDetails.getProductName());
        holder.productDiscountPrice.setText(pDetails.getProductDiscount()+" "+context.getResources().getString(R.string.main_activity_currency));
        holder.productRealPrice.setText(pDetails.getProductPrice()+" "+context.getResources().getString(R.string.main_activity_currency));
        holder.productSale.setText(pDetails.getProductPercentage() + "% " + "\n" + context.getResources().getString(R.string.main_activity_off));

        if(!(pDetails.getProductPercentage().equals("0"))) {
            holder.productDiscountPrice.setPaintFlags(holder.productDiscountPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productDiscountPrice.setText(pDetails.getProductPrice() + " " + context.getResources().getString(R.string.main_activity_currency));
            holder.productRealPrice.setText(pDetails.getProductDiscount()+" "+context.getResources().getString(R.string.main_activity_currency));
            holder.productSale.setText(pDetails.getProductPercentage() + "% " + "\n" + context.getResources().getString(R.string.main_activity_off));
            holder.productSale.setVisibility(View.VISIBLE);
            holder.productDiscountPrice.setVisibility(View.VISIBLE);
            holder.relativeLayout_off.setVisibility(View.VISIBLE);
        } else {
            holder.productSale.setVisibility(View.INVISIBLE);
            holder.productDiscountPrice.setVisibility(View.INVISIBLE);
            holder.relativeLayout_off.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
