package fi.digi.savonia.movesense;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import fi.digi.savonia.movesense.Fragments.ConfigurationFragment;
import fi.digi.savonia.movesense.Fragments.ECG_Fragment;
import fi.digi.savonia.movesense.Fragments.Gyro_Fragment;
import fi.digi.savonia.movesense.Fragments.InfoActivity;
import fi.digi.savonia.movesense.Fragments.LinearAcc_Fragment;
import fi.digi.savonia.movesense.Fragments.LiveShowFragment;
import fi.digi.savonia.movesense.Fragments.Magneto_Fragment;
import fi.digi.savonia.movesense.Fragments.ParametersFragment;
import fi.digi.savonia.movesense.Fragments.ScanFragment;
import fi.digi.savonia.movesense.Fragments.Temperature_Fragment;
import fi.digi.savonia.movesense.Models.MeasurementInterval;
import fi.digi.savonia.movesense.Models.Movesense.Data.BatteryVoltageData;
import fi.digi.savonia.movesense.Models.Movesense.Data.ECGData;
import fi.digi.savonia.movesense.Models.Movesense.Data.GyroscopeData;
import fi.digi.savonia.movesense.Models.Movesense.Data.HeartrateData;
import fi.digi.savonia.movesense.Models.Movesense.Data.Imu6Data;
import fi.digi.savonia.movesense.Models.Movesense.Data.Imu6mData;
import fi.digi.savonia.movesense.Models.Movesense.Data.Imu9Data;
import fi.digi.savonia.movesense.Models.Movesense.Data.LinearAccelerationData;
import fi.digi.savonia.movesense.Models.Movesense.Data.MagnetometerData;
import fi.digi.savonia.movesense.Models.Movesense.Data.TemperatureData;
import fi.digi.savonia.movesense.Models.Movesense.Float3DVector;
import fi.digi.savonia.movesense.Models.Movesense.Info.ECGInfo;
import fi.digi.savonia.movesense.Models.Movesense.Info.GyroscopeInfo;
import fi.digi.savonia.movesense.Models.Movesense.Info.LinearAccelerationInfo;
import fi.digi.savonia.movesense.Models.Movesense.Info.MagnetometerInfo;
import fi.digi.savonia.movesense.Tools.BluetoothHelper;
import fi.digi.savonia.movesense.Tools.Listeners.BluetoothActionListener;
import fi.digi.savonia.movesense.Tools.Listeners.MovesenseActionListener;
import fi.digi.savonia.movesense.Tools.Listeners.SamiMeasurementsActionListener;
import fi.digi.savonia.movesense.Tools.MeasurementHelper;
import fi.digi.savonia.movesense.Tools.MovesenseHelper;
import fi.digi.savonia.movesense.Tools.SamiMeasurementHelper;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.movesense.mds.MdsException;
import com.polidea.rxandroidble2.RxBleDevice;


