package com.ecommerce.dell.mcommerce.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.dell.mcommerce.activity.MainActivity;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.activity.product.DetailsActivity;
import com.ecommerce.dell.mcommerce.models.ProductData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<ProductData> itemList;
    Context context;

    ArrayList <ProductData> productDetailsArr;

    public void setProductDetailsArr(ArrayList<ProductData> productDetailsArr) {
        this.productDetailsArr = productDetailsArr;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView productImage, mainShareImage, mainCartImage;
        public TextView productName, productSale, productRealPrice, productDiscountPrice;
        public RelativeLayout relativeLayout_off;

        public MyViewHolder(View view){
            super(view);
            productImage = (ImageView) view.findViewById(R.id.product_image);
            mainShareImage = (ImageView) view.findViewById(R.id.mainShare_imageView);
            mainCartImage = (ImageView) view.findViewById(R.id.mainCart_imageView);
            productName = (TextView) view.findViewById(R.id.product_name);
            productSale = (TextView) view.findViewById(R.id.product_sale);
            productRealPrice = (TextView) view.findViewById(R.id.product_rprise);
            productDiscountPrice = (TextView) view.findViewById(R.id.product_dprise);
            relativeLayout_off = (RelativeLayout) view.findViewById(R.id.relativeLayout_off);
        }

    }
    public RecyclerAdapter(ArrayList<ProductData> itemsList, Context context) {
        this.itemList = itemsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_bestsellers,parent,false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ProductData item = itemList.get(position);

        holder.productSale.setText(item.getProductPercentage() + "% " + "\n" + context.getString(R.string.main_activity_off));
        Picasso.with(context).load(item.getProductImage()).into(holder.productImage);
        holder.productName.setText(item.getProductName());

        if (!(item.getProductPercentage().equals("0"))) {
            holder.productDiscountPrice.setPaintFlags(holder.productRealPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productDiscountPrice.setText(item.getProductPrice() + " " + context.getResources().getString(R.string.main_activity_currency));
            holder.productDiscountPrice.setVisibility(View.VISIBLE);
            holder.productSale.setVisibility(View.VISIBLE);
            holder.relativeLayout_off.setVisibility(View.VISIBLE);
        } else {
            holder.productDiscountPrice.setVisibility(View.INVISIBLE);
            holder.productSale.setVisibility(View.INVISIBLE);
            holder.relativeLayout_off.setVisibility(View.INVISIBLE);
        }
        holder.productRealPrice.setText(item.getProductDiscount() + " " + context.getResources().getString(R.string.main_activity_currency));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, DetailsActivity.class);
                myIntent.putExtra("image", item.getProductImage());
                myIntent.putExtra("name", item.getProductName());
                myIntent.putExtra("price", item.getProductPrice());
                myIntent.putExtra("sale", item.getProductPercentage());
                myIntent.putExtra("discount", item.getProductDiscount());
                myIntent.putExtra("description", item.getProductDescreiption());
                myIntent.putExtra("features", "No features");
                myIntent.putExtra("URL",item.getpURL());

                myIntent.putExtra("fromServer", "yes");
                myIntent.putExtra("id", item.getProductID());
                myIntent.putExtra("code",item.getpSKU());
                myIntent.putExtra("size",item.getpSize());
                myIntent.putExtra("type",item.getpType());
                myIntent.putExtra("origins",item.getpOrigin());
                myIntent.putExtra("fragrance",item.getpFragrance());

                context.startActivity(myIntent);
            }
        });

        holder.mainShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click", "" + item.getProductName());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://shop.arabianoud.com/index.php/" + item.getpURL().substring(43, item.getpURL().length()));
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });

        holder.mainCartImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(item.getProductPercentage()) > 0){
                    saveInSharedPreference("item", item.getProductImage(), item.getProductName(), item.getProductDiscount(), "1", Integer.parseInt(item.getProductID()));
                } else {
                    saveInSharedPreference("item", item.getProductImage(), item.getProductName(), item.getProductPrice(), "1", Integer.parseInt(item.getProductID()));
                }

//                saveInSharedPreference("item", item.getProductImage(), item.getProductName(), item.getProductDiscount(), "1", Integer.parseInt(item.getProductID()));

                Toast.makeText(context, context.getResources().getString(R.string.itemaddedTocart), Toast.LENGTH_SHORT).show();

                try {
                    SharedPreferences carts = context.getSharedPreferences("item", 0);
                    MainActivity.cartItems = carts.getInt("count", 0);

                    MainActivity.reaiCartCount = 0;
                    for (int i = 1; i <= MainActivity.cartItems; i++) {
                        if (carts.getInt("id" + i, 0) != 0) {
                            MainActivity.reaiCartCount++;
                        }
                    }
                    MainActivity.cartCount.setTitle(String.valueOf(MainActivity.reaiCartCount));

                } catch (Exception ex) {
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void saveInSharedPreference( String sharedPreferencesName , String imageBitmap , String title , String price , String amount , int id) {

        String imagePath ="";
        imagePath = imageBitmap;

        boolean itemExists = false;
        //save data to shared preferences
        SharedPreferences ss = context.getSharedPreferences(sharedPreferencesName, 0);
        SharedPreferences.Editor edit = ss.edit();
        int itemsCount = ss.getInt("count", 0);

        if(itemsCount == 0){
            ++itemsCount;
            edit.putString("image" + id, imagePath);
            edit.putString("title"+id, title);
            edit.putString("price"+id, price);
            edit.putString("amount" + id, amount);
            edit.putInt("id" + itemsCount, id);
            edit.putInt("count", itemsCount);
            edit.commit();
        } else {
            for (int index = 1; index <= itemsCount ; index++) {
                int ids = ss.getInt("id"+index, 0);
                if (ids == id){
                    edit.putString("amount" + id, String.valueOf(Integer.parseInt(ss.getString("amount" + id, ""))+1));
                    edit.commit();

                    itemExists = true;
                    break;
                }
            }

            if (!itemExists){
                ++itemsCount;
                edit.putString("image" + id, imagePath);
                edit.putString("title"+id, title);
                edit.putString("price"+id, price);
                edit.putString("amount" + id, amount);
                edit.putInt("id" + itemsCount, id);
                edit.putInt("count", itemsCount);
                edit.commit();
            }
        }
    }
}
