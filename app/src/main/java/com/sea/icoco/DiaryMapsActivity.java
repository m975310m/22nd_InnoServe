package com.sea.icoco;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sea.icoco.Control.DataControler;

import org.json.JSONException;
import org.json.JSONObject;

public class DiaryMapsActivity extends AppCompatActivity implements OnMapReadyCallback ,GoogleMap.OnInfoWindowClickListener{

    public static GoogleMap mMap;
    DataControler dataControler = MainActivity.dataControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MainActivity.initDiaryMapActivity = true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        dataControler.gpsData.moveCamera();
        mMap.setOnInfoWindowClickListener(this);
        if (dataControler.shopData.onLoadSuccess())
        {
            for (int i = 0 ; i < dataControler.shopData.getShopData().length() ; i++)
            {
                try {
                    JSONObject shop  = dataControler.shopData.getShopData().getJSONObject(i);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(shop.getString("latitude")), Double.parseDouble(shop.getString("longitude"))))
                            .title(shop.getString("shop_name")+" 的日記")
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("map_diary2",100,100))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LatLng position = marker.getPosition();
        for (int i = 0 ; i < dataControler.shopData.getShopData().length();i++)
        {
            try {
                JSONObject shop = dataControler.shopData.getShopData().getJSONObject(i);
                if (Double.parseDouble(shop.getString("latitude")) == position.latitude && Double.parseDouble(shop.getString("longitude")) == position.longitude)
                {
                    startActivity(new Intent(DiaryMapsActivity.this,ShopSummary.class).putExtra("shopUid",shop.getString("uid")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
