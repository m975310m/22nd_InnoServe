package com.sea.icoco.ActivitySupporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;
import com.sea.icoco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by AndyChuo on 2016/4/5.
 * 建立自訂義清單:
 *      單一項目利用 list_item 介面元件來建立
 */
public class ListAdapter_Transaction extends BaseAdapter
{
    private AppCompatActivity activity;
    DataControler dataControler= MainActivity.dataControler;;
    JSONArray data = dataControler.transactionData.getTransactionDataOrderDesc();

    private static LayoutInflater inflater = null;

    public ListAdapter_Transaction(AppCompatActivity a)
    {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount()
    {
        return data.length();
    }

    public Object getItem(int position)
    {
        return position;
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.transaction_item, null);
        }

        TextView Narrative_txv = (TextView) convertView.findViewById(R.id.Narrative_txv);
        TextView TrfAcct_txv = (TextView) convertView.findViewById(R.id.TrfAcct_txv);
        TextView Balance_txv = (TextView) convertView.findViewById(R.id.Balance_txv);
        TextView TranDate_txv = (TextView) convertView.findViewById(R.id.TranDate_txv);
        TextView type_txv = (TextView) convertView.findViewById(R.id.type_txv);
        TextView TranAmt_txv = (TextView) convertView.findViewById(R.id.TranAmt_txv);

        try
        {
            //    {
            //        "TranDate": "1452182400000",
            //            "Narrative": " 薪資  ",
            //            "TranAmt": "120.0",
            //            "TrfAcct": "0000000000000000",
            //            "TrfBank": "448",
            //            "Balance": "1000240.0"
            //    }

            JSONObject transaction = data.getJSONObject(position);
            Narrative_txv.setText(transaction.getString("Narrative"));
            TrfAcct_txv.setText(transaction.getString("TrfAcct"));
            Balance_txv.setText(transaction.getString("Balance"));
            TranDate_txv.setText(transaction.getString("TranDate"));
            TranAmt_txv.setText(transaction.getString("TranAmt"));

            String type = (Double.parseDouble(TranAmt_txv.getText().toString()) >= 0.0) ? "收入":"支出";
            type_txv.setText(type);
            TranAmt_txv.setText(transaction.getString("TranAmt").replace("-",""));
        } catch (JSONException e)
        {
            Log.e("Deubg TrransactionError",e.toString());
        }
        return convertView;
    }

}
