package com.sea.icoco.ActivitySupporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;
import com.sea.icoco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by AndyChuo on 2016/4/5.
 * 建立自訂義清單:
 *      單一項目利用 list_item 介面元件來建立
 */
public class ListAdapter_Coupon extends BaseAdapter
{
    private AppCompatActivity activity;
    DataControler dataControler= MainActivity.dataControler;;
    JSONArray data = dataControler.couponData.getCouponDataOrderDesc();
    Integer newCoupon;
    private static LayoutInflater inflater = null;

    public ListAdapter_Coupon(AppCompatActivity a,Integer newCoupon)
    {
        this.newCoupon = newCoupon;
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount()
    {
        return dataControler.couponData.getCouponData().length();
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.coupon_item, null);
        }

        ImageView news_img = (ImageView) convertView.findViewById(R.id.news_img);
        if(position < newCoupon)
            news_img.setVisibility(View.VISIBLE);
        else
            news_img.setVisibility(View.GONE);

        TextView name = (TextView) convertView.findViewById(R.id.name_txv);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.list_image);
        TextView coupon_content_txv = (TextView) convertView.findViewById(R.id.couponContent_txv);
        TextView address_txv = (TextView) convertView.findViewById(R.id.address_txv);

        TextView money = (TextView) convertView.findViewById(R.id.money_txv);

        try
        {
            JSONObject coupon = data.getJSONObject(position);
            coupon_content_txv.setText(coupon.getString("coupon_content"));
//            imageView.setImageBitmap((Bitmap) friendData.get("picture"));
            money.setText(coupon.getString("off_amt"));

            JSONObject shop = dataControler.shopData.getShopData_uid().getJSONObject(coupon.getString("uid"));
            name.setText(shop.getString("shop_name"));
            address_txv.setText(shop.getString("address"));

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return convertView;
    }
}
