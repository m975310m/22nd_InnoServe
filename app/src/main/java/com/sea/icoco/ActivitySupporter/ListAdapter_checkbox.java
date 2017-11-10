package com.sea.icoco.ActivitySupporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.FacebookAPI;
import com.sea.icoco.MainActivity;
import com.sea.icoco.R;

import org.json.JSONException;

import java.util.List;


/**
 * Created by AndyChuo on 2016/4/5.
 *  建立自訂義清單:
 *      單一項目利用 list_item_checkbox 介面元件來建立
 */
public class ListAdapter_checkbox extends BaseAdapter
{
    private AppCompatActivity activity;
    private List<String> mList;
    DataControler dataControler;
    FacebookAPI facebookAPI = MainActivity.myFacebookAPI;
    private static LayoutInflater inflater = null;

    public ListAdapter_checkbox(AppCompatActivity a, List<String> list)
    {
        activity = a;
        mList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dataControler= MainActivity.dataControler;
    }

    public int getCount()
    {
        return mList.size();
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
            convertView = inflater.inflate(R.layout.list_item_checkbox, null);
        }

        TextView name_txv = (TextView) convertView.findViewById(R.id.name_txv);
        CheckedTextView chkBshow = (CheckedTextView) convertView.findViewById(R.id.check1);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
        try
        {
            imageView.setImageBitmap((Bitmap) facebookAPI.getFriends().getJSONObject(position).get("picture"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
//        facebookAPI.getFriends().getJSONObject(position).getString("pictureUrl")

        name_txv.setText(mList.get(position).toString());

        return convertView;

    }


}
