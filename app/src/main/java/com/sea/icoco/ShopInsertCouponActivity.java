package com.sea.icoco;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sea.icoco.Control.DataControler;
import com.sea.icoco.Control.MySQL.MySQL_Execute;

public class ShopInsertCouponActivity extends AppCompatActivity
{
    DataControler dataControler = MainActivity.dataControler;
    EditText couponName_edt,day_edt,percent_edt,counponContent_edt,offAmt_edt;
    Button insert_btn;
    RadioGroup radioGroup;
    RadioButton budget_rdo,location_rdo;
    TextView textView18,textView15,textView16,textView17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_insert_coupon);
        findView();
        setChangeMode();
        setOnClickListener();
    }

    private void setChangeMode()
    {
        View.OnClickListener listenr = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (radioGroup.getCheckedRadioButtonId())
                {
                    case R.id.budget_rdo:
                        textView15.setVisibility(View.VISIBLE);
                        textView16.setVisibility(View.VISIBLE);
                        textView17.setVisibility(View.VISIBLE);
                        textView18.setVisibility(View.VISIBLE);
                        day_edt.setVisibility(View.VISIBLE);
                        percent_edt.setVisibility(View.VISIBLE);
                        break;
                    case R.id.location_rdo:
                        textView15.setVisibility(View.GONE);
                        textView16.setVisibility(View.GONE);
                        textView17.setVisibility(View.GONE);
                        textView18.setVisibility(View.GONE);
                        day_edt.setVisibility(View.GONE);
                        percent_edt.setVisibility(View.GONE);
                        break;
                }
            }
        };

        budget_rdo.setOnClickListener(listenr);
        location_rdo.setOnClickListener(listenr);
    }

    private void setOnClickListener()
    {
        insert_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean mode_budget = (radioGroup.getCheckedRadioButtonId() == R.id.budget_rdo);
                String mode = (mode_budget) ? "month":"location";
                String uid = dataControler.userData.getId();
                String couponName = couponName_edt.getText().toString();
                String day = (day_edt.getText().toString().isEmpty()) ? "0":day_edt.getText().toString();
                String percent = (percent_edt.getText().toString().isEmpty()) ? "0":percent_edt.getText().toString();
                String counponContent = counponContent_edt.getText().toString();
                String offAmt = offAmt_edt.getText().toString();

                class insertCoupon extends MySQL_Execute
                {
                    @Override
                    protected void onPostExecute(String result)
                    {
                        if(result.equals("true"))
                        {
                            new AlertDialog.Builder(ShopInsertCouponActivity.this).setTitle("優惠卷上傳資訊").setMessage("優惠卷上傳成功").setNegativeButton("OK", null).show();
                            dataControler.shopData.loadMyShopCoupon();
                        }
                    }
                }

                boolean insertCoupon = false;
                if ( !(couponName.isEmpty() || day.isEmpty() || percent.isEmpty() || counponContent.isEmpty() || offAmt.isEmpty()) )
                    insertCoupon = true;

                if (insertCoupon)
                {
                    String sql = "INSERT INTO `icoco`.`shop_coupon_budget` (`uid`,`coupon_name`, `rule`, `rule_day`, `rule_scale`, `coupon_content`, `off_amt`) VALUES ('" + uid + "','" + couponName + "', '" + mode + "', '" + day + "', '" + percent + "', '" + counponContent + "', '" + offAmt + "');";
//                    Log.d("debug sql ",sql);
                    new insertCoupon().execute(sql);
                }

            }
        });
    }

    private void findView()
    {
        couponName_edt = (EditText) findViewById(R.id.couponName_edt);
        day_edt = (EditText) findViewById(R.id.day_edt);
        percent_edt = (EditText) findViewById(R.id.percent_edt);
        counponContent_edt = (EditText) findViewById(R.id.counponContent_edt);
        offAmt_edt = (EditText) findViewById(R.id.offAmt_edt);
        insert_btn = (Button) findViewById(R.id.insert_btn);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        budget_rdo = (RadioButton) findViewById(R.id.budget_rdo);
        location_rdo = (RadioButton) findViewById(R.id.location_rdo);


        textView15 = (TextView) findViewById(R.id.textView15);
        textView16 = (TextView) findViewById(R.id.textView16);
        textView17 = (TextView) findViewById(R.id.textView17);
        textView18 = (TextView) findViewById(R.id.textView18);
    }
}
