package com.sea.icoco.Entity;

import android.util.Log;

import com.sea.icoco.Control.MySQL.MySQL_Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/2.
 */
public class friendsData
{
    JSONArray friendsData;
    JSONObject friendsDataUID;
    boolean loadUIDSuccess = false,loadCTBCAcSuccess = false;
    public void loadFbFriends(JSONArray jsonArray) throws JSONException
    {
        this.friendsData = jsonArray;
        getFriendUID();
    }

    private void getFriendUID() throws JSONException
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < friendsData.length() ; i++)
        {
            sb.append(friendsData.getJSONObject(i).getString("id"));
            if (i < friendsData.length()-1) sb.append(",");
        }
        String sql = "SELECT uid,fbid FROM icoco.user where fbid in ("+sb.toString()+");";
        new friendUID_Select().execute(sql);
    }

    private void getFriendCTBCAC()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < friendsData.length() ; i++)
        {
            try
            {
                sb.append(friendsData.getJSONObject(i).getString("uid"));
                if (i < friendsData.length()-1) sb.append(",");

            }catch (Exception e){ Log.e("Debug Friend Data", "getFriendCTBCAC Fail "+e.toString());}
        }


        class searchBankAc extends MySQL_Select
        {
            @Override
            protected void onPostExecute(String result)
            {
                try
                {
                    if(!result.equals("[]"))
                    {
                        JSONArray data = new JSONArray(result);
                        for (int i = 0 ; i < data.length() ; i++)
                        {
                            try
                            {
                                JSONArray bank = friendsDataUID.getJSONObject(data.getJSONObject(i).getString("uid")).getJSONArray("bank");
                                bank.put(data.getJSONObject(i).getString("ac"));
                            }
                            catch (Exception e)
                            {
                                JSONArray bank = new JSONArray();
                                bank.put(data.getJSONObject(i).getString("ac"));
                                friendsDataUID.getJSONObject(data.getJSONObject(i).getString("uid")).put("bank",bank);
                            }
                        }
                    }
                    for (int i = 0 ; i < friendsData.length() ; i++)
                    {
                        JSONObject friend = friendsData.getJSONObject(i);
                        try
                        {
                            JSONArray bank = friend.getJSONArray("bank");
                        }
                        catch (Exception e)
                        {
                            friend.put("bank",new JSONArray());
                        }
                    }
                    loadCTBCAcSuccess = true;
                }
                catch (Exception e) { Log.e("frined Data"," searchBankAc Faill"+e.toString()); }
            }
        }
        String sql = "SELECT uid,ac FROM icoco.user_bank_acc where uid in ("+sb.toString()+");";
        new searchBankAc().execute(sql);
    }

    public JSONArray getFriendsData()
    {
        return friendsData;
    }

    public boolean isLoadUIDSuccess() { return loadUIDSuccess;}
    public boolean isLoadCTBCAcSuccess() { return loadCTBCAcSuccess; }


    private class friendUID_Select extends MySQL_Select
    {
        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                JSONArray uidDatas = new JSONArray(result);
                for ( int i = 0 ; i < uidDatas.length() ; i++) // 逐一比對 回傳資料
                {
                    JSONObject uidData = uidDatas.getJSONObject(i);
                    for (int j = 0 ; j < friendsData.length() ; j ++) // 比對 回傳資料 與 好友資料
                    {
                        JSONObject friend = friendsData.getJSONObject(j);
                        try
                        {
                            if (friend.getString("id").equals(uidData.getString("fbid"))) //如果好友資料的 ID == 回傳資料的 FBID
                            {
                                friend.put("uid",uidData.getString("uid")); //好友新增 ID == 回傳資料的 UID
                                break;
                            }
                        }catch (Exception e){}
                    }
                }
                loadUIDSuccess = true;
                buildUIDfriendData();
//                Log.d("Debug Friend Data", "Friend UID Load Success");
            } catch (JSONException e)
            {
                Log.e("Debug Friend Data", "Friend UID Load Fail "+e.toString());
                Log.e("Debug Friend Data", "Friend UID Load Fail "+result);
            }
        }
    }

    private void buildUIDfriendData()
    {
        friendsDataUID = new JSONObject();
        for ( int i = 0 ; i < friendsData.length() ; i++)
        {
            try
            {
                JSONObject friend = friendsData.getJSONObject(i);
                friendsDataUID.put(friend.getString("uid"),friend);
            }catch (Exception e) { Log.e("Debug Friend Data", "buildUIDfriendData Fail "+e.toString());}
        }
//        Log.d("debug","friend data uid= "+friendsDataUID.toString());
        getFriendCTBCAC();
    }

    public JSONObject getFriendsDataUID() {return  friendsDataUID;}
}
