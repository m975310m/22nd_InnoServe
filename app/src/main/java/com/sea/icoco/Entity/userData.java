package com.sea.icoco.Entity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sea.icoco.Control.BankAPI.CTBC.CTBC_Login;
import com.sea.icoco.Control.BankAPI.ESUN.Bank.ESUN_Login;
import com.sea.icoco.Control.BankAPI.Emulation.Login;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.loginSelect;
import com.sea.icoco.LinkCTBCActivity;
import com.sea.icoco.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/1.
 */
public class userData
{
    private String id = "",fbid = "",ac = "",name = "",phone = "",email = "",birthday = "",gender = "",ctbcAc="",ctbcPin="",CustID="";
    private Intent intent = new Intent();
    private DataControler dataControler = MainActivity.dataControler;
    JSONObject userData = new JSONObject();
    boolean EmulationMode = dataControler.EmulationMode;
    boolean CTBCMode = dataControler.CTBCMode;
    boolean ESUNMode = dataControler.ESUNMode;

    public void login(JSONObject jsonObject, Context context,Class c) throws JSONException
    {
        this.userData = jsonObject;
//        Log.d("Debug userData","Loging ...");
        id = jsonObject.getString("uid");
        try {fbid = jsonObject.getString("fbid");} catch(Exception e) {Log.e("Debug userData","No value for fbid");}
        try {ac = jsonObject.getString("ac");} catch (Exception e) {Log.e("Debug userData","No value for ac");}
        try {name = jsonObject.getString("name");} catch (Exception e) {Log.e("Debug userData","No value for name");}
        try {phone = jsonObject.getString("phone");} catch (Exception e) {Log.e("Debug userData","No value for phone");}
        try {email = jsonObject.getString("email");} catch (Exception e) {Log.e("Debug userData","No value for email");}
        try {gender = jsonObject.getString("gender");} catch (Exception e) {Log.e("Debug userData","No value for gender");}
        try {birthday = jsonObject.getString("birthday");} catch (Exception e) {Log.e("Debug userData","No value for birthday");}
        try {ctbcAc = jsonObject.getString("ctbcAc");} catch (Exception e) {Log.e("Debug userData","No value for ctbcAc");}
        try {ctbcPin = jsonObject.getString("ctbcPin");} catch (Exception e) {Log.e("Debug userData","No value for ctbcPin");}
        try {CustID = jsonObject.getString("CustID");} catch (Exception e) {Log.e("Debug userData","No value for CustID");}
        Log.d("Debug userData","Loging Success !!");

        dataControler.creditCardData.loadCreditData(); // 讀取信用卡資料
        dataControler.budgetData.loadBudget(); // 讀取預算管理資料
        dataControler.couponData.loadCouponData();// 讀取優惠卷資料
        dataControler.shopData.loadShopData(); // 讀取商家資料
        dataControler.aaMessageData.loadAAMessage(); // 讀取拆帳邀請資料
        dataControler.payData.loadPayData(); // 讀取付款資料

        if (!CustID.isEmpty() && !ctbcAc.isEmpty() && !ctbcPin.isEmpty())
        {
            if (EmulationMode)
            {
                new Login().execute(CustID,ctbcAc,ctbcPin);//登入CTBC
            }
            else if (CTBCMode)
            {
                new CTBC_Login().execute(CustID,ctbcAc,ctbcPin);//登入CTBC
            }
            else if (ESUNMode)
            {
                new ESUN_Login().execute(ctbcAc,ctbcPin);//登入ESUNBank
            }

            intent.setClass(context,c);
            context.startActivity(intent);
        }
        else
        {
            intent.setClass(context, LinkCTBCActivity.class);
            context.startActivity(intent);
        }





    }

//    public void reLogin(JSONObject jsonObject) throws JSONException
//    {
//        this.userData = jsonObject;
//        Log.d("Debug userData","Loging ...");
//        id = jsonObject.getString("uid");
//        try {fbid = jsonObject.getString("fbid");} catch(Exception e) {Log.e("Debug userData","No value for fbid");}
//        try {ac = jsonObject.getString("ac");} catch (Exception e) {Log.e("Debug userData","No value for ac");}
//        try {name = jsonObject.getString("name");} catch (Exception e) {Log.e("Debug userData","No value for name");}
//        try {phone = jsonObject.getString("phone");} catch (Exception e) {Log.e("Debug userData","No value for phone");}
//        try {email = jsonObject.getString("email");} catch (Exception e) {Log.e("Debug userData","No value for email");}
//        try {gender = jsonObject.getString("gender");} catch (Exception e) {Log.e("Debug userData","No value for gender");}
//        try {birthday = jsonObject.getString("birthday");} catch (Exception e) {Log.e("Debug userData","No value for birthday");}
//        try {ctbcAc = jsonObject.getString("ctbcAc");} catch (Exception e) {Log.e("Debug userData","No value for ctbcAc");}
//        try {ctbcPin = jsonObject.getString("ctbcPin");} catch (Exception e) {Log.e("Debug userData","No value for ctbcPin");}
//        try {CustID = jsonObject.getString("CustID");} catch (Exception e) {Log.e("Debug userData","No value for CustID");}
//        Log.d("Debug userData","reLoging Success !!");
//
//        if (!CustID.isEmpty() && !ctbcAc.isEmpty() && !ctbcPin.isEmpty()) //登入CTBC
//            new MicrosoftVision().execute(CustID,ctbcAc,ctbcPin);
//        dataControler.creditCardData.loadCreditData(); // 讀取信用卡資料
//    }

    public void signup(JSONObject jsonObject,Context context,Class c) throws JSONException
    {
        Log.d("Debug userData","SignUp ...");

        fbid = jsonObject.getString("id");
        name = jsonObject.getString("name");

        try {email = jsonObject.getString("email");} catch(Exception e) { Log.e("Debug userData",e.toString()); }
        try {gender = jsonObject.getString("gender");} catch(Exception e) { Log.e("Debug userData",e.toString()); }
        try {birthday = jsonObject.getString("birthday");} catch(Exception e) { Log.e("Debug userData",e.toString()); }

        String sql = "INSERT INTO `icoco`.`user` (`fbid`, `name`, `email`, `gender`, `birthday`) " +
                "VALUES ('"+fbid+"', '"+name+"', '"+email+"', '"+gender+"', '"+birthday+"');";
        new MySQL_Execute().execute(sql);
        new loginSelect(context,c).execute("SELECT * FROM icoco.user where `fbid` = "+getId());
    }

    public String getId() { return id; }
    public String getFbid() { return fbid; }
    public String getAc() { return ac; }
    public String getName() { return name;}
    public String getCreditcatd() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getBirthday() { return birthday; }
    public String getGender() { return gender; }
    public JSONObject getAll() {return userData;}
    public String getCustID (){return CustID;}
    public void setCustID(String custID)
    {
        this.CustID = custID;
    }

}

