package com.example.tiagosaldanha.wifiandhotspot;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity_WifiWatcher extends Activity {

    Switch aSwitch;
    WifiManager wifiManager;
    Button btn;
    MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();
    StringBuilder sb = new StringBuilder();

    private static final String ROW_ID = "row_id";
    private List<ScanResult> wifiList;
    private ListView wifiListView;
    private ArrayList<String> wifiArray = new ArrayList<String>();
    private ArrayAdapter wifiArrayAdapter;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Settings.System.canWrite(this))
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);

            Log.i("NetworkUtil","PERMISSION TO WRITE_SETTINGS : " + Settings.System.canWrite(this));

        }

        setContentView(R.layout.activity_main_activity__wifi_watcher);
        wifiListView = (ListView) findViewById(R.id.wifi_list_view);
        wifiListView.setOnItemClickListener(viewWifiListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                }
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

            }
        }

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        aSwitch = (Switch) findViewById(R.id.myswitch);
        btn = (Button) findViewById(R.id.btn);

        //register the switch for event handling
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked && !wifiManager.isWifiEnabled()) {
                    //to switch on WiFi
                    wifiManager.setWifiEnabled(true);
                }
                //to switch off WiFi
                else if (!isChecked && wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                }
            }
        });
//
        btn.setOnClickListener(new View.OnClickListener() {   //melhorar verificação com o swtich do wifi
            @Override
            //On click function
            public void onClick(View view) {
                if (!wifiManager.isWifiEnabled()) {
                    Toast.makeText(getApplicationContext(), "Turn On Wifi", Toast.LENGTH_SHORT).show();
                } else {
                    wifiManager.startScan();
                }
            }
        });

        //register the broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        registerReceiver(myBroadCastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // TODO: What you want to do when it works or maybe .PERMISSION_DENIED if it works better
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainActivity_WifiWatcher Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.tiagosaldanha.wifiandhotspot/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainActivity_WifiWatcher Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.tiagosaldanha.wifiandhotspot/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    private class MyBroadCastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            wifiList = wifiManager.getScanResults();

            sb.append("\n Number of WiFi connections:" + wifiList.size() + "\n\n");
            wifiArray.add(sb.toString());
            sb.setLength(0);
            for (int i = 0; i < wifiList.size(); i++) {
                sb.append(new Integer(i + 1).toString() + ".");
                sb.append("SSID: ");
                sb.append((wifiList.get(i)).SSID.toString());
                sb.append("\n");
                sb.append("BSSID: ");
                sb.append((wifiList.get(i)).BSSID.toString());
                sb.append("\n\n");
                wifiArray.add(sb.toString());
                sb.setLength(0);
            }

            wifiArrayAdapter = new ArrayAdapter(MainActivity_WifiWatcher.this, R.layout.wifi_list_item, R.id.wifi_list_item_text_view, wifiArray);
            wifiListView.setAdapter(wifiArrayAdapter);
        }
    }

    ListView.OnItemClickListener viewWifiListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            Intent viewWiFi = new Intent(MainActivity_WifiWatcher.this,SecondActivity_HotspotCreator.class);

            viewWiFi.putExtra(ROW_ID,l);
            viewWiFi.putExtra("network_ssid",wifiList.get((int) (l-1)).SSID);
            viewWiFi.putExtra("network_bssid",wifiList.get((int) (l-1)).BSSID);
            viewWiFi.putExtra("network_capabilities",wifiList.get((int) (l-1)).capabilities);
            startActivity(viewWiFi);


        }
    };

}