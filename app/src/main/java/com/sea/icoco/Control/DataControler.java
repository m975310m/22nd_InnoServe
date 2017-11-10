package com.sea.icoco.Control;

import android.content.Context;
import android.database.Cursor;

import com.sea.icoco.Entity.aaMessageData;
import com.sea.icoco.Entity.budgetData;
import com.sea.icoco.Entity.couponData;
import com.sea.icoco.Entity.creditCardData;
import com.sea.icoco.Entity.ctbcData;
import com.sea.icoco.Entity.esunData;
import com.sea.icoco.Entity.friendsData;
import com.sea.icoco.Entity.gpsData;
import com.sea.icoco.Entity.payData;
import com.sea.icoco.Entity.shopData;
import com.sea.icoco.Entity.transactionData;
import com.sea.icoco.Entity.userData;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/1.
 */
public class DataControler
{
    public static com.sea.icoco.Entity.userData userData;
    public static friendsData friendsData;
    public static com.sea.icoco.Entity.ctbcData ctbcData;
    public static com.sea.icoco.Entity.esunData esunData;
    public static com.sea.icoco.Entity.creditCardData creditCardData;
    public static com.sea.icoco.Entity.aaMessageData aaMessageData;
    public static com.sea.icoco.Entity.payData payData;
    public static com.sea.icoco.Entity.budgetData budgetData;
    public static com.sea.icoco.Entity.couponData couponData;
    public static com.sea.icoco.Entity.shopData shopData;
    public static com.sea.icoco.Entity.gpsData gpsData;
    public static com.sea.icoco.Entity.transactionData transactionData;
    public static MyDBHelper myDBHelper;
    public static boolean EmulationMode = true,CTBCMode = false,ESUNMode = false;
    public static String server = "220.134.230.193";
    //    public static String server = "40.83.122.51";
    public static int exchangePoint = 0;
    Integer point = 0;


    public void setPoint(int point){
        this.point = point;
    }

    public Integer getPoint(){
        return  this.point;
    }

    public DataControler(Context context)
    {
        userData = new userData();
        friendsData = new friendsData();
        ctbcData = new ctbcData();
        esunData = new esunData();
        creditCardData = new creditCardData();
        myDBHelper = new MyDBHelper(context);
        aaMessageData = new aaMessageData(); // 必須在 SQLite 後面
        payData = new payData();
        budgetData = new budgetData();
        couponData = new couponData();
        shopData = new shopData();
        gpsData = new gpsData();
        transactionData = new transactionData();
        gpsData.loadGpsData(context);
    }

    public JSONArray CursorToJsonArray(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

//        cursor.close();
        return resultSet;
    }
}
