package com.sea.icoco.ActivitySupporter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.MainActivity;
import com.sea.icoco.R;

import org.json.JSONException;


/**
 * Created by AndyChuo on 2016/4/5.
 * 建立自訂義清單:
 *      單一項目利用 list_item 介面元件來建立
 */
public class ListAdapter_SelectPoint extends BaseAdapter
{
    private AppCompatActivity activity;
    DataControler dataControler= MainActivity.dataControler;;
    private static LayoutInflater inflater = null;
    int point = dataControler.exchangePoint;
    public ListAdapter_SelectPoint(AppCompatActivity a)
    {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount()
    {
        return 1;
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
            convertView = inflater.inflate(R.layout.selec_point_item, null);
        }

        final EditText point_edt = (EditText) convertView.findViewById(R.id.point_edt);
        TextView arrow_up_txv = (TextView) convertView.findViewById(R.id.arrow_up_txv);
        TextView arrow_down_txv = (TextView) convertView.findViewById(R.id.arrow_down_txv);
        final TextView point_txv = (TextView) convertView.findViewById(R.id.point_txv);

        arrow_up_txv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                point_edt.setText(String.valueOf(Integer.parseInt(point_edt.getText().toString())+10));
                dataControler.exchangePoint = Integer.parseInt(point_edt.getText().toString());
                point_txv.setText(String.valueOf(dataControler.exchangePoint/10));
            }
        });

        arrow_down_txv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                point_edt.setText(String.valueOf(Integer.parseInt(point_edt.getText().toString())-10));
                dataControler.exchangePoint = Integer.parseInt(point_edt.getText().toString());
                point_txv.setText(String.valueOf(dataControler.exchangePoint/10));
            }
        });
        return convertView;
    }
}
