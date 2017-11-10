package com.sea.icoco.ActivitySupporter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by AndyChuo on 2016/2/19.
 */

/**
 * Edit by AndyChuo on 2016/2/21.
 */

/**
 * 利用 zxing API 建立 QRCode
 */
public class BuilderQRcode
{
    private String QRCodeContent,content;
//    private String[] contentArr;
    private ImageView target;
    JSONObject contentJObj;
    // QR code 寬度,QR code 高度
    private int QRCodeWidth=1000,QRCodeHeight = 1000;

    public BuilderQRcode(ImageView target, String content) throws JSONException
    {
        this.target = target;
        this.content = content;
    }

    public void setWidth(int Width)
    {
        this.QRCodeWidth=Width;
    }
    public void setHeight(int Height)
    {
        this.QRCodeHeight=Height;
    }
    public Bitmap getBitmap(){
        Bitmap bitmap = null;
        try
        {
            QRCodeContent = content;
            // QR code 內容編碼
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            MultiFormatWriter writer = new MultiFormatWriter();
            // 容錯率姑且可以將它想像成解析度，分為 4 級：L(7%)，M(15%)，Q(25%)，H(30%)
            // 設定 QR code 容錯率為 H
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

            // 建立 QR code 的資料矩陣
            BitMatrix result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
            // ZXing 還可以生成其他形式條碼，如：BarcodeFormat.CODE_39、BarcodeFormat.CODE_93、BarcodeFormat.CODE_128、BarcodeFormat.EAN_8、BarcodeFormat.EAN_13...

            //建立點陣圖
            bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
            // 將 QR code 資料矩陣繪製到點陣圖上
            for (int y = 0; y < QRCodeHeight; y++)
            {
                for (int x = 0; x < QRCodeWidth; x++)
                {
                    bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            // 設定為 QR code 影像
        } catch (WriterException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public void build()
    {
        try
        {
            QRCodeContent = content;
            // QR code 內容編碼
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            MultiFormatWriter writer = new MultiFormatWriter();
            // 容錯率姑且可以將它想像成解析度，分為 4 級：L(7%)，M(15%)，Q(25%)，H(30%)
            // 設定 QR code 容錯率為 H
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            // 建立 QR code 的資料矩陣
            BitMatrix result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
            // ZXing 還可以生成其他形式條碼，如：BarcodeFormat.CODE_39、BarcodeFormat.CODE_93、BarcodeFormat.CODE_128、BarcodeFormat.EAN_8、BarcodeFormat.EAN_13...

            //建立點陣圖
            Bitmap bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
            // 將 QR code 資料矩陣繪製到點陣圖上
            for (int y = 0; y < QRCodeHeight; y++)
            {
                for (int x = 0; x < QRCodeWidth; x++)
                {
                    bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            // 設定為 QR code 影像
            target.setImageBitmap(bitmap);
        } catch (WriterException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
