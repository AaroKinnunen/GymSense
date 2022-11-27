package fi.digi.savonia.movesense.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;
import com.movesense.mds.internal.connectivity.MovesenseConnectedDevices;

import java.util.Locale;

import butterknife.BindView;
import fi.digi.savonia.movesense.Models.MeasurementInterval;
import fi.digi.savonia.movesense.R;
import fi.digi.savonia.movesense.Tools.MovesenseHelper;

/*
public class LiveDataFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    GraphView ecg_GraphView;
    TextView heart_t;
    TextView magnetom;
    TextView heart_rInterval;
    TextView temperatureView;
    TextView xAxisTextView;
    TextView yAxisTextView;
    TextView zAxisTextView;
    Button stop_button;
    String Temp;
    float temp_float;
    private int mDataPointsAppended;
    private LineGraphSeries<DataPoint> mSeriesECG;
    LineChart mChart;
    LineData mLineData;



    public LiveDataFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_livedata, container, false);
        InitViews(view);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void InitViews(View view)
    {
        heart_t=view.findViewById(R.id.heart_rate);
        heart_rInterval=view.findViewById(R.id.heart_rateinterval);
        magnetom=view.findViewById(R.id.magneto);
        ecg_GraphView=view.findViewById(R.id.ecg_graph);
        stop_button=view.findViewById(R.id.Stop_button);
        temperatureView=view.findViewById(R.id.temperaturetext);
        xAxisTextView=view.findViewById(R.id.x_axis_textView);
        yAxisTextView=view.findViewById(R.id.y_axis_textView);
        zAxisTextView=view.findViewById(R.id.z_axis_textView);
        xAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        yAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        zAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        mChart=view.findViewById(R.id.linearAcc_lineChart);

        stop_button.setOnClickListener(this);
        mListener.setHeartData(heart_t,heart_rInterval);
        mListener.setmagneto(magnetom);
        mListener.setTemperature(temperatureView);
        initLinAccChart(mChart);
        mListener.setLinearAccData(xAxisTextView,yAxisTextView,zAxisTextView);
        mListener.setAccLine(mLineData,mChart);

        initGraph(ecg_GraphView);

        ecg_GraphView.addSeries(mListener.ECGPoints(mSeriesECG));


    }


    private void initLinAccChart(LineChart ln){
        // Init Empty Chart
        ln.setData(new LineData());
        ln.getDescription().setText("Linear Acc");
        ln.setTouchEnabled(false);
        ln.setAutoScaleMinMaxEnabled(true);
        //ln.getLegend().setEnabled(false);
        ln.getXAxis().setEnabled(false);
        ln.invalidate();
    }
    private void initGraph(GraphView graphView){
        //ECG-Graph:
        mSeriesECG = new LineGraphSeries<DataPoint>();
        //graphView.addSeries(mListener.ECGPoints(mSeriesECG));
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        //graphView.getViewport().setMaxX(128*3);
        ecg_GraphView.getViewport().setMaxX(500);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-15000);
        graphView.getViewport().setMaxY(15000);

        graphView.getViewport().setScrollable(false);
        graphView.getViewport().setScrollableY(false);
        //number of labels on x nd y axis
        graphView.getGridLabelRenderer().setNumHorizontalLabels(7);
        graphView.getGridLabelRenderer().setNumVerticalLabels(7);

        graphView.setTitleColor(Color.WHITE);
        //ecg_GraphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setHighlightZeroLines(false);

        graphView.setTitle("ECG-Graph");
        graphView.setTitleColor(Color.GREEN);
        graphView.setTitleTextSize(25);
    }



    @Override
    public void onClick(View view) {
        mListener.onStopPressedInLiveData();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
/*
    public interface OnFragmentInteractionListener {
        void setAccLine(LineData ln, LineChart lc);
        void setLinearAccData(TextView x,TextView y,TextView z);
        void setHeartData(TextView hrate,TextView interval);
        void setmagneto(TextView textView);
        void setTemperature(TextView textView);
        void onStopPressedInLiveData();
        LineGraphSeries<DataPoint> ECGPoints(LineGraphSeries<DataPoint> points);
    }
}

 */
