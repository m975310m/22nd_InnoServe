package com.sea.icoco.Entity;

import android.util.Log;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/3.
 */

public class couponData
{
    DataControler dataControler = MainActivity.dataControler;
    JSONObject couponData_id = new JSONObject();
    JSONArray couponData = new JSONArray();
    JSONArray couponData_id_temp;
    JSONObject couponData_cid_temp = new JSONObject();

    public void loadCouponData()
    {
        class selectCouponData extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    if(!result.equals("[]"))
                    {
                        couponData_id_temp = new JSONArray();
                        couponData_id_temp = new JSONArray(result);
                        ArrayList<String> detail_couponList = new ArrayList<String>();
                        boolean detailSearch = false;
                        for (int i = 0 ; i < couponData_id_temp.length() ; i++)
                        {
                            try
                            {
                                couponData_cid_temp.put(couponData_id_temp.getJSONObject(i).getString("coupon_id"),couponData_id_temp.getJSONObject(i));
                                JSONObject coupon = couponData_id.getJSONObject(couponData_id_temp.getJSONObject(i).getString("coupon_id"));
                            }
                            catch (Exception e)
                            {
                                detail_couponList.add(couponData_id_temp.getJSONObject(i).getString("coupon_id"));
                                detailSearch = true;
                            }
                        }

                        if(detailSearch)
                        {
                            class selectCouponDetail extends MySQL_Select
                            {
                                @Override
                                protected void onPostExecute(String result)
                                {
                                    try
                                    {
                                        Log.d("reuslt","result = "+result);
                                        JSONArray DetailCouponData = new JSONArray(result);

                                        for (int i = 0 ; i < DetailCouponData.length(); i++)
                                        {
                                            JSONObject couponDetail = DetailCouponData.getJSONObject(i);
                                            couponData_id.put(couponDetail.getString("coupon_id"),couponDetail);
                                            couponData.put(couponDetail);
                                        }
//                                        Log.d("debug CouponData","couponData_id = "+couponData_id.toString());
//                                        Log.d("debug CouponData","couponData = "+couponData.toString());
                                    } catch (Exception e)
                                    {
                                        Log.e("Debug CouponData","SelectCouponDetail Error"+e.toString());
                                    }
                                }
                            }

                            StringBuilder couponidList = new StringBuilder();
                            for (int i = 0 ; i < detail_couponList.size() ; i++)
                            {
                                couponidList.append(detail_couponList.get(i));
                                if (i<detail_couponList.size()-1)
                                    couponidList.append(",");
                            }
                            String sql = "SELECT * FROM icoco.shop_coupon_budget where coupon_id in ("+couponidList.toString()+");";
                            new selectCouponDetail().execute(sql);
                        }
                    }
                }catch (Exception e) { Log.e("Debug CouponData","SelectCouponData Error"+e.toString()); }
            }
        }
        String sql = "SELECT * FROM icoco.user_coupon where user_uid = "+dataControler.userData.getId()+";";
        new selectCouponData().execute(sql);
    }

    public JSONObject getCouponData_id() { return couponData_id; }
    public JSONArray getCouponData() { return couponData; }
    public JSONArray getCouponDataOrderDesc()
    {
        JSONArray jsonArray = new JSONArray();
        for (int i = couponData.length()-1 ; i > -1 ; i--)
            try
            {
                jsonArray.put(couponData.getJSONObject(i));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        return  jsonArray;
    }
    public void setAllRead()
    {
        StringBuilder all_couponid = new StringBuilder();
        for (int i = 0 ; i < couponData_id_temp.length() ; i++)
        {
            try
            {
                couponData_id_temp.getJSONObject(i).put("read","1");
                all_couponid.append(couponData_id_temp.getJSONObject(i).getString("coupon_id"));
                if (i < couponData.length()-1)
                    all_couponid.append(",");
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        String sql = "UPDATE `icoco`.`user_coupon` SET `read`='1' WHERE `user_uid`='"+dataControler.userData.getId()+"' and coupon_id in ("+all_couponid.toString()+");";
        new MySQL_Execute().execute(sql);
    }

    public Integer getNewCouponLength()
    {
        Integer length = 0;
        for (int i = 0 ; i < couponData.length() ; i++)
        {
            try
            {
                if (couponData_cid_temp.getJSONObject(couponData.getJSONObject(i).getString("coupon_id")).getString("read").equals("0"))
                {
                    length++;
                }
            } catch (JSONException e) { Log.e("Debug Coupon Get Length","Error" + e.toString()); }
        }
//        Log.d("debug length", String.valueOf(length));
        return length;
    }
}
