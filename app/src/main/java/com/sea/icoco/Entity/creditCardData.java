package com.sea.icoco.Entity;

import com.sea.icoco.Control.BankAPI.CTBC.CTBC_CreditCardLimit;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/6.
 */
public class creditCardData
{
    private DataControler dataControler = MainActivity.dataControler;
    private JSONArray creditCardData;
    JSONObject creditCardData_cardNo = new JSONObject();
    boolean EmulationMode = true;
    boolean CTBCMode = false;

    public void loadCreditData()
    {
        String sql = "SELECT * FROM icoco.usercreditcard where uid = "+dataControler.userData.getId();
        new CreditCard_Select().execute(sql);
    }

    public JSONArray getCreditCardData() {return creditCardData;}

    private class CreditCard_Select extends MySQL_Select
    {
        @Override
        protected void onPostExecute(String result)
        {
            if (!result.equals("[]"))
            {
                try
                {
                    creditCardData = new JSONArray(result);
                    for (int i = 0 ;i < creditCardData.length();i++)
                    {
                        creditCardData_cardNo.put(creditCardData.getJSONObject(i).getString("cardNo"),creditCardData.getJSONObject(i));
                    }

                    if (CTBCMode)
                    {
                        new CTBC_CreditCardLimit().execute();
                    }
//                    Log.d("Debug Credit Card Data","Credit Card Data Load Success !!");
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public JSONObject getCreditCardData_cardNo() { return creditCardData_cardNo;}
}
