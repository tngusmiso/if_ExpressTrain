package express.if_week.expresstrain_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.android.navermap.*;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MyMap extends NMapActivity {

    ProgressDialog loadingdDialog;
    private NMapView mMapView;// 지도 화면 View
    private NMapController mMapController;
    private final String CLIENT_ID = "N9b68ZJe0OThPeeDBC_b";// 애플리케이션 클라이언트 아이디 값

    private static final String LOG_TAG = "";
    private static final String TAG_JSON = "";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_LAT = "LATITUDE";
    private static final String TAG_LON = "longitude";

    private GetJson getJson = new GetJson();
    // create resource provider
    NMapViewerResourceProvider mMapViewerResourceProvider = null;
    NMapOverlayManager mOverlayManager = null;
    NMapLocationManager mMapLocationManager = null;

    String mJsonString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_map);

        mMapView = new NMapView(this);

        mMapView.setClientId(CLIENT_ID); // 클라이언트 아이디 값 설정
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);


        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();
        mMapController = mMapView.getMapController();
        ViewGroup vg = findViewById(R.id.mymap);
        vg.addView(mMapView);
        final String storeName = getIntent().getStringExtra("storeName");
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);



        final NGeoPoint myLocation = getLocat();
        switch (getIntent().getStringExtra("type")){
            case "showAll":
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int markerId = NMapPOIflagType.PIN;
                        // set POI data
                        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
                        poiData.beginPOIdata(1);


                        poiData.addPOIitem(127.061230, 37.548048, "소셜캠퍼스", markerId, 0);


                        poiData.endPOIdata();

                        // create POI data overlay
                        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

                        // show all POI data
                        poiDataOverlay.showAllPOIdata(0);
                    }
                });
                break;
            case "showOne":
                new Thread() {
                    public void run() {
                        // 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                        getJson.requestWebServer(callback, "selectMap.php", "store=" + storeName);
                    }
                }.start();
                break;

        }

        getLocat();

    }

    private final Callback callback = new Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {
            Log.d(TAG, "콜백오류:" + e.getMessage());
        }

        @Override
        public void onResponse(okhttp3.Call call, Response response) throws IOException {
            String body = response.body().string();
            Log.d(TAG, "서버에서 응답한 Body:" + body);
            mJsonString = body;
            getResult();
        }
    };

    private void getResult() {


        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                int markerId = NMapPOIflagType.PIN;
                // set POI data
                NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
                poiData.beginPOIdata(1);


                poiData.addPOIitem(126.918564, 37.614219, "뚜레쥬르(불광현대)", markerId, 0);


                poiData.endPOIdata();

                // create POI data overlay
                NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

                // show all POI data
                poiDataOverlay.showAllPOIdata(0);

            }

        });
    }

    public NGeoPoint getLocat() {
        NMapLocationManager myLocationManager = new NMapLocationManager(this);
        //myLocationManager.enableMyLocation(false);
        NGeoPoint mylocation = myLocationManager.getMyLocation();


        return mylocation;
    }
    public void setMarker(double longitutde, double latitude, String name) {
        int markerId = NMapPOIflagType.PIN;

        //NMapPOIdataOverlay 객체 생성 - 여러개의 오버레이 아이템들을 하나의 오버레이 객체로 관리하기 위해
        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
        poiData.beginPOIdata(1);
        poiData.addPOIitem(longitutde, latitude, name, markerId, 0);
        poiData.endPOIdata();
        // create POI data overlay
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);


        // 해당 오버레이 객체에 포함된 전체 아이템이 화면에 표시되도록 지도 중심 및 축적 레벨을 변경하려면 아래와 같이 구현합니다.
        // show all POI data
        poiDataOverlay.showAllPOIdata(0);
    }
}