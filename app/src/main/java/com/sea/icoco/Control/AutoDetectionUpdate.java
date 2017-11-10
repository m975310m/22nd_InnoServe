package com.sea.icoco.Control;

import android.os.Handler;

import com.sea.icoco.Control.Action.AutoDetectionCoupon_Budget;
import com.sea.icoco.Control.Action.AutoDetectionCoupon_Location;
import com.sea.icoco.Control.BankAPI.CTBC.CTBC_BonusPointInq;
import com.sea.icoco.Control.BankAPI.CTBC.CTBC_SavingAcctInq;
import com.sea.icoco.Control.BankAPI.Emulation.BonusPointInq;
import com.sea.icoco.Control.MySQL.MySQL_Execute;
import com.sea.icoco.Control.MySQL.MySQL_Select;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/7.
 */
public class AutoDetectionUpdate
{
    DataControler dataControler = MainActivity.dataControler;
    AutoDetectionCoupon_Budget detectionCoupon_budget = new AutoDetectionCoupon_Budget();
    AutoDetectionCoupon_Location autoDetectionCoupon_location = new AutoDetectionCoupon_Location();
    int detectionFlag =0;
    int detectionSecond = 3;
    boolean EmulationMode = false;
    boolean CTBCMode = false;
    boolean ESUNMode = true;

    public AutoDetectionUpdate()
    {
        final Handler handler_detectionUpdate = new Handler();
        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                if(detectionFlag == 0)
                {
                    detectionFlag = 1;
                    if (!detectionCoupon_budget.isLocked())
                    {
                        detectionCoupon_budget.startDetection();
                    }

                    if (!autoDetectionCoupon_location.isLocked())
                    {
                        autoDetectionCoupon_location.startDetection();
                    }


                    String sql = "SELECT * FROM icoco.userupdate where uid = "+dataControler.userData.getId()+";";
                    new Update_Select().execute(sql);
                }
                handler_detectionUpdate.postDelayed(this, detectionSecond*1000);
            }
        };
        handler_detectionUpdate.post(runnable);
    }

    private void updatePointValue()
    {
        if (EmulationMode)
        {
            new BonusPointInq().execute(dataControler.ctbcData.getToken());
        }
        else if (CTBCMode)
        {
            new CTBC_BonusPointInq().execute(dataControler.ctbcData.getToken());
        }


    }

    private void updateAA()
    {
        dataControler.aaMessageData.loadAAMessage();
    }


    private void updateBalance()
    {
        new CTBC_SavingAcctInq().execute();
    }

    private void updatePay()
    {
        dataControler.payData.loadPayData();
    }

    private class Update_Select extends MySQL_Select
    {
        @Override
        protected void onPostExecute(String result)
        {
            try
            {
                if (!result.equals("[]"))
                {
                    boolean freshFlag = false;
                    JSONObject updateData = new JSONArray(result).getJSONObject(0);

                    if (updateData.getString("PointValue").equals("1"))
                    {
                        freshFlag = true;
                        updatePointValue();
                    }

                    if (updateData.getString("aa").equals("1"))
                    {
                        freshFlag = true;
                        updateAA();
                    }

                    if (updateData.getString("balance").equals("1"))
                    {
                        freshFlag = true;
                        updateBalance();
                    }

                    if (updateData.getString("pay").equals("1"))
                    {
                        freshFlag = true;
                        updatePay();
                    }

                    String sql = "UPDATE `icoco`.`userupdate` SET `PointValue`='0',`aa`='0',`balance`='0',`pay`='0' WHERE `uid`='"+dataControler.userData.getId()+"';";
                    if (freshFlag)
                        new Update_Excute().execute(sql);
                    else
                        detectionFlag = 0;
                }
                else
                {
                    class InsertMyData extends MySQL_Execute
                    {
                        @Override
                        protected void onPostExecute(String result)
                        {
                            if(result.equals("true"))
                            {
                                detectionFlag = 0;
                            }
                        }
                    }
                    String sql = "INSERT INTO `icoco`.`userupdate` (`uid`) VALUES ('"+dataControler.userData.getId()+"');";
                    new InsertMyData().execute(sql);
                }

            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }



    private class Update_Excute extends MySQL_Execute
    {
        @Override
        protected void onPostExecute(String result)
        {
            if (result.equals("true")) detectionFlag = 0;
        }
    }
}
