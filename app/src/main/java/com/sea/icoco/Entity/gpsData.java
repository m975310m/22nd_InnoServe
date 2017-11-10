package com.sea.icoco.Entity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.sea.icoco.DiaryMapsActivity;
import com.sea.icoco.MainActivity;
import com.sea.icoco.MapsActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by AndyChuo on 2016/5/4.
 */
public class gpsData
{
    Context context;
    LocationManager lmg;    // 定位管理員
    Location myLocation;    // 儲存最近的定位資料
    Geocoder geocoder;        // 用來查詢地址的Geocoder物件
    Date date; //時間
    LatLng place; //經緯度地點
    LocationListener locationListener;
    boolean locationSuccess,locateStatus;
    boolean mapReady = false;

    public void loadGpsData(Context context)
    {
        this.context = context;
        initLmg();
    }


    private void initLmg()
    {
        //定位管理員初始化
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {  // 位置變更事件
                if(location!=null)
                {
                    myLocation = location;
                    locationSuccess = true;
//                    Log.d("debug","testLocation:"+location.getLongitude()+","+location.getLatitude()+"Provider="+location.getProvider());
                    if (isBetterLocation(location, myLocation))
                    {
                        updatePlace(location);
                    }
                    else
                    {
                        updatePlace(location);
                    }
                    Log.d("GPS","gpsDataonLocationChanged");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
//                Log.d("debug","gpsDataStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider)
            { //定位提供者可提供服務
                Log.d("debug","gpsDataonProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider)
            { //定位提供者狀態改變

            }
        };

        lmg = (LocationManager) context.getSystemService(context.LOCATION_SERVICE); // 取得系統服務的LocationManager物件
        try
        {
            lmg.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3, 5, locationListener);  //wifi
            locateStatus = true;
        }
        catch (Exception e) {
            Log.e("Debug GPS Data","Network LocationUpdates Error"+e.toString());
        }

        try
        {
            lmg.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 5, locationListener);  //gps
            locateStatus = true;
        }
        catch (Exception e) {
            Log.e("Debug GPS Data","GPS LocationUpdates Error"+e.toString());
        }

        geocoder = new Geocoder(context, Locale.getDefault());    // 建立 Geocoder 物件
    }

    public Location getMyLocation()
    {
        return myLocation;
    }

    public boolean getLocationLoadSuccess()
    {
        return locationSuccess;
    }

    public String getProvider()
    {
        return myLocation.getProvider();
    }

    public void stopLocate()
    {
        lmg.removeUpdates(locationListener);
        locateStatus = false;
    }

    public void startLocate()
    {
        try
        {
            lmg.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3, 5, locationListener);  //wifi
        }
        catch (Exception e)
        {        }

        try
        {
            lmg.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 5, locationListener);  //gps
        }
        catch (Exception e)
        {        }

        locateStatus = true;
    }

    public boolean getLocateIsON()
    {
        return  locateStatus;
    }

    public LatLng getPlace()
    {
        return  place;
    }

    public String getAddress()
    {
        String strAddr = "";    // 用來建立所要顯示的訊息字串 (地址字串)
        // 用經緯度查地址
        try
        {
            List<Address> listAddr = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);    // 只需傳回1筆地址資料

            if (listAddr == null || listAddr.size() == 0)    //檢查是否有取得地址
                strAddr = "無法取得地址資料!";
            else
            {
                Address addr = listAddr.get(0);    // 取 List 中的第一筆(也是唯一的一筆)
                for (int i = 0; i <= addr.getMaxAddressLineIndex(); i++)
                    strAddr += addr.getAddressLine(i); //+ "\n";
            }

            place = new LatLng(myLocation.getLatitude(), myLocation.getLongitude()); //地址的經緯度


        } catch (Exception ex)
        {
            strAddr += "取得地址發生錯誤:" + ex.toString();
        }
//    txv.setText(strAddr);
        return strAddr.replace("台灣","");
    }






////////////// ↓gps,wifi切換判定

    protected boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 3000;
        boolean isSignificantlyOlder = timeDelta < -3000;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate
                && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null)
        {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
///////////////↑



    private String getDate()
    {  //取得定位時間
        date = new Date(myLocation.getTime());
        DateFormat dateformat = DateFormat.getDateTimeInstance();
        String time = dateformat.format(date);
        return time;
    }

    private void updatePlace(Location location)
    {  //地理位置更新
//        Log.d("debug","updatePlace");
        if (location != null)
        {
//            myLocation = location;
            place = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            moveCamera();
        }
        else
        {
//            txv.setText("無法取得定位資料！");
        }
    }

    public  void moveCamera()
    {
        if (place != null)
        {
            if (MainActivity.initMapActivity)
            {
                MapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                MapsActivity.mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
            if (MainActivity.initDiaryMapActivity)
            {
                DiaryMapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                DiaryMapsActivity.mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
        }
    }

    // 手機定位設定紐
    public void setup(View v)
    {
        // 使用Intent物件啟動"定位"設定程式
        Intent it =	new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(it);
    }

}
