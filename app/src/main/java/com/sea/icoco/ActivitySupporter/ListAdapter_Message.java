package com.sea.icoco.ActivitySupporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
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

import java.util.List;


/**
 * Created by AndyChuo on 2016/4/5.
 * 建立自訂義清單:
 *      單一項目利用 list_item 介面元件來建立
 */
public class ListAdapter_Message extends BaseAdapter
{
    private AppCompatActivity activity;
    DataControler dataControler= MainActivity.dataControler;;
    JSONArray data = dataControler.aaMessageData.getAaMessageOrderDesc();
    Integer newCoupon;
    private static LayoutInflater inflater = null;

    public ListAdapter_Message(AppCompatActivity a,Integer newCoupon)
    {
        this.newCoupon = newCoupon;
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount()
    {
        return dataControler.aaMessageData.getAAMessage().length();
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
            convertView = inflater.inflate(R.layout.message_item, null);
        }

        ImageView news_img = (ImageView) convertView.findViewById(R.id.news_img);
        if(position < newCoupon)
            news_img.setVisibility(View.VISIBLE);
        else
            news_img.setVisibility(View.GONE);

        TextView name = (TextView) convertView.findViewById(R.id.name_txv);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.list_image);
        TextView money = (TextView) convertView.findViewById(R.id.money_txv);

        try
        {
            JSONObject friendData = dataControler.friendsData.getFriendsDataUID().getJSONObject(data.getJSONObject(position).getString("from"));
            name.setText(friendData.getString("name"));
            imageView.setImageBitmap((Bitmap) friendData.get("picture"));
            money.setText(data.getJSONObject(position).getString("money"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return convertView;
    }

}
