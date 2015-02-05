package com.coretronic.bdt.WalkWay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.coretronic.bdt.Adapter.DialogListAdapter;
import com.coretronic.bdt.AppConfig;
import com.coretronic.bdt.AppUtils;
import com.coretronic.bdt.R;
import com.coretronic.bdt.Utility.CustomerOneBtnAlertDialog;
import com.coretronic.bdt.WalkWay.Module.WalkwayInfo;
import com.coretronic.bdt.module.MapClusterItem;
import com.coretronic.bdt.module.MenuPopupWindow;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WalkWayNear extends Fragment {
    private Spinner spinner_distanceType;
    private ArrayAdapter<String> distancyTypeAdapter;
    private List<String> distanceTypeList;
    private LayoutInflater inflater;
    private String TAG = WalkWayNear.class.getSimpleName();
    private AsyncHttpClient client;
    private RequestParams params;

    // map
    private GoogleMap mMap;
    private SupportMapFragment mapFrag;
    private LocationManager locationManager;
    private List<Marker> markerList;

    // map center
    private int minZoom = 10;
    private Location mapCenter = new Location("mapCenter");
    private Location itemLocation = new Location("itemLocation");
    private Map<Integer, Boolean> hashMap = new HashMap<Integer, Boolean>();

    // Dialog
    private AlertDialog markerInfoDialog = null;
    // Division
    private Marker userMarker = null;
    private List<WalkwayInfo> walkwayInfos = null;


    // Declare a variable for the cluster mansger
    private ClusterManager<MapClusterItem> clusterManager;
    private List<MapClusterItem> mapClusterItems;
    private DialogListAdapter dialogListAdapter;
    private ListView WalkWayList;
    private ClusterRunnable clusterRunnable;

    private String fullyDistance = "全部";

    // sharedpreferences
    private PopupWindow menuPopupWindow;
    private Button btnPopMenu;
    private Button btnBack;
    private TextView barTitle;
    private Context mContext;
    private RelativeLayout navigationBar;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog = null;
    private String uuidChose;
    private AsyncHttpClient asyncHttpClient;
    private Gson gson = new Gson();

    private long startTime = 0;
    private long processTime = 0;

    private final int ERROR_WALKWAY_MESSAGE = 0;
    private final int MESSAGE_WALKWAY_LIST = 1;
    private String[] distanceArr = {"全部", "1-3公里", "3-5公里", "5公里以上"};

    //google map
    private SupportMapFragment fragment;

    //transactionLog
    private String currentTime;
    private String append;
    private CustomerOneBtnAlertDialog customerDialog;

    public JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.i(TAG, "onSuccess");
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Log.d(TAG, "onSuccess = " + response);
            try {
                if (response.get("msgCode").toString().contains("A")) {
                    walkwayInfos.clear();
                    WalkWayLocation walkWayLocation = gson.fromJson(response.toString(), WalkWayLocation.class);
                    walkwayInfos = walkWayLocation.getResult();
                    setDistanceType(fullyDistance);
                } else {
                    customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
                    customerDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e) {
                customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_result_error));
                customerDialog.show();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d(TAG, "onFailure");
            customerDialog = AppUtils.getAlertDialog(mContext, getString(R.string.data_load_error));
            customerDialog.show();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.SHAREDPREFERENCES_NAME, 0);
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(AppConfig.TIMEOUT);
        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // location manager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // marker clusterer
        markerList = new ArrayList<Marker>();
        client = new AsyncHttpClient();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.walkway_near, container, false);
        mContext = v.getContext();
        progressDialog = new ProgressDialog(mContext);
        menuPopupWindow = new MenuPopupWindow(mContext, null);
        walkwayInfos = new ArrayList<WalkwayInfo>();

        initView(v);
        // get divition info
        distanceTypeList.addAll(Arrays.asList(distanceArr));
        distancyTypeAdapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        if (menuPopupWindow != null) {
            menuPopupWindow.dismiss();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if(customerDialog != null && customerDialog.isShowing()){
            customerDialog.dismiss();
        }
        if(asyncHttpClient != null) {
            asyncHttpClient.cancelAllRequests(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            Log.d(TAG, "add MapFragment");
            fragment = SupportMapFragment.newInstance();
            if (fragment != null)
                fm.beginTransaction().replace(R.id.map_container, fragment).commit();
            else
                Log.d(TAG, "add fragment == null");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        // Check Google player
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.d(TAG, "show dialog");
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), AppConfig.RQS_GooglePlayServices);
            dialog.show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        initMap();
        if (mMap == null) {
            Log.d(TAG, "mapFrag == null");
            return;
        }
        //  gps listener
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Log.d(TAG, "bestProvider =" + bestProvider);
        locationManager.requestLocationUpdates(bestProvider, 1000, 0, locationListener);

        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            Log.d(TAG, " first location=" + location.getLatitude() + "," + location.getLongitude());
            LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, AppConfig.MAP_DEFAULT_ZOOM));
        }

        getWalkWayLocation("全部");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(View v) {
        // navigation bar
        navigationBar = (RelativeLayout) v.findViewById(R.id.navigationBar_option);
        btnBack = (Button) v.findViewById(R.id.btnBack);
        btnPopMenu = (Button) v.findViewById(R.id.btnPopMenu);
        barTitle = (TextView) v.findViewById(R.id.action_bar_title);

        barTitle.setText(getString(R.string.lb_ww_title));
        btnBack.setOnClickListener(btnListener);
        btnPopMenu.setOnClickListener(btnListener);

        // spinner setting
        distanceTypeList = new ArrayList<String>();
        distancyTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout_division, distanceTypeList);
        spinner_distanceType = (Spinner) v.findViewById(R.id.walkway_location_distance_type_spinner);
        spinner_distanceType.setAdapter(distancyTypeAdapter);
        spinner_distanceType.setOnItemSelectedListener(spinnerListener);

    }

    private void initMap() {
        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFrag = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
            if (mapFrag != null)
                mMap = mapFrag.getMap();
            else {
                Log.d(TAG, "mapFrag == null");
                return;
            }
        }
        if (mMap != null) {
            Log.d(TAG, "mMap is exist");
            mMap.setMyLocationEnabled(true);
            // customer marker
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            // parameter
            mMap.setOnCameraChangeListener(cameraChangeListener);
            // Default GPS
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AppConfig.MAP_DEFAULT_LOCATION, AppConfig.MAP_DEFAULT_ZOOM));
            clusterManager = new ClusterManager<MapClusterItem>(mContext, mMap);