import java.util.ArrayList;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements BluetoothActionListener, MovesenseActionListener, ScanFragment.OnFragmentInteractionListener, ParametersFragment.OnFragmentInteractionListener, ConfigurationFragment.OnFragmentInteractionListener, InfoActivity.OnFragmentInteractionListener, LiveShowFragment.OnFragmentInteractionListener, ECG_Fragment.OnFragmentInteractionListener, Temperature_Fragment.OnFragmentInteractionListener, LinearAcc_Fragment.OnFragmentInteractionListener, Gyro_Fragment.OnFragmentInteractionListener, Magneto_Fragment.OnFragmentInteractionListener, SamiMeasurementsActionListener {

    /*
    private String magnet;
    private TextView ECGtextview;
    private TextView GyroScopeTextview;
    private TextView magnetoTextview;

     */
    private TextView avg;
    private TextView TemperatureTextView;
    //private TextView Heart_rateView;
    private TextView Heart_rIntervalView;
    private TextView battery_view;

    Animation anim;
    ImageView im;
    //LinearAccChart
    LineChart mChart;
    LineData mLineData;

    LineChart gmChart;
    LineData gmLineData;

    LineChart mmChart;
    LineData mmLineData;
    /*
    private TextView xAxisTextView;
    private TextView yAxisTextView;
    private TextView zAxisTextView;

     */

    //ECG-Graph
    private LineGraphSeries<DataPoint> mSeriesECG = new LineGraphSeries<>();
    private int mDataPointsAppended;

    final int MY_PERMISSIONS_REQUEST = 5;
    BluetoothHelper bluetoothHelper;
    MovesenseHelper movesenseHelper;
    SamiMeasurementHelper samiMeasurementHelper;
    MeasurementHelper measurementHelper;
    ProgressDialog progressDialog;
    final int limitDataRate = 350;
    final int REQUEST_ENABLE_BT = 876;
    boolean canScan = false;
    Page currrentPage = Page.info;

    @Override
    public void onSt_ButtonPressed() {
        ShowConfiguration();
    }

    @Override
    public void onEnd_ButtonPressed() {
        movesenseHelper.UnsubscribeAll();
        counter=0;
        try {
            mSeriesECG.resetData(new DataPoint[0]);

        }catch (IllegalArgumentException e){
            Toast.makeText(this, (CharSequence) e, Toast.LENGTH_SHORT).show();
        }

        CreateLoadingDialog(getString(R.string.loading_title),getString(R.string.unsubscribe_message));
        new Handler().postDelayed(() -> runOnUiThread(() -> {
            measurementHelper.Stop();
            progressDialog.dismiss();
            setFragment(new ParametersFragment(),Page.parameter);
        }),5000);
    }

    @Override
    public void setBatteryview(TextView b) {
        this.battery_view=b;
    }

    @Override
    public LineGraphSeries<DataPoint> ECGPointsit(LineGraphSeries<DataPoint> points) {
        this.mSeriesECG=points;
        return points;
    }

    @Override
    public void setTemperature(TextView textView,TextView avg) {
        this.avg=avg;
        this.TemperatureTextView=textView;
    }


    @Override
    public void setHeartData( TextView interval,ImageView image, Animation animation) {
        //this.Heart_rateView=hrate;
        this.Heart_rIntervalView=interval;
        this.anim=animation;
        this.im=image;

    }

    @Override
    public void setAccLine(LineData ln, LineChart lc) {
        this.mLineData=ln;
        this.mChart=lc;
    }

    @Override
    public void setMagLine(LineData ln, LineChart lc) {
        this.mmChart=lc;
        this.mmLineData=ln;
    }

    @Override
    public void setGyroLine(LineData ln, LineChart lc) {
        this.gmLineData=ln;
        this.gmChart=lc;
    }

    public enum Page
    {
        info,
        scan,
        configuration,
        parameter,
        livedata
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        RequestIgnoreBatteryOptimization();
        CheckPermissions();
        startAnimation(1,this);
        //Test();


    }

    public static void startAnimation(final int view, final Activity activity) {
        //
        // final int start = Color.parseColor("#FA07B5");
        //final int start = Color.parseColor("#F444C2");
        //final int mid = Color.parseColor("#88FDB72B");
        //final int mid = Color.parseColor("#F15555");
        //final int end = Color.TRANSPARENT;

        final int start = Color.parseColor("#16A085");
        final int mid = Color.parseColor("#F4D03F");
        //final int mid = Color.parseColor("#F15555");
        final int end = Color.TRANSPARENT;


        final ArgbEvaluator evaluator = new ArgbEvaluator();
        View preloader = activity.findViewById(R.id.gradientPreloaderView);
        preloader.setVisibility(View.VISIBLE);
        final GradientDrawable gradient = (GradientDrawable) preloader.getBackground();

        ValueAnimator animator = TimeAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(9000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(valueAnimator -> {
            float fraction = valueAnimator.getAnimatedFraction();
            int newStrat = (int) evaluator.evaluate(fraction, start, end);
            int newMid = (int) evaluator.evaluate(fraction, mid, start);
            int newEnd = (int) evaluator.evaluate(fraction, end, mid);
            int[] newArray = {newStrat, newMid, newEnd};
            gradient.setColors(newArray);
        });

        animator.start();
    }




    public static void stopAnimation(final int view, final Activity activity){

        ObjectAnimator.ofFloat(activity.findViewById(view), "alpha", 0f).setDuration(125).start();
    }

    protected void setFragment(Fragment fragment, Page page) {
        currrentPage = page;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment,page.name());
        fragmentTransaction.commit();
    }

    private void ConnectToMovesense(RxBleDevice device)
    {
        bluetoothHelper.StopScan();
        movesenseHelper.Connect(device.getMacAddress());
        CreateLoadingDialog(getString(R.string.loading_title),getString(R.string.connecting_message));
    }


    private void CreateLoadingDialog(String title, String message)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    private void CheckPermissions()
    {
        // Here, thisActivity is the current activity
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            //Program();
            //ShowConfiguration();
            ShowStartInfo();

        }
    }

    private void RequestIgnoreBatteryOptimization()
    {
        String packageName = this.getPackageName();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    private void ShowConfiguration() {
        setFragment(new ConfigurationFragment(),Page.configuration);
    }
    private void ShowStartInfo() {
        setFragment(new InfoActivity(),Page.info);
    }

    @Override
    public void onBackPressed() {
        if(GetFragment(Page.info)!=null){
            finishAndRemoveTask();
        }
        else if(GetFragment(Page.scan)!=null)
        {
            setFragment(new ConfigurationFragment(),Page.configuration);
        }
        else if(GetFragment(Page.configuration)!=null)
        {
            setFragment(new InfoActivity(),Page.info);
        }
        else  if(GetFragment(Page.parameter)!=null)
        {
            movesenseHelper.Disconnect();
            setFragment(new ScanFragment(),Page.scan);
        }
        else if (GetFragment(Page.livedata)!=null)
        {
            //setFragment(para,Page.parameter);
            Toast.makeText(this, "Please press End Workout to go back", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST)
        {
            if(grantResults[0] ==  PackageManager.PERMISSION_GRANTED)
            {
                //ShowConfiguration();
                ShowStartInfo();
            }
            else
            {
                Toast.makeText(this,getString(R.string.notification_location_is_required),Toast.LENGTH_LONG).show();
                //TODO something user interaction
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT)
        {
            if(resultCode == RESULT_OK)
            {
                bluetoothHelper.CheckRequirements();
            }
            else
            {
                Toast.makeText(this,getString(R.string.notification_bluetooth_must_be_on),Toast.LENGTH_LONG).show();
                //TODO something user interaction
            }
        }
    }

    private void Program(long intervalMS, String sWritekey, String sObject) {

        bluetoothHelper = new BluetoothHelper(this);
        bluetoothHelper.SetBluetoothActionListener(this);

        bluetoothHelper.CheckRequirements();

        movesenseHelper = new MovesenseHelper(this);
        movesenseHelper.SetMovesenseActionListener(this);

        samiMeasurementHelper = new SamiMeasurementHelper();
        samiMeasurementHelper.SetListener(this);

        measurementHelper = new MeasurementHelper(samiMeasurementHelper);
        measurementHelper.SetSendInterval(intervalMS);
        //measurementHelper.SetMeasurementNote(sNote);
        measurementHelper.SetMeasurementObject(sObject);
        measurementHelper.SetMeasurementWritekey(sWritekey);

        setFragment(new ScanFragment(),Page.scan);
    }

    private Fragment GetFragment(Page page)
    {
        return getSupportFragmentManager().findFragmentByTag(page.name());
    }

    // Bluetooth Action Listener

    @Override
    public void BleDeviceFound(RxBleDevice bleDevice) {
        ScanFragment fragment = (ScanFragment) GetFragment(Page.scan);
        fragment.AddNewBleDevice(bleDevice);
    }

    @Override
    public void ReadyToScan() {
        bluetoothHelper.Scan(30000);
        canScan = true;
    }

    @Override
    public void BluetoothNotEnabled() {

        Intent intentEnableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intentEnableBluetooth,REQUEST_ENABLE_BT);
    }

    @Override
    public void LocationPermissionNotGranted() {
        Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void LocationNotEnabled() {
        Toast.makeText(this, "Location not enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void BluetoothNotAvailable() {
        Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void Error(String explanation) {
        Toast.makeText(this, explanation, Toast.LENGTH_SHORT).show();
    }

    //ScanFragment Interaction Listener

    @Override
    public void onScanButtonPressed() {
        if(canScan)
        {
            bluetoothHelper.StopScan();
            ScanFragment scanFragment = (ScanFragment) GetFragment(Page.scan);
            scanFragment.ClearList();
            bluetoothHelper.Scan(30000);
        }
    }

    @Override
    public void onDeviceSelected(RxBleDevice bleDevice) {

        ConnectToMovesense(bleDevice);

    }

    //Movesense Action Listener

    @Override
    public void ConnectionResult(boolean success) {
        String sConnectResult;
        progressDialog.dismiss();

        if(success)
        {
            sConnectResult = "Connected to the device successfully!";
        }
        else
        {
            sConnectResult = "Failed connecting to the device!";
        }

        Toast.makeText(MainActivity.this, sConnectResult, Toast.LENGTH_SHORT).show();
        setFragment(new ParametersFragment(),Page.parameter);



    }

    private void GetMovesenseSensorInfo() {

        runOnUiThread(() -> {

            ParametersFragment parameters = (ParametersFragment) GetFragment(Page.parameter);

            MeasurementInterval measurementIntervalTemperature = new MeasurementInterval(MovesenseHelper.Sensor.Temperature,new int[]{1,5,10,15,30,1,5,10,15,30,1,2,3,4,6,12,24},-1,"{value} S",5,"{value} M",10,"{value} H");
            MeasurementInterval measurementIntervalBatteryLevel = new MeasurementInterval(MovesenseHelper.Sensor.BatteryVoltage,new int[]{1,5,10,15,30,1,2,3,4,6,12,24},-1,"{value} M", 5,"{value} H");
            MeasurementInterval measurementIntervalHR = new MeasurementInterval(MovesenseHelper.Sensor.HeartRate,new int[]{1,5,10,25,50},-1,"1/{value}");

            parameters.SetMeasurementIntervals(measurementIntervalTemperature, MovesenseHelper.Sensor.Temperature);
            parameters.SetMeasurementIntervals(measurementIntervalBatteryLevel, MovesenseHelper.Sensor.BatteryVoltage);
            parameters.SetMeasurementIntervals(measurementIntervalHR, MovesenseHelper.Sensor.HeartRate);

            //movesenseHelper.GetInfo(MovesenseHelper.Sensor.Temperature);

            //movesenseHelper.GetInfo(MovesenseHelper.Sensor.BatteryVoltage);
            movesenseHelper.GetInfo(MovesenseHelper.Sensor.LinearAcceleration);
            movesenseHelper.GetInfo(MovesenseHelper.Sensor.Gyroscope);
            movesenseHelper.GetInfo(MovesenseHelper.Sensor.Magnetometer);
            movesenseHelper.GetInfo(MovesenseHelper.Sensor.ECG);
            //movesenseHelper.GetInfo(MovesenseHelper.Sensor.HeartRate);

        });

    }

    @Override
    public void OnDisconnect(String reason) {
        Toast.makeText(this,R.string.notification_movesense_disconnect, Toast.LENGTH_SHORT).show();

        if(currrentPage == Page.parameter)
        {
            measurementHelper.Stop();
        }
    }

    @Override
    public void OnError(MdsException mdsException) {

        Toast.makeText(this, R.string.notification_movesense_error, Toast.LENGTH_SHORT).show();

        if(currrentPage == Page.parameter)
        {
            measurementHelper.Stop();
            setFragment(new ScanFragment(),Page.scan);
        }
    }

    @Override
    public void OnError(String reason) {
        Toast.makeText(this, R.string.notification_movesense_error, Toast.LENGTH_SHORT).show();

        if(currrentPage == Page.parameter)
        {
            measurementHelper.Stop();
            setFragment(new ScanFragment(),Page.scan);
        }
    }
    double counter=0;
    ArrayList <Float> al = new ArrayList<>();

    @Override
    public void OnDataReceived(Object Data, MovesenseHelper.Sensor sensor) {
        switch (sensor)
        {
            case Temperature:
                if(Data != null) {
                    try {
                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            Gson tempgson = new Gson();
                            String tempString = tempgson.toJson(Data);
                            TemperatureData td = new Gson().fromJson(tempString, TemperatureData.class);
                            if(TemperatureTextView != null) {
                                TemperatureTextView.setText(String.format(Locale.getDefault(), "%.2f C°", td.ConvertToCelcius()));
                            }
                        });

                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Connection error with TemperatureView: " + e, Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case BatteryVoltage:
                if(Data != null) {
                    try {
                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            Gson tempgson = new Gson();
                            String battString = tempgson.toJson(Data);
                            BatteryVoltageData bd = new Gson().fromJson(battString, BatteryVoltageData.class);
                            if(battery_view != null) {
                                battery_view.setText(getString(R.string.batter)+bd.Percent+"%");
                            }
                        });

                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Connection error with BatteryView: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case LinearAcceleration:
                if(Data != null) {
                        try {
                            runOnUiThread(() -> {
                                // Stuff that updates the UI
                                if(mChart!=null) {
                                    mLineData = mChart.getData();

                                    ILineDataSet xSet = mLineData.getDataSetByIndex(0);
                                    ILineDataSet ySet = mLineData.getDataSetByIndex(1);
                                    ILineDataSet zSet = mLineData.getDataSetByIndex(2);

                                    if (xSet == null) {
                                        xSet = createSet("LinAcc x", getColor(android.R.color.holo_orange_dark));
                                        ySet = createSet("LinAcc y", getColor(android.R.color.holo_green_dark));
                                        zSet = createSet("LinAcc z", getColor(android.R.color.holo_blue_dark));
                                        mLineData.addDataSet(xSet);
                                        mLineData.addDataSet(ySet);
                                        mLineData.addDataSet(zSet);
                                    }
                                }
                                Gson lineargson = new Gson();
                                String linearaccString = lineargson.toJson(Data);
                                LinearAccelerationData linearAccelerationData = new Gson().fromJson(linearaccString, LinearAccelerationData.class);
                                Float3DVector arr = new Float3DVector();
                                for (int i = 0; i < linearAccelerationData.ArrayAcc.length; i++) {
                                    arr = linearAccelerationData.ArrayAcc[i];
                                }
                                //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                                //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                                //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                                if(mLineData!=null) {
                                    mLineData.addEntry(new Entry(linearAccelerationData.Timestamp / 100,(float) arr.x), 0);
                                    mLineData.addEntry(new Entry(linearAccelerationData.Timestamp / 100, arr.y), 1);
                                    mLineData.addEntry(new Entry(linearAccelerationData.Timestamp / 100, arr.z), 2);
                                    mLineData.notifyDataChanged();
                                }
                                if (mChart != null){
                                    // let the chart know it's data has changed
                                    mChart.notifyDataSetChanged();

                                    // limit the number of visible entries
                                    mChart.setVisibleXRangeMaximum(50);

                                    // move to the latest entry
                                    mChart.moveViewToX(linearAccelerationData.Timestamp / 100);
                                }
                            });


                        } catch (IllegalArgumentException e) {
                            Toast.makeText(this, "Connection error with LinearAcc: " + e, Toast.LENGTH_SHORT).show();
                        }
                }

                break;
            case Gyroscope:
                try {
                    runOnUiThread(() -> {
                        // Stuff that updates the UI
                        Gson gson = new Gson();
                        String Gyroscopejson = gson.toJson(Data);
                        //GyroScopeTextview.setText("Gyroscope:  "+Gyroscopejson);
                        GyroscopeData gyroscopeData = new Gson().fromJson(Gyroscopejson,GyroscopeData.class);
                        Float3DVector g_arr = new Float3DVector();
                        float avgx=0;
                        float aveg=0;
                        for (int i = 0; i < gyroscopeData.ArrayGyro.length; i++) {
                            g_arr = gyroscopeData.ArrayGyro[i];
                            avgx +=gyroscopeData.ArrayGyro[i].x;
                            aveg=avgx/gyroscopeData.ArrayGyro.length;
                        }

                        if (aveg >125){
                            counter +=0.1;
                        }
                        if(avg !=null){
                            avg.setText(String.format(Locale.getDefault(), "Count %.1f  ", counter));
                        }
                        if(Data != null) {
                                if(gmChart!=null) {
                                    gmLineData = gmChart.getData();

                                    ILineDataSet xSet = gmLineData.getDataSetByIndex(0);
                                    ILineDataSet ySet = gmLineData.getDataSetByIndex(1);
                                    ILineDataSet zSet = gmLineData.getDataSetByIndex(2);

                                    if (xSet == null) {
                                        xSet = createSet("Gyro x", getColor(android.R.color.holo_orange_dark));
                                        ySet = createSet("Gyro y", getColor(android.R.color.holo_green_dark));
                                        zSet = createSet("Gyro z", getColor(android.R.color.holo_blue_dark));
                                        gmLineData.addDataSet(xSet);
                                        gmLineData.addDataSet(ySet);
                                        gmLineData.addDataSet(zSet);
                                    }
                                }
                                //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                                //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                                //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                                if(gmLineData!=null) {
                                    gmLineData.addEntry(new Entry(gyroscopeData.Timestamp / 100,(float) g_arr.x), 0);
                                    gmLineData.addEntry(new Entry(gyroscopeData.Timestamp / 100, g_arr.y), 1);
                                    gmLineData.addEntry(new Entry(gyroscopeData.Timestamp / 100, g_arr.z), 2);
                                    gmLineData.notifyDataChanged();
                                }
                                if (gmChart != null){
                                    // let the chart know it's data has changed
                                    gmChart.notifyDataSetChanged();

                                    // limit the number of visible entries
                                    gmChart.setVisibleXRangeMaximum(50);

                                    // move to the latest entry
                                    gmChart.moveViewToX(gyroscopeData.Timestamp / 100);
                                }
                        }
                    });

                }catch (IllegalArgumentException e){
                    Toast.makeText(this, "Connection error with Gyroscope: " + e, Toast.LENGTH_SHORT).show();
                }


                break;

            case Magnetometer:
                //Gson mag_gson = new Gson();
                //magnet = mag_gson.toJson(Data);
                //magnetoTextview.setText("Magnetometer:  "+magnet);
                if(Data != null) {
                    try {
                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            if(mmChart!=null) {
                                mmLineData = mmChart.getData();

                                ILineDataSet xSet = mmLineData.getDataSetByIndex(0);
                                ILineDataSet ySet = mmLineData.getDataSetByIndex(1);
                                ILineDataSet zSet = mmLineData.getDataSetByIndex(2);

                                if (xSet == null) {
                                    xSet = createSet("Magn x", getColor(android.R.color.holo_orange_dark));
                                    ySet = createSet("Magn y", getColor(android.R.color.holo_green_dark));
                                    zSet = createSet("Magn z", getColor(android.R.color.holo_blue_dark));
                                    mmLineData.addDataSet(xSet);
                                    mmLineData.addDataSet(ySet);
                                    mmLineData.addDataSet(zSet);
                                }
                            }
                            Gson lineargson = new Gson();
                            String linearaccString = lineargson.toJson(Data);
                            MagnetometerData linearAccelerationData = new Gson().fromJson(linearaccString, MagnetometerData.class);
                            Float3DVector arr = new Float3DVector();
                            for (int i = 0; i < linearAccelerationData.ArrayMagn.length; i++) {
                                arr = linearAccelerationData.ArrayMagn[i];
                            }
                            //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                            //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                            //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                            if(mmLineData!=null) {
                                mmLineData.addEntry(new Entry(linearAccelerationData.Timestamp / 100, (float)arr.x), 0);
                                mmLineData.addEntry(new Entry(linearAccelerationData.Timestamp / 100, arr.y), 1);
                                mmLineData.addEntry(new Entry(linearAccelerationData.Timestamp / 100, arr.z), 2);
                                mmLineData.notifyDataChanged();
                            }
                            if (mmChart != null){
                                // let the chart know it's data has changed
                                mmChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mmChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                mmChart.moveViewToX(linearAccelerationData.Timestamp / 100);
                            }
                        });


                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Connection error with Magneto: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ECG:
                if(Data != null) {
                    try {
                        Gson ecg_gson = new Gson();
                        String ECGjson = ecg_gson.toJson(Data);
                        ECGData ecgData = new Gson().fromJson(ECGjson, ECGData.class);
                        int[] ecgSamples = ecgData.Samples;
                        final int sampleCount = ecgSamples.length;
                        int width = 128 * 3;
                            try {
                                runOnUiThread(() -> {
                                    // Stuff that updates the UI
                                    for (int ecgSample : ecgSamples) {
                                        if (mSeriesECG != null) {
                                            mSeriesECG.appendData(
                                                    new DataPoint(mDataPointsAppended, ecgSample), false,
                                                    width);
                                        }
                                        mDataPointsAppended++;

                                    if (mDataPointsAppended == 500) {
                                        mDataPointsAppended = 0;
                                        assert mSeriesECG != null;
                                        mSeriesECG.resetData(new DataPoint[0]);
                                    }
                                    assert mSeriesECG != null;
                                    if (mSeriesECG.getHighestValueY() > 2400) {
                                        mSeriesECG.setColor(Color.RED);
                                    } else {
                                        mSeriesECG.setColor(Color.GREEN);
                                    }
                                }

                                });

                            } catch (IllegalArgumentException e) {
                                Log.e("Error", "GraphView error ", e);
                            }
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Connection error with ecg Graph: " + e, Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case IMU6:
                if(Data != null) {
                    try {
                        runOnUiThread(() -> {

                            // Stuff that updates the UI
                            if(mChart!=null) {
                                mLineData = mChart.getData();

                                ILineDataSet xSet = mLineData.getDataSetByIndex(0);
                                ILineDataSet ySet = mLineData.getDataSetByIndex(1);
                                ILineDataSet zSet = mLineData.getDataSetByIndex(2);

                                if (xSet == null) {
                                    xSet = createSet("LinAcc x", getColor(android.R.color.holo_orange_dark));
                                    ySet = createSet("LinAcc y", getColor(android.R.color.holo_green_dark));
                                    zSet = createSet("LinAcc z", getColor(android.R.color.holo_blue_dark));
                                    mLineData.addDataSet(xSet);
                                    mLineData.addDataSet(ySet);
                                    mLineData.addDataSet(zSet);
                                }
                            }
                            Gson lineargson = new Gson();
                            String linearaccString = lineargson.toJson(Data);
                            Imu6Data imu6Data = new Gson().fromJson(linearaccString, Imu6Data.class);
                            Float3DVector arrA = new Float3DVector();
                            Float3DVector arrG = new Float3DVector();
                            for (int i = 0; i < imu6Data.ArrayAcc.length; i++) {
                                arrA = imu6Data.ArrayAcc[i];
                            }
                            for (int i = 0; i < imu6Data.ArrayGyro.length; i++) {
                                arrG = imu6Data.ArrayGyro[i];
                            }
                            //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                            //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                            //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                            if(mLineData!=null) {
                                mLineData.addEntry(new Entry(imu6Data.Timestamp / 100, (float) arrA.x), 0);
                                mLineData.addEntry(new Entry(imu6Data.Timestamp / 100, arrA.y), 1);
                                mLineData.addEntry(new Entry(imu6Data.Timestamp / 100, arrA.z), 2);
                                mLineData.notifyDataChanged();
                            }
                            if (mChart != null){
                                // let the chart know it's data has changed
                                mChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                mChart.moveViewToX(imu6Data.Timestamp / 100);
                            }
                            if(gmChart!=null) {
                                gmLineData = gmChart.getData();

                                ILineDataSet gxSet = gmLineData.getDataSetByIndex(0);
                                ILineDataSet gySet = gmLineData.getDataSetByIndex(1);
                                ILineDataSet gzSet = gmLineData.getDataSetByIndex(2);

                                if (gxSet == null) {
                                    gxSet = createSet("Gyro x", getColor(android.R.color.holo_orange_dark));
                                    gySet = createSet("Gyro y", getColor(android.R.color.holo_green_dark));
                                    gzSet = createSet("Gyro z", getColor(android.R.color.holo_blue_dark));
                                    gmLineData.addDataSet(gxSet);
                                    gmLineData.addDataSet(gySet);
                                    gmLineData.addDataSet(gzSet);
                                }
                            }
                            //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                            //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                            //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                            if(gmLineData!=null) {
                                gmLineData.addEntry(new Entry(imu6Data.Timestamp / 100, (float) arrG.x), 0);
                                gmLineData.addEntry(new Entry(imu6Data.Timestamp / 100, arrG.y), 1);
                                gmLineData.addEntry(new Entry(imu6Data.Timestamp / 100, arrG.z), 2);
                                gmLineData.notifyDataChanged();
                            }
                            if (gmChart != null){
                                // let the chart know it's data has changed
                                gmChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                gmChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                gmChart.moveViewToX(imu6Data.Timestamp / 100);
                            }

                        });

                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Connection error with Imu6: " + e, Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case IMU6m:
                if(Data != null) {
                    try {
                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            if(mChart!=null) {
                                mLineData = mChart.getData();

                                ILineDataSet xSet = mLineData.getDataSetByIndex(0);
                                ILineDataSet ySet = mLineData.getDataSetByIndex(1);
                                ILineDataSet zSet = mLineData.getDataSetByIndex(2);

                                if (xSet == null) {
                                    xSet = createSet("LinAcc x", getColor(android.R.color.holo_orange_dark));
                                    ySet = createSet("LinAcc y", getColor(android.R.color.holo_green_dark));
                                    zSet = createSet("LinAcc z", getColor(android.R.color.holo_blue_dark));
                                    mLineData.addDataSet(xSet);
                                    mLineData.addDataSet(ySet);
                                    mLineData.addDataSet(zSet);
                                }
                            }
                            Gson lineargson = new Gson();
                            String linearaccString = lineargson.toJson(Data);
                            Imu6mData imu6mData = new Gson().fromJson(linearaccString, Imu6mData.class);
                            Float3DVector arrA = new Float3DVector();
                            Float3DVector arrM = new Float3DVector();
                            for (int i = 0; i < imu6mData.ArrayAcc.length; i++) {
                                arrA = imu6mData.ArrayAcc[i];
                            }

                            for (int i = 0; i < imu6mData.ArrayMagn.length; i++) {
                                arrM = imu6mData.ArrayMagn[i];
                            }
                            //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                            //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                            //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                            if(mLineData!=null) {
                                mLineData.addEntry(new Entry(imu6mData.Timestamp / 100, (float) arrA.x), 0);
                                mLineData.addEntry(new Entry(imu6mData.Timestamp / 100, arrA.y), 1);
                                mLineData.addEntry(new Entry(imu6mData.Timestamp / 100, arrA.z), 2);
                                mLineData.notifyDataChanged();
                            }
                            if (mChart != null){
                                // let the chart know it's data has changed
                                mChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                mChart.moveViewToX(imu6mData.Timestamp / 100);
                            }

                            if(mmChart!=null) {
                                mmLineData = mmChart.getData();

                                ILineDataSet mxSet = mmLineData.getDataSetByIndex(0);
                                ILineDataSet mySet = mmLineData.getDataSetByIndex(1);
                                ILineDataSet mzSet = mmLineData.getDataSetByIndex(2);

                                if (mxSet == null) {
                                    mxSet = createSet("Magn x", getColor(android.R.color.holo_orange_dark));
                                    mySet = createSet("Magn y", getColor(android.R.color.holo_green_dark));
                                    mzSet = createSet("Magn z", getColor(android.R.color.holo_blue_dark));
                                    mmLineData.addDataSet(mxSet);
                                    mmLineData.addDataSet(mySet);
                                    mmLineData.addDataSet(mzSet);
                                }
                            }
                            if(mmLineData!=null) {
                                mmLineData.addEntry(new Entry(imu6mData.Timestamp / 100, (float) arrM.x), 0);
                                mmLineData.addEntry(new Entry(imu6mData.Timestamp / 100, arrM.y), 1);
                                mmLineData.addEntry(new Entry(imu6mData.Timestamp / 100, arrM.z), 2);
                                mmLineData.notifyDataChanged();
                            }
                            if (mmChart != null){
                                // let the chart know it's data has changed
                                mmChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mmChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                mmChart.moveViewToX(imu6mData.Timestamp / 100);
                            }

                        });

                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Connection error with Imu6m: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case IMU9:
                if(Data != null) {
                    try {
                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            if(mChart!=null) {
                                mLineData = mChart.getData();

                                ILineDataSet xSet = mLineData.getDataSetByIndex(0);
                                ILineDataSet ySet = mLineData.getDataSetByIndex(1);
                                ILineDataSet zSet = mLineData.getDataSetByIndex(2);

                                if (xSet == null) {
                                    xSet = createSet("LinAcc x", getColor(android.R.color.holo_orange_dark));
                                    ySet = createSet("LinAcc y", getColor(android.R.color.holo_green_dark));
                                    zSet = createSet("LinAcc z", getColor(android.R.color.holo_blue_dark));
                                    mLineData.addDataSet(xSet);
                                    mLineData.addDataSet(ySet);
                                    mLineData.addDataSet(zSet);
                                }
                            }
                            Gson lineargson = new Gson();
                            String linearaccString = lineargson.toJson(Data);
                            Imu9Data imu9Data = new Gson().fromJson(linearaccString, Imu9Data.class);
                            Float3DVector arrA = new Float3DVector();
                            Float3DVector arrG = new Float3DVector();
                            Float3DVector arrM = new Float3DVector();
                            for (int i = 0; i < imu9Data.ArrayAcc.length; i++) {
                                arrA = imu9Data.ArrayAcc[i];
                            }
                            for (int i = 0; i < imu9Data.ArrayGyro.length; i++) {
                                arrG = imu9Data.ArrayGyro[i];
                            }

                            for (int i = 0; i < imu9Data.ArrayMagn.length; i++) {
                                arrM = imu9Data.ArrayMagn[i];
                            }
                            //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                            //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                            //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                            if(mLineData!=null) {
                                mLineData.addEntry(new Entry(imu9Data.Timestamp / 100, (float) arrA.x), 0);
                                mLineData.addEntry(new Entry(imu9Data.Timestamp / 100, arrA.y), 1);
                                mLineData.addEntry(new Entry(imu9Data.Timestamp / 100, arrA.z), 2);
                                mLineData.notifyDataChanged();
                            }
                            if (mChart != null){
                                // let the chart know it's data has changed
                                mChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                mChart.moveViewToX(imu9Data.Timestamp / 100);
                            }
                            if(gmChart!=null) {
                                gmLineData = gmChart.getData();

                                ILineDataSet gxSet = gmLineData.getDataSetByIndex(0);
                                ILineDataSet gySet = gmLineData.getDataSetByIndex(1);
                                ILineDataSet gzSet = gmLineData.getDataSetByIndex(2);

                                if (gxSet == null) {
                                    gxSet = createSet("Gyro x", getColor(android.R.color.holo_orange_dark));
                                    gySet = createSet("Gyro y", getColor(android.R.color.holo_green_dark));
                                    gzSet = createSet("Gyro z", getColor(android.R.color.holo_blue_dark));
                                    gmLineData.addDataSet(gxSet);
                                    gmLineData.addDataSet(gySet);
                                    gmLineData.addDataSet(gzSet);
                                }
                            }
                            //xAxisTextView.setText(String.format(Locale.getDefault(),"x: %.6f", arr.x));
                            //yAxisTextView.setText(String.format(Locale.getDefault(), "y: %.6f", arr.y));
                            //zAxisTextView.setText(String.format(Locale.getDefault(), "z: %.6f", arr.z));
                            if(gmLineData!=null) {
                                gmLineData.addEntry(new Entry(imu9Data.Timestamp / 100, (float) arrG.x), 0);
                                gmLineData.addEntry(new Entry(imu9Data.Timestamp / 100, arrG.y), 1);
                                gmLineData.addEntry(new Entry(imu9Data.Timestamp / 100, arrG.z), 2);
                                gmLineData.notifyDataChanged();
                            }
                            if (gmChart != null){
                                // let the chart know it's data has changed
                                gmChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                gmChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                gmChart.moveViewToX(imu9Data.Timestamp / 100);
                            }

                            if(mmChart!=null) {
                                mmLineData = mmChart.getData();

                                ILineDataSet mxSet = mmLineData.getDataSetByIndex(0);
                                ILineDataSet mySet = mmLineData.getDataSetByIndex(1);
                                ILineDataSet mzSet = mmLineData.getDataSetByIndex(2);

                                if (mxSet == null) {
                                    mxSet = createSet("Magn x", getColor(android.R.color.holo_orange_dark));
                                    mySet = createSet("Magn y", getColor(android.R.color.holo_green_dark));
                                    mzSet = createSet("Magn z", getColor(android.R.color.holo_blue_dark));
                                    mmLineData.addDataSet(mxSet);
                                    mmLineData.addDataSet(mySet);
                                    mmLineData.addDataSet(mzSet);
                                }
                            }
                            if(mmLineData!=null) {
                                mmLineData.addEntry(new Entry(imu9Data.Timestamp / 100, (float) arrM.x), 0);
                                mmLineData.addEntry(new Entry(imu9Data.Timestamp / 100, arrM.y), 1);
                                mmLineData.addEntry(new Entry(imu9Data.Timestamp / 100, arrM.z), 2);
                                mmLineData.notifyDataChanged();
                            }
                            if (mmChart != null){
                                // let the chart know it's data has changed
                                mmChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mmChart.setVisibleXRangeMaximum(50);

                                // move to the latest entry
                                mmChart.moveViewToX(imu9Data.Timestamp / 100);
                            }


                        });


                } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Connection error with Imu9: " + e, Toast.LENGTH_SHORT).show();
            }
                }
                break;
            case HeartRate:
                if(Data != null) {
                    try {
                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            Gson heart_gson = new Gson();
                            String heart_rateString = heart_gson.toJson(Data);
                            HeartrateData heartrateData = new Gson().fromJson(heart_rateString, HeartrateData.class);
                            //Heart_rateView.setText(getString(R.string.hrate) + heartrateData.rrData[0]);
                            if(Heart_rIntervalView!=null) {
                                Heart_rIntervalView.setText(String.format(Locale.getDefault(), getString(R.string.bpm) + "%.2f", heartrateData.average));
                            }
                            if(anim!=null&&im!=null){
                                int ans = (int) (60000/heartrateData.average);

                                if(!anim.hasStarted()||anim.hasEnded()) {
                                    anim.setDuration(ans);
                                    im.setAnimation(anim);
                                    im.startAnimation(anim);
                                }
                            }

                        });



                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, "Error with heartbeat view: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

        measurementHelper.AddMeasurement(Data,sensor);

    }

    private LineDataSet createSet(String name,int color) {
        LineDataSet set = new LineDataSet(null, name);
        set.setLineWidth(2.5f);
        set.setColor(color);
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(0f);

        return set;
    }


    @Override
    public void OnInfoReceived(Object Data, MovesenseHelper.Sensor sensor) {
        runOnUiThread(() -> {

            ParametersFragment parameters = (ParametersFragment) GetFragment(Page.parameter);

            switch (sensor)
            {
                case Temperature:
                    break;
                case LinearAcceleration:
                    LinearAccelerationInfo _linearAccelerationInfo = (LinearAccelerationInfo) Data;
                    parameters.SetMeasurementIntervals(new MeasurementInterval(sensor,_linearAccelerationInfo.SampleRates,limitDataRate,"1/{value} S"),sensor);
                    break;
                case Gyroscope:
                    GyroscopeInfo _gyroscopeInfo = (GyroscopeInfo) Data;
                    parameters.SetMeasurementIntervals(new MeasurementInterval(sensor,_gyroscopeInfo.SampleRates,limitDataRate,"1/{value} S"),sensor);
                    break;
                case Magnetometer:
                    MagnetometerInfo _magnetometerInfo = (MagnetometerInfo) Data;
                    parameters.SetMeasurementIntervals(new MeasurementInterval(sensor,_magnetometerInfo.SampleRates,limitDataRate,"1/{value} S"),sensor);
                    break;
                case ECG:
                    ECGInfo _ECGInfo = (ECGInfo) Data;
                    parameters.SetMeasurementIntervals(new MeasurementInterval(sensor,_ECGInfo.AvailableSampleRates,limitDataRate+50,"1/{value} S"),sensor);
                    break;
                case HeartRate:
                    break;
            }
        });




        Log.i("Info_debug received", sensor.name());
    }

    // Parameters Fragment

    @Override
    public void onStartButtonPressed(MeasurementInterval[] measurementIntervals) {
        try {
            movesenseHelper.SubscribeAll(measurementIntervals);
            CreateLoadingDialog("Get Ready","Measurement starting");
            new Handler().postDelayed(() -> runOnUiThread(() -> {
                measurementHelper.Start();
                setFragment(new LiveShowFragment(),Page.livedata);
                progressDialog.dismiss();
            }),4000);
            //setFragment(new LiveDataFragment(),Page.livedata);
        }catch (IllegalArgumentException e){
            Toast.makeText(this, (CharSequence) e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStopButtonPressed() {
        movesenseHelper.UnsubscribeAll();
        CreateLoadingDialog(getString(R.string.loading_title),getString(R.string.unsubscribe_message));
        new Handler().postDelayed(() -> runOnUiThread(() -> {
            measurementHelper.Stop();
            progressDialog.dismiss();
        }),5000);
    }

    @Override
    public void onReady() {

        GetMovesenseSensorInfo();
    }

    // SamiMeasurementListener

    @Override
    public void onError() {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.measurement_occured_error), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onSuccess() {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.measurement_success), Toast.LENGTH_SHORT).show());
        //runOnUiThread(() -> Toast.makeText(getApplicationContext(),measurementHelper.temperatureData.toString() , Toast.LENGTH_SHORT).show());
    }

    // Configuration Fragment

    @Override
    public void onConfigurationConfirm(long intervalMS, String writekey, String object) {
        //Program(intervalMS,writekey,object,note);
        Program(intervalMS,writekey,object);
    }

}
