package com.sea.icoco.Entity;

import android.util.Log;

import com.sea.icoco.Control.BankAPI.CTBC.CTBC_BonusPointInq;
import com.sea.icoco.Control.BankAPI.CTBC.CTBC_SavingAcctInq;
import com.sea.icoco.Control.BankAPI.ESUN.Bank.ESUN_BonusPointInq;
import com.sea.icoco.Control.BankAPI.ESUN.Bank.ESUN_SavingAcctInq;
import com.sea.icoco.Control.BankAPI.Emulation.BonusPointInq;
import com.sea.icoco.Control.BankAPI.Emulation.SavingAcctInq;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/5.
 */
public class esunData
{
    public static String Authorization = "MGVlNWEyMmQtMTdhNi00MWYwLTg2ZDgtMzRmZDM1MDYwYjY3OmNYM21QMHVJM2tBM2RTOHFJN25JNXZBN29WN2JJMHlKMHdHMXlWMWhWOHhGN3FIMGxJ";
    public static String client_id = "0ee5a22d-17a6-41f0-86d8-34fd35060b67";
    JSONObject ctbcData = new JSONObject();
    String ac,Token;
    Double BonusPoint,Balance;
    JSONArray accData;
    boolean loginSuccess = false;
    private DataControler dataControler = MainActivity.dataControler;

    boolean ESUNMode = true;
    boolean EmulationMode = false;

    public void setToken(JSONObject jsonObject) throws JSONException
    {
        if (!jsonObject.getString("access_token").isEmpty())
        {
            ctbcData.put("Token",jsonObject.getString("access_token"));
            Token = jsonObject.getString("access_token");

            if (ESUNMode)
            {
//                new ESUN_BonusPointInq().execute(Token);
                new ESUN_SavingAcctInq().execute(Token);
            }
            else if (EmulationMode)
            {
                new BonusPointInq().execute(Token);
                new SavingAcctInq().execute(Token);
            }

            loginSuccess = true;
//            Log.d("Debug CTBC Data","Get CTBC Token Success");
        }
    }

    public String getToken()
    {
        return Token;
    }
    public Double getBalance() {return Balance;}
    public void setBounsPoint(JSONObject jsonObject) throws JSONException
    {
        if (jsonObject.getString("ResponseCode").equals("0000"))
        {
            this.BonusPoint = Double.parseDouble(jsonObject.getString("BonusPoint"));
            ctbcData.put("BonusPoint",BonusPoint);
            Log.d("Debug CTBC Data","SET BounsPoint Success");
            String sql = "UPDATE `icoco`.`user` SET `BonusPoint`='"+BonusPoint+"' WHERE `uid`='"+dataControler.userData.getId()+"';";
            new MySQL_Execute().execute(sql);
        }
    }

    public void setBalance(final JSONObject jsonObject) throws JSONException
    {
        if (jsonObject.getJSONArray("details").length()>0)
        {
            setAccData(jsonObject.getJSONArray("details"));
            this.Balance = Double.parseDouble(jsonObject.getJSONArray("details").getJSONObject(2).getString("balance"));
            this.ac = jsonObject.getJSONArray("details").getJSONObject(2).getString("account_id");

            ctbcData.put("Balance",Balance);
            Log.d("Debug CTBC Data","SET ESUN Balance Success");
            final String ac = this.ac;

            class SearchAc extends MySQL_Select
            {
                @Override
                protected void onPostExecute(String result)
                {
                    try
                    {
                        if (!result.equals("[]")) //查到資料
                        {
                            JSONObject user_bank_acc = new JSONArray(result).getJSONObject(2);
                            if(Balance != Double.parseDouble(user_bank_acc.getString("balance"))) //如果資料庫餘額不等於CTBC餘額
                            {
                                //更新餘額
                                String sql = "UPDATE `icoco`.`user_bank_acc` SET `balance`='"+Balance.toString()+"' WHERE `uid`='"+dataControler.userData.getId()+"' and`ac`='"+ac+"';";
                                new MySQL_Execute().execute(sql);
                            }
                        } else //查不到此帳號
                        {
                            //新增資料
                            String sql = "INSERT INTO `icoco`.`user_bank_acc` (`uid`, `ac`, `balance`) " +
                                    "VALUES ('"+dataControler.userData.getId()+"', '"+ac+"', '"+Balance.toString()+"');";
                            new MySQL_Execute().execute(sql);
                        }
                    }
                    catch (Exception e) { Log.e("Debug ESUN Data",e.toString()); }
                }
            }

            String sql = "SELECT * FROM icoco.user_bank_acc where uid = '"+dataControler.userData.getId()+"' and ac = '"+ac+"'";
            new SearchAc().execute(sql);
//            String sql = "UPDATE `icoco`.`user` SET `BonusPoint`='"+BonusPoint+"' WHERE `uid`='"+dataControler.userData.getId()+"';";
//            new MySQL_Execute().execute(sql);
        }
    }

    public void setCreditCardLimit(JSONArray creditCardData) throws JSONException
    {
        for ( int i = 0 ; i < creditCardData.length() ; i++)
        {
            JSONObject CTBC_creditCard = creditCardData.getJSONObject(i);
            try
            {
                String CardNO = CTBC_creditCard.getString("CardNO");
                String CreditCardLimit = CTBC_creditCard.getString("CreditCardLimit");
                String AvailableCredit = CTBC_creditCard.getString("AvailableCredit");

                JSONObject credit = dataControler.creditCardData.getCreditCardData_cardNo().getJSONObject(CardNO);
                credit.put("limit",CreditCardLimit);
                credit.put("available",AvailableCredit);

                String sql = "UPDATE `icoco`.`usercreditcard` SET `limit`='"+CreditCardLimit+"', `available`='"+AvailableCredit+"' WHERE `uid`='"+dataControler.userData.getId()+"' and`cardNo`='"+CardNO+"';";
                new MySQL_Execute().execute(sql);
            }
            catch (Exception e)
            {
            }
        }


    }


    public Double getBounsPoint() {return BonusPoint;}
    public boolean isLoginSuccess() {return loginSuccess;}
    public String getCustID() throws JSONException {return dataControler.userData.getCustID();}
    public String getAc() { return ac; }
    private void setAccData(JSONArray jsonArray)
    {
        this.accData = jsonArray;
        dataControler.transactionData.loadTransactionData();
    }
    
    public JSONArray getAccData() { return accData; }
}