//            mMap.setOnCameraChangeListener(clusterManager);
            mMap.setOnMarkerClickListener(clusterManager);
            mMap.setOnInfoWindowClickListener(clusterManager);
            clusterManager.setOnClusterClickListener(onClusterClickListener);
            clusterManager.setOnClusterItemClickListener(onClusterItemClickListener);
        } else {
            Log.d(TAG, "mMap is Null");
        }
    }


    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage");
            switch (msg.what) {
                case MESSAGE_WALKWAY_LIST:
                    clusterManager.cluster();
                    break;
                case ERROR_WALKWAY_MESSAGE:
                    Log.d(TAG, "ERROR_WALKWAY_MESSAGE");
                    break;
            }
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    };

    public class ClusterRunnable implements Runnable {

        private String fullyDistance;

        public ClusterRunnable(String fullyDistance) {
            this.fullyDistance = fullyDistance;
        }

        @Override
        public void run() {
            Log.d(TAG, "ClusterRunnable run");

            if (walkwayInfos == null) {
                return;
            }
            WalkwayInfo walkwayInfo;
            Log.d(TAG, "ClusterRunnable walkwayInfos size:" + walkwayInfos.size());

            for (int i = 0; i < walkwayInfos.size(); i++) {
                walkwayInfo = walkwayInfos.get(i);
                Double lan = Double.valueOf(walkwayInfo.getLocation().getLat());
                Double lng = Double.valueOf(walkwayInfo.getLocation().getLng());
                LatLng latLng = new LatLng(lan, lng);
                itemLocation.setLatitude(lan);
                itemLocation.setLongitude(lng);
                Log.d(TAG, "lan:" + lan + "/lng:" + lng);
                // distance : meter
//                    if (mapCenter.distanceTo(itemLocation) < AppConfig.MAP_DEFAULT_SHOW_DISTANCE) {
                // cluster marker
                MapClusterItem item = new MapClusterItem(latLng, walkwayInfo.getWalkwayName(), i);
                if (hashMap.get(i) == null) {
                    clusterManager.addItem(item);
                    hashMap.put(i, true);
                }
                item = null;
            }
            Message msg = new Message();
            msg.what = MESSAGE_WALKWAY_LIST;
            handler.sendMessage(msg);

        }
    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Fragment fragment;
            switch (view.getId()) {
                case R.id.btnBack:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayNear" + "," +
                            "WalkWayMainActivity" + "," +
                            "btnBack" + "\n";
                    transactionLogSave(append);
                    getFragmentManager().popBackStackImmediate();
                    break;
                case R.id.btnPopMenu:
                    callSystemTime();
                    append = currentTime + "," +
                            uuidChose + "," +
                            "WalkWayNear" + "," +
                            "MenuPopupWindow" + "," +
                            "btnPopMenu" + "\n";
                    transactionLogSave(append);
                    Log.d(TAG, "btnPopMenu");
                    menuPopupWindow.showAsDropDown(navigationBar, 0, 0);
                    break;
            }
        }
    };


    // show doctor detail info
    private void showWalkwayDetailInfo(int position) {
        Fragment fragment = new WalkWayListDetailInfo();
        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putString("WalkWayId", walkwayInfos.get(position).getWalkwayId());
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment, "WalkWayDetailInfo")
                    .addToBackStack("WalkWayDetailInfo")
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
        }
    }

    // ClusterManager Listener
    ClusterManager.OnClusterClickListener<MapClusterItem> onClusterClickListener = new ClusterManager.OnClusterClickListener<MapClusterItem>() {
        @Override
        public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
            // Show a toast with some info when the cluster is clicked.
            Log.i(TAG, "onClusterClick -");
            setDialogView();
            Iterator iterator = cluster.getItems().iterator();
            // ListView Dialog
            while (iterator.hasNext()) {
                mapClusterItems.add((MapClusterItem) iterator.next());
            }
            dialogListAdapter.notifyDataSetChanged();

            //Dialog
            markerInfoDialog.show();
            return true;
        }
    };

    ClusterManager.OnClusterItemClickListener<MapClusterItem> onClusterItemClickListener = new ClusterManager.OnClusterItemClickListener<MapClusterItem>() {
        @Override
        public boolean onClusterItemClick(MapClusterItem item) {
            Log.i(TAG, "onClusterItemClick ---");
            // ListView Dialog
            setDialogView();
            mapClusterItems.add(item);
            dialogListAdapter.notifyDataSetChanged();

            //Dialog
            markerInfoDialog.show();
            return true;
        }
    };

    private void setDialogView() {
        WalkWayList = null;
        mapClusterItems = null;
        markerInfoDialog = null;
        mapClusterItems = new ArrayList<MapClusterItem>();
        dialogListAdapter = new DialogListAdapter(mContext, mapClusterItems, inflater);
        WalkWayList = new ListView(mContext);
        WalkWayList.setAdapter(dialogListAdapter);
        WalkWayList.setOnItemClickListener(listItemListener);

        markerInfoDialog = null;
        markerInfoDialog = new AlertDialog.Builder(mContext)
                .setTitle("步道名稱")
                .create();
        markerInfoDialog.setView(WalkWayList);
        markerInfoDialog.setCanceledOnTouchOutside(true);
    }

    // Location Listener
    AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            MapClusterItem item = (MapClusterItem) adapterView.getItemAtPosition(position);
            showWalkwayDetailInfo(Integer.valueOf(item.index));
            if (markerInfoDialog != null) {
                markerInfoDialog.dismiss();
            }
        }
    };

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (userMarker != null) {
                userMarker.remove();
            }
