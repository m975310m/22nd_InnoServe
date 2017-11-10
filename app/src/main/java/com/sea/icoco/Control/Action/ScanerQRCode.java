package com.sea.icoco.Control.Action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

/**
 * Created by AndyChuo on 2016/2/20.
 */

/**
 * 利用 zxing API 掃描QRCode 並進入指定頁面
 */
public class ScanerQRCode
{
    private Context context;
    private int requestCode,resultCode;
    private Intent intent;
    private String scanContent,FormatName;
    Class intoClass;

    public ScanerQRCode(int requestCode, int resultCode, Intent intent, Context context,Class c)
    {
        this.intent=intent;
        this.requestCode=requestCode;
        this.resultCode=resultCode;
        this.context=context;
        this.intoClass = c;
    }

    public void getContextResult()
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult!=null)
        {
            //建立資料型態
            scanContent = scanningResult.getContents();
            FormatName= scanningResult.getFormatName();
            try
            {
                if(scanContent!=null)
                {
                    Intent intent = new Intent(context,intoClass);
                    intent.putExtra("content",scanContent);
                    //進入
                    ((Activity)context).finish();
                    context.startActivity(intent);
                }

            } catch (Exception e)
            {
            }
        }
    }
    public String getFormat()
    {
        return FormatName;
    }

}
