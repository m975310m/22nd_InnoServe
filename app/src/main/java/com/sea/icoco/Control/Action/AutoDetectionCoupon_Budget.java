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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/3.
 */

public class AutoDetectionCoupon_Budget
{
    DataControler dataControler = MainActivity.dataControler;
    boolean locked = false;
//    Context context;
    public void startDetection()
    {
//        this.context = context;
        class SelectShopCoupon extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d");  //日期時間
                    Integer day = Integer.parseInt(dateFormat.format(now));
                    Double scale = dataControler.budgetData.getScale();
                    if (!result.equals("[]"))
                    {
                        JSONArray shopCouponData = new JSONArray(result);
                        ArrayList<String> sendCouponList = new ArrayList<String>();
                        for (int i = 0 ; i < shopCouponData.length() ; i ++)
                        {
                            JSONObject shopCoupon = shopCouponData.getJSONObject(i);
                            String rule = shopCoupon.getString("rule");

                            if (rule.equals("month"))
                            {
                                Integer rule_day = Integer.parseInt(shopCoupon.getString("rule_day"));
                                Double rule_scale = Double.parseDouble(shopCoupon.getString("rule_scale"));
                                if (day > rule_day && scale < rule_scale)
                                {
                                    try
                                    {
                                        JSONObject coupon = dataControler.couponData.getCouponData_id().getJSONObject(shopCoupon.getString("coupon_id"));
                                    }catch (Exception e)
                                    {
                                        sendCouponList.add(shopCoupon.getString("coupon_id"));
                                    }
                                }
                            }
                        }

                        if (sendCouponList.size()>0)
                            sendArryayCouponList(sendCouponList);
                        else
                            locked = false;
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        String sql = "SELECT * FROM icoco.shop_coupon_budget;";
        locked = true;
        new SelectShopCoupon().execute(sql);
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