//            userMarker = setMapMark(new LatLng(location.getLatitude(), location.getLongitude()),);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    // Spinner
    Spinner.OnItemSelectedListener spinnerListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            // Get Json from Service
            Log.d(TAG, "select = " + distancyTypeAdapter.getItem(position));
            final String distanceType = distancyTypeAdapter.getItem(position);
            getWalkWayLocation(distanceType);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void getWalkWayLocation(String fullyDistance) {
        progressDialog.setMessage(getString(R.string.dialog_download_msg));
        progressDialog.show();
        final String url = AppConfig.DOMAIN_SITE_PATE + AppConfig.REQUEST_WALKWAYS_LOCATION;
        Log.i(TAG, "url:  " + url);

        uuidChose = sharedPreferences.getString(AppConfig.PREF_UNIQUE_ID, "");
        Log.i(TAG, "UUID:  " + uuidChose);
        RequestParams params = new RequestParams();
        params.add("uid", uuidChose);
        params.add("time", AppUtils.getChineseSystemTime());
        params.add("fullyDistance", fullyDistance);

        Log.i(TAG, "params:  " + params);
        asyncHttpClient.post(url, params, jsonHandler);
    }


    private void setDistanceType(final String fullyDistance) {
        if (fullyDistance == null) {
            return;
        }
        this.fullyDistance = fullyDistance;
        progressDialog.setMessage(getString(R.string.dialog_read_msg));
        progressDialog.show();
        //clear marker hashmap
        clusterManager.clearItems();
        hashMap.clear();
        mMap.clear();
        setWalkwayOnMap(fullyDistance);
    }

    private void setWalkwayOnMap(String fullyDistance) {
        Log.d(TAG, "setWalkwayOnMap");

        if (walkwayInfos != null) {
            clusterRunnable = new ClusterRunnable(fullyDistance);
            new Thread(clusterRunnable).start();
        }
    }

    //Google hashMap
    GoogleMap.OnCameraChangeListener cameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            int currentZoomSize = (int) cameraPosition.zoom;
            if (currentZoomSize < minZoom) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
                return;
            }
            mapCenter.setLongitude(cameraPosition.target.longitude);
            mapCenter.setLatitude(cameraPosition.target.latitude);
            new Thread(clusterRunnable).start();
        }
    };


    public class WalkWayLocation {
        private String msgCode;
        private String status;
        private List<WalkwayInfo> result;


        public String getMsgCode() {
            return msgCode;
        }

        public String getStatus() {
            return status;
        }

        public List<WalkwayInfo> getResult() {
            return result;
        }
    }


