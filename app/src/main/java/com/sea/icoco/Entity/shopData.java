package com.sea.icoco.Entity;

import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/4.
 */

public class shopData
{
    DataControler dataControler = MainActivity.dataControler;
    JSONArray shopData;
    JSONObject shopData_uid = new JSONObject();
    JSONArray myShopCoupon;
    JSONObject myShopCoupon_uid = new JSONObject();
    boolean loadSuccess = false;

    public void loadShopData()
    {
        class SelectShopData extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    shopData = new JSONArray(result);
                    for (int i = 0; i < shopData.length(); i++)
                    {
                        JSONObject shop = shopData.getJSONObject(i);
                        shopData_uid.put(shop.getString("uid"), shop);
                    }
                    loadSuccess = true;
                    loadMyShopCoupon();
                } catch (Exception e)
                {
                    Log.e("Debug ShopData", "SelectShopData Error" + e.toString());
                }
            }
        }
        String sql = "SELECT * FROM icoco.shop;";
        new SelectShopData().execute(sql);
    }

    public void loadMyShopCoupon()
    {
        class SelectMyShopCouponData extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    myShopCoupon = new JSONArray(result);
                    for (int i = 0; i < myShopCoupon.length(); i++)
                    {
                        JSONObject coupon = myShopCoupon.getJSONObject(i);
                        myShopCoupon_uid.put(coupon.getString("coupon_id"), coupon);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        String sql = "SELECT * FROM icoco.shop_coupon_budget where uid = "+dataControler.userData.getId();
        new SelectMyShopCouponData().execute(sql);
    }

    public JSONArray getShopData() { return shopData; }
    public JSONObject getShopData_uid() { return shopData_uid; }
    public JSONObject getMyShopCoupon_uid() {return myShopCoupon_uid; }
    public boolean onLoadSuccess() { return loadSuccess;}
}
