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

import org.json.JSONException;

import java.util.List;


/**
 * Created by AndyChuo on 2016/4/5.
 * 建立自訂義清單:
 *      單一項目利用 list_item 介面元件來建立
 */
public class ListAdapter extends BaseAdapter
{

    private AppCompatActivity activity;
    private List<String> mList;
    DataControler dataControler;

    private static LayoutInflater inflater = null;

    public ListAdapter(AppCompatActivity a, List<String> list)
    {
        activity = a;
        mList = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dataControler = MainActivity.dataControler;
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
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.list_name);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.list_image);
        try
        {
            imageView.setImageBitmap((Bitmap) dataControler.friendsData.getFriendsData().getJSONObject(position).get("picture"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        name.setText(mList.get(position).toString());
        return convertView;
    }

}