//    public static List<WalkwayInfo> getWalkwayInfo(Context context, int fileId) {
//        List<WalkwayInfo> walkwayInfos = new ArrayList<WalkwayInfo>();
//        String line = null;
//        try {
//            InputStream is = context.getResources().openRawResource(fileId);
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            // first line
//            line = bufferedReader.readLine();
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] info = line.split(",");
//                Log.d(TAG, "line:" + line);
//                if (info.length > 12) {
//                    WalkwayInfo walkwayInfo = new WalkwayInfo();
//                    walkwayInfo.setId(info[0]);
//                    walkwayInfo.setPhoto(info[1]);
//                    walkwayInfo.setArea(info[2]);
//                    walkwayInfo.setTitle(info[3]);
//                    walkwayInfo.setSubtitle(info[4]);
//                    walkwayInfo.setAddress(info[5]);
//                    walkwayInfo.setLocation_lat(info[6]);
//                    walkwayInfo.setLocation_lng(info[7]);
//                    walkwayInfo.setCarpark(info[8]);
//                    walkwayInfo.setFeature(info[9]);
//                    walkwayInfo.setDistanceType(info[10]);
//                    walkwayInfo.setDistance(info[11]);
//                    walkwayInfo.setDescription(info[12]);
//                    walkwayInfos.add(walkwayInfo);
//
//                } else {
//                    Log.e(TAG, "資料不足:" + line);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return walkwayInfos;
//    }
public void transactionLogSave(String append) {
    try {
        FileOutputStream outStream = new FileOutputStream("/sdcard/outputLog.txt", true);
        outStream.write(append.getBytes());
        outStream.close();
    } catch (FileNotFoundException e) {
        return;
    } catch (IOException e) {
        return;
    }
}

    //抓取系統時間
    private void callSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
        currentTime = formatter.format(curDate);
        Log.i(TAG, "==Time==: " + currentTime);

    }
}