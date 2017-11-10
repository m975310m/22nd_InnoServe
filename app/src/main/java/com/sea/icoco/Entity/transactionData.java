package com.sea.icoco.Entity;

import android.util.Log;
import android.widget.ListView;

import com.sea.icoco.Control.BankAPI.CTBC.CTBC_SavingAcctDtlInq;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andy on 2016/10/6.
 */

public class transactionData
{
    DataControler dataControler = MainActivity.dataControler;
    JSONArray transactionData = new JSONArray();
    JSONObject transactionData_timestamp = new JSONObject();
    Double AddExpense = 0.0;
//    "SavingAcctDtlInq":
//            [{
//    "TranDate": "1452182400000",
//            "Narrative": " 薪資  ",
//            "TranAmt": "120.0",
//            "TrfAcct": "0000000000000000",
//            "TrfBank": "448",
//            "Balance": "1000120.0"
//},
//    {
//        "TranDate": "1452182400000",
//            "Narrative": " 薪資  ",
//            "TranAmt": "120.0",
//            "TrfAcct": "0000000000000000",
//            "TrfBank": "448",
//            "Balance": "1000240.0"
//    }
//    ]}
    public void loadTransactionData(final JSONObject... data)
    {
        class getTransactionDataFromCTBC extends CTBC_SavingAcctDtlInq
        {
            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONArray transactionDataTemp = new JSONObject(result).getJSONArray("SavingAcctDtlInq");
//                    Log.d("Debug","transactionDataTemp = "+transactionDataTemp.toString());

                    for (int i = 0 ; i < transactionDataTemp.length() ; i++)
                    {
                        try
                        {
                            //如果找的到資料則不必新增資料
                            JSONObject data = transactionData_timestamp.getJSONObject(transactionDataTemp.getJSONObject(i).getString("TranDate"));
                        }catch (Exception e)
                        {
                            JSONObject transaction = transactionDataTemp.getJSONObject(i);
                            //Timestamp 轉 Date
                            Long TranDate = Long.parseLong(transaction.getString("TranDate"));
                            Timestamp timestamp = new Timestamp(TranDate);
                            Date date = new Date(timestamp.getTime());
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                            transaction.put("Timestamp",transaction.getString("TranDate"));
                            transaction.put("TranDate",formatter.format(date));

                            //新增支出
                            if(Double.parseDouble(transaction.getString("TranAmt"))<0.0)
                                AddExpense+=Double.parseDouble(transaction.getString("TranAmt"))*-1;

                            transactionData.put(transaction);
                            transactionData_timestamp.put(transaction.getString("Timestamp"),transaction);

//                            try
//                            {
//                                ((ListView)data[0].get("listView")).setAdapter(new ListAdapter_Transaction((AppCompatActivity) data[0].get("context")));
//                                Log.d("debug","listView 讀取成功");
//                            }catch (Exception e1)
//                            { }
                        }

                    }
//                    Log.d("debug transactionData",transactionData.toString());
//                    Log.d("debug double",String.valueOf(AddExpense));
                    dataControler.budgetData.setTotal_expense(dataControler.budgetData.getTotal_expense()+AddExpense);
                    Log.d("Debug transactionData","From transactionData Add Expense "+String.valueOf(AddExpense));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        new getTransactionDataFromCTBC().execute();
    }

    public JSONArray getTransactionDataOrderDesc()
    {
        JSONArray transactionDataDesc = new JSONArray();
        for (int i = transactionData.length()-1 ; i > -1 ; i--)
        {
            try {
                transactionDataDesc.put(transactionData.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return transactionDataDesc;
    }

    public void setListView(ListView listView)
    {

    }
}
