package com.sea.icoco.Control.Action;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/3.
 */

public class AutoDetectionCoupon_Location
{
    DataControler dataControler = MainActivity.dataControler;
    boolean locked = false;
//    Context context;
    public void startDetection()
    {
        try
        {
//            this.context = context;
            if (dataControler.shopData.onLoadSuccess() && dataControler.gpsData.getLocationLoadSuccess())
            {
                locked = true;
                Double longitude = dataControler.gpsData.getMyLocation().getLongitude();
                Double latitude = dataControler.gpsData.getMyLocation().getLatitude();
//                Double longitude = 121.474361;
//                Double latitude = 25.023481;

                ArrayList<String> sendShopList = new ArrayList<String>();
                for (int i = 0 ; i < dataControler.shopData.getShopData().length() ; i++)
                {
                    JSONObject shop = dataControler.shopData.getShopData().getJSONObject(i);
                    Double shopLongitude = Double.parseDouble(shop.getString("longitude"));
                    Double shopLatitude = Double.parseDouble(shop.getString("latitude"));

                    if (longitude >= (shopLongitude -0.05) && longitude <= (shopLongitude +0.05)
                            && latitude >= (shopLatitude -0.05) && latitude <= (shopLatitude+0.05))
                    {
                        sendShopList.add(shop.getString("uid"));
                    }
                }


                StringBuilder shopList = new StringBuilder();
                for (int i = 0 ; i < sendShopList.size() ; i++)
                {
                    shopList.append(sendShopList.get(i));
                    if (i < sendShopList.size()-1) shopList.append(",");
                }

                class SelectShopCoupon extends MySQL_Select
                {
                    @Override
                    protected void onPostExecute(String result)
                    {
                        try
                        {
                            JSONArray shopCoupons = new JSONArray(result);
                            ArrayList<String> sendCouponList = new ArrayList<String>();

                            for (int i = 0 ; i < shopCoupons.length() ; i++)
                            {
                                try
                                {
                                    dataControler.couponData.getCouponData_id().getJSONObject(shopCoupons.getJSONObject(i).getString("coupon_id"));
                                }catch (Exception e)
                                {
                                    if (shopCoupons.getJSONObject(i).getString("rule").equals("location"))
                                        sendCouponList.add(shopCoupons.getJSONObject(i).getString("coupon_id"));
                                }
                            }
                            if (sendCouponList.size() > 0)
                            {
                                sendArryayCouponList(sendCouponList);
                            }
                            else
                            {
                                locked = false;
                            }

                        } catch (JSONException e)
                        {
                            locked = false;
                            Log.e("Debug Detection Locate","Detection Fail");
                        }
                    }
                }
                if (shopList.length() > 0)
                {
                    String sql  = "SELECT * FROM icoco.shop_coupon_budget where uid in ("+shopList.toString()+");";
                    new SelectShopCoupon().execute(sql);
                }
                else if  (shopList.length() == 0)
                {
                    locked = false;
                }

            }
        }catch (Exception e){
            locked = false;
            Log.e("Debug Detection Locate","Detection Fail");
        }

    }

    private void sendArryayCouponList(final ArrayList<String> sendCouponList)
    {
        class sendCoupon extends MySQL_Execute
        {
            @Override
            protected void onPostExecute(String result)
            {
                if(result.equals("true"))
                {
                    dataControler.couponData.loadCouponData();
                    locked = false;
                    Log.d("debug location","send Coupon locaiton");
//                    new AlertDialog.Builder(context).setTitle("優惠卷接收狀態").setMessage("接收了 "+String.valueOf(sendCouponList.size())+" 張優惠卷").setNegativeButton("OK",null).show();
                }
            }
        }

        StringBuilder SqlBuilder = new StringBuilder();
        for (int i = 0 ; i < sendCouponList.size() ; i ++)
        {
            String sql = "INSERT INTO `icoco`.`user_coupon` (`coupon_id`, `user_uid`) VALUES ('"+sendCouponList.get(i)+"', '"+dataControler.userData.getId()+"');";
            SqlBuilder.append(sql);
        }
        new sendCoupon().execute(SqlBuilder.toString());
    }

    public void sendCoupon(String coupon_id)
    {

    }

    public boolean isLocked()
    {
        return locked;
    }
}
