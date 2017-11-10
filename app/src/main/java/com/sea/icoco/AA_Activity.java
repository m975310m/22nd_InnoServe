package com.sea.icoco;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sea.icoco.ActivitySupporter.DownloadImageTask;
import com.sea.icoco.ActivitySupporter.ListAdapter_checkbox;
import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.FacebookAPI;
import com.sea.icoco.Control.MySQL.MySQL_Execute;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AA_Activity extends AppCompatActivity
{
    private DataControler dataControler = MainActivity.dataControler;
//    private FacebookAPI facebookAPI = MainActivity.myFacebookAPI;
    private TextView friends_txv1,friends_txv2,friends_txv3,friends_txv4,friends_txv5,friends_txv6; // 好友下方的文字 ( NT )
    private ImageView friends_img1,friends_img2,friends_img3,friends_img4,friends_img5,friends_img6; // 好友的圖片
    private Integer selectedFriendTotal; // 紀錄現在總共選了幾個好友
    private ArrayList<Boolean> selectingFriends; //紀錄選擇原本的好友
    private ArrayList<Boolean> selectingFriendTemp; //紀錄選擇新的好友
    private JSONObject recordNowPosition = new JSONObject(); //紀錄目前每個位置(6個位置) 目前是被那些好友使用  {fbid:{position:0,pictureUrl:"http:",money:0,name:"xxx",id:{}...}
    private ImageView[] friendsImgArray; // 好友的圖片 (陣列包起來)
    private TextView[] friendsTxvArray; // 好友的下方文字 (陣列包起來)
    private View.OnClickListener addFriendsEvent; // 新增好友的事件
    private AlertDialog dialog; //新增好友的對話窗
    private EditText total_edt,shareNum_edt; // 平分金額 (輸入框) ,平分人數 (輸入框)
    private Integer totalShare = 0; // 平分金額
    private EditText editText1,editText2,editText3,editText4,editText5,editText6; // 每個好友目前分享的金額
    private EditText[] friendsEdtArray; // 每個好友目前分享的金額 (陣列包起來)
    private Button sendAA_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aa);
        findView();
        friendsDialogCreate(); //建立彈出新增好友的對話窗
        setAddFriendsEvent(); // 設定新增好友事件 (+)
        initViewData(); // 初始化 螢幕物件的 設定
        setTotalOnKeyListener(); //設定輸入總金額的觸發事件
        setOnClickListener();
    }

    private void setOnClickListener()
    {
        sendAA_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
//            INSERT INTO `icoco`.`message_aa` (`from`, `to`, `money`) VALUES ('2', '3', '100');
//            INSERT INTO `icoco`.`message_aa` (`from`, `to`, `money`) VALUES ('2', '4', '100');

            public void onClick(View view)
            {
                StringBuilder sql = new StringBuilder();
                try
                {
                    for ( int i = 0 ; i < recordNowPosition.names().length() ; i++)
                    {
                        JSONObject friend = recordNowPosition.getJSONObject(recordNowPosition.names().getString(i));
                        if (friend.getInt("position") > -1)
                        {
                            sql.append("INSERT INTO `icoco`.`message_aa` (`from`, `to`, `money`) VALUES ('"+dataControler.userData.getId()+"', '"+dataControler.friendsData.getFriendsData().getJSONObject(friend.getInt("friendPosition")).getString("uid")+"', '"+friendsEdtArray[friend.getInt("position")].getText().toString()+"');");
                            sql.append("UPDATE `icoco`.`userupdate` SET `aa`='1' WHERE `uid`='"+dataControler.friendsData.getFriendsData().getJSONObject(friend.getInt("friendPosition")).getString("uid")+"';");
                        }
                    }
                    new SendAAMessage().execute(sql.toString());
                }
                catch (JSONException e) { Log.e("Debug AA","Send Error" + e.toString());}
            }
        });
    }

    private void setTotalOnKeyListener()
    {
        total_edt.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent)
            {
                try
                {
                    totalShare = Integer.parseInt(total_edt.getText().toString());
                }
                catch (Exception e)
                {
                    Log.e("debug",e.toString());
                }
                return false;
            }
        });
    }


    private void setAddFriendsEvent()
    {
        addFriendsEvent = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectingFriendTemp = new ArrayList<Boolean>();
                for (int i = 0 ; i < selectingFriends.size();i++) selectingFriendTemp.add(selectingFriends.get(i));
                dialog.show();
            }
        };
    }

    private void initViewData()
    {
        selectedFriendTotal = 0;
        friendsImgArray = new ImageView[6];
        friendsImgArray[0] = friends_img1;
        friendsImgArray[1] = friends_img2;
        friendsImgArray[2] = friends_img3;
        friendsImgArray[3] = friends_img4;
        friendsImgArray[4] = friends_img5;
        friendsImgArray[5] = friends_img6;
        friendsTxvArray = new TextView[6];
        friendsTxvArray[0] = friends_txv1;
        friendsTxvArray[1] = friends_txv2;
        friendsTxvArray[2] = friends_txv3;
        friendsTxvArray[3] = friends_txv4;
        friendsTxvArray[4] = friends_txv5;
        friendsTxvArray[5] = friends_txv6;
        friendsImgArray[0].setOnClickListener(addFriendsEvent);
        friendsEdtArray = new EditText[6];
        friendsEdtArray[0] = editText1;
        friendsEdtArray[1] = editText2;
        friendsEdtArray[2] = editText3;
        friendsEdtArray[3] = editText4;
        friendsEdtArray[4] = editText5;
        friendsEdtArray[5] = editText6;
    }

    private void findView()
    {
        friends_txv1 = (TextView) findViewById(R.id.textView1);
        friends_txv2 = (TextView) findViewById(R.id.textView2);
        friends_txv3 = (TextView) findViewById(R.id.textView3);
        friends_txv4 = (TextView) findViewById(R.id.textView4);
        friends_txv5 = (TextView) findViewById(R.id.textView5);
        friends_txv6 = (TextView) findViewById(R.id.textView6);
        friends_img1 = (ImageView) findViewById(R.id.imageView1);
        friends_img2 = (ImageView) findViewById(R.id.imageView2);
        friends_img3 = (ImageView) findViewById(R.id.imageView3);
        friends_img4 = (ImageView) findViewById(R.id.imageView4);
        friends_img5 = (ImageView) findViewById(R.id.imageView5);
        friends_img6 = (ImageView) findViewById(R.id.imageView6);
        total_edt = (EditText) findViewById(R.id.total_edt);
        shareNum_edt = (EditText) findViewById(R.id.shareNum_edt);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText4);
        editText4 = (EditText) findViewById(R.id.editText5);
        editText5 = (EditText) findViewById(R.id.editText6);
        editText6 = (EditText) findViewById(R.id.editText7);
        sendAA_btn = (Button) findViewById(R.id.sendAA_btn);
    }

    private void friendsDialogCreate()
    {
        final ArrayList<String> friendsData = new ArrayList<String>();
        //JSONArray 轉 ArrayList
        try
        {
            for (int j=0; j < dataControler.friendsData.getFriendsData().length(); j++)
                friendsData.add(dataControler.friendsData.getFriendsData().getJSONObject(j).getString("name"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        ListAdapter_checkbox adapterItem = new ListAdapter_checkbox(AA_Activity.this, friendsData);

        dialog = new AlertDialog.Builder(AA_Activity.this)
                .setTitle("請選擇好友")
                .setAdapter(adapterItem, null)
                .setPositiveButton(getResources().getString(android.R.string.ok), new AlertDialog.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        int diffsub = 0; //紀錄好友總共需要位移幾個位置
                        ArrayList<String> updatePosition = new ArrayList<String>();  // 紀錄需要被更新位置的 fbid
                        ArrayList<String> rmLt = new ArrayList<String>(); // 紀錄需要被刪除位置的 fbid

                        // 比對原先選擇的好友 與 選擇完後的好友差異
                        for (int j = 0 ; j < selectingFriends.size() ; j++)
                        {
                            if (selectingFriends.get(j)!=selectingFriendTemp.get(j)) //如果不一樣
                            {
                                if (selectingFriendTemp.get(j) == true) //如果是更改為新增
                                {
                                    try
                                    {
                                        // ----- 將好友資訊  紀錄至 recordNowPosition (目前位置的好友狀況) -------
                                        JSONObject id = new JSONObject();
                                        int position = selectedFriendTotal;
                                        friendsImgArray[position].setImageDrawable(null); // 初始化圖片
                                        friendsImgArray[position].setOnClickListener(null); // 初始化點擊事件
                                        String pictureUrl = dataControler.friendsData.getFriendsData().getJSONObject(j).getString("pictureUrl");
                                        String name = dataControler.friendsData.getFriendsData().getJSONObject(j).getString("name");
                                        int  friendPosition = j;
                                        id.put("friendPosition",friendPosition);
                                        id.put("position",position);
                                        id.put("pictureUrl",pictureUrl);
                                        id.put("money",0);
                                        id.put("name",name);
                                        recordNowPosition.put(dataControler.friendsData.getFriendsData().getJSONObject(j).getString("id"),id); // {id:{position:0,pictureUrl:"http:",money:0,name:"xxx",id:{}}

                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    selectedFriendTotal++;
                                }
                                else if (selectingFriendTemp.get(j) == false) //如果是更改為刪除
                                {
                                    try
                                    {
                                        // 從 recordNowPosition 尋找出 該好友目前的位置
                                        int position = recordNowPosition.getJSONObject(dataControler.friendsData.getFriendsData().getJSONObject(j).getString("id")).getInt("position");
                                        // 逐一比對 每一個位置的好友 (recordNowPosition )
                                        for (int l = 0 ; l < recordNowPosition.names().length() ; l++)
                                        {
                                            JSONObject friend = recordNowPosition.getJSONObject(recordNowPosition.names().getString(l));
                                            if (friend.getInt("position") > position) // 如果 位置大於刪除好友的位置
                                            {
                                                // 如果更新清單 (updatePosition) 沒有找到此好友
                                                if (!rmLt.contains(recordNowPosition.names().getString(l)) && !updatePosition.contains(recordNowPosition.names().getString(l)))
                                                    updatePosition.add(recordNowPosition.names().getString(l)); //新增好友至更新清單
                                            }
                                            else if (friend.getInt("position") == position)// 如果 位置等於刪除好友的位置
                                            {
                                                //將好友新增至刪除清單 (rmLt)
                                                rmLt.add(recordNowPosition.names().getString(l));
                                                diffsub++; // 位移+1
                                                if (updatePosition.contains(recordNowPosition.names().getString(l))) //如果在更新清單發現要刪除的好友
                                                    updatePosition.remove(recordNowPosition.names().getString(l)); //從更新清單中排除
                                            }
                                        }
                                    } catch (JSONException e)
                                    {
                                        Log.e("Debug",e.toString());
                                        e.printStackTrace();
                                    }
                                    selectedFriendTotal--; // 總共選的好友-1
                                }
                            }
                        }


                        int minPosition = 0;
                        // 逐一更新 在 updatePosition 的好友位置
                        for (int k = 0 ; k < updatePosition.size();k++)
                            try
                            {
                                int newPosition; // 新的位置宣告
                                if (recordNowPosition.getJSONObject(updatePosition.get(k)).getInt("position")-diffsub > -1) //如果新的位置正常
                                {
                                    newPosition = recordNowPosition.getJSONObject(updatePosition.get(k)).getInt("position")-diffsub; // 新的位置 = 原先位置-位移
                                }
                                else //如果新的位置不正常 ( <0 )
                                {
                                    newPosition = 0+minPosition;  // 設定在第一個位置
                                    minPosition ++; //最小位置+1
                                }
                                recordNowPosition.getJSONObject(updatePosition.get(k)).put("position", newPosition);
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        if(updatePosition.size()>0) Log.d("Debug","Update "+updatePosition.toString());

                        selectingFriends.clear(); // 選擇好友 (原本) 清除
                        for ( int j = 0 ; j < selectingFriendTemp.size() ; j++) selectingFriends.add(selectingFriendTemp.get(j)); //選擇好友(原本) 更新以 選擇好友(暫時)資料為主

                        // 刪除在刪除清單好友裡的位置
                        for ( int rmInt = 0 ; rmInt < rmLt.size() ; rmInt++)
                        {
                            try
                            {
                                recordNowPosition.getJSONObject(rmLt.get(rmInt)).put("position",-1); //將原本的好友設為-1
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if (rmLt.size()>0) Log.d("Debug","Delete "+rmLt.toString());

                        ArrayList<String> range = new ArrayList<String>(); // 新增 更新事件、圖片、文字 至 "無" 的位置
                        for (int j = 0 ; j < friendsTxvArray.length ; j++) range.add(String.valueOf(j)); //將1~6新增至更新事件 "無"清單

                        if (recordNowPosition.length()>0)
                            for (int j = 0 ; j < recordNowPosition.names().length() ; j++) //逐一查看  每一個位置的好友 的狀況
                            {
                                try
                                {
                                    JSONObject friend = recordNowPosition.getJSONObject(recordNowPosition.names().getString(j)); //取出好友資料
                                    if (friend.getInt("position") > -1) //如果好友位置不是被刪除的
                                    {
                                        friendsImgArray[friend.getInt("position")].setImageBitmap((Bitmap) dataControler.friendsData.getFriendsData().getJSONObject(friend.getInt("friendPosition")).get("picture"));//設定好友的圖片
                                        friendsTxvArray[friend.getInt("position")].setText("NT "); //設定好友文字 (NT)
                                        int shareForFriend = (totalShare>0)? totalShare/selectedFriendTotal:0; // 如果平分總額 > 0 , 自動計算平分金額,否則平分金額 = 0
                                        friendsEdtArray[friend.getInt("position")].setText(String.valueOf(shareForFriend)); // 將平分金額顯示在 輸入框
                                        range.remove(String.valueOf(friend.getInt("position"))); //更新清單"無" 去除
                                        Log.d("Debug Add",recordNowPosition.names().getString(j));
                                    }
                                } catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        if (diffsub!=0) Log.d("Debug","Move Position "+String.valueOf(diffsub));
                        //在 更新清單"無" 裡的位置 通通初始化
                        for (int j = 0 ; j < range.size() ; j++) friendsTxvArray[Integer.parseInt(range.get(j))].setText("");
                        for (int j = 0 ; j < range.size() ; j++) friendsEdtArray[Integer.parseInt(range.get(j))].setText("");
                        for (int j = 0 ; j < range.size() ; j++) friendsImgArray[Integer.parseInt(range.get(j))].setImageDrawable(null);
                        for (int j = 0 ; j < range.size() ; j++) friendsImgArray[Integer.parseInt(range.get(j))].setOnClickListener(null);
                        friendsImgArray[selectedFriendTotal].setImageDrawable(getResources().getDrawable(R.drawable.add));
                        // 新增好友圖片、事件 放在最後一個位置
                        friendsImgArray[selectedFriendTotal].setOnClickListener(addFriendsEvent);
                        friendsTxvArray[selectedFriendTotal].setText("");
                        friendsEdtArray[selectedFriendTotal].setText("");
                        shareNum_edt.setText(String.valueOf(selectedFriendTotal));
                    }
                })
                .setCancelable(false)
                .create();

        dialog.getListView().setItemsCanFocus(false);
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        selectingFriends = new ArrayList<Boolean>();
        for (int i = 0;i<dataControler.friendsData.getFriendsData().length();i++) selectingFriends.add(false);

        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
//                        參考資料:http://stackoverflow.com/questions/10932832/multiple-choice-alertdialog-with-custom-adapter
                CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.check1);
                chkItem.setChecked(!chkItem.isChecked());
                selectingFriendTemp.set(position,chkItem.isChecked());
            }
        });
    }


    private class SendAAMessage extends MySQL_Execute
    {
        @Override
        protected void onPostExecute(String result)
        {
            if (result.equals("true"))
            {
                new AlertDialog.Builder(AA_Activity.this).setTitle("拆帳訊息").setMessage("拆帳請求成功!\n\n即將跳回主選單").setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        AA_Activity.this.finish();
                    }
                }).setCancelable(false).show();
            }
        }
    }


}

