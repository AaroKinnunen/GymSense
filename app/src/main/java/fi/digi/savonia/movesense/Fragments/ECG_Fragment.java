package fi.digi.savonia.movesense.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import fi.digi.savonia.movesense.R;

public class ECG_Fragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    GraphView ecg_GraphView;
    TextView heart_rInterval;
    TextView batteryview;
    ImageView image;
    Animation anim;

    public ECG_Fragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ecg_layout, container, false);
        ecg_GraphView=view.findViewById(R.id.ecg_graphview);
        heart_rInterval=view.findViewById(R.id.hr);
        batteryview=view.findViewById(R.id.battery);
        image=view.findViewById(R.id.beatimage);
        mListener.setHeartData(heart_rInterval,image,anim);
        mListener.setBatteryview(batteryview);
        try {
            LineGraphSeries<DataPoint> mSeriesECG = new LineGraphSeries<>();
            ecg_GraphView.addSeries(mListener.ECGPointsit(mSeriesECG));
            initGraph(ecg_GraphView);
            //image.setAnimation(anim);
            //image.startAnimation(anim);

        }catch (IllegalArgumentException e){
            Log.d("TAG", "onCreateView: ",e);
        }

        //InitViews(view);
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            anim = AnimationUtils.loadAnimation(context, R.anim.pulse);
        } else {
            throw new RuntimeException(context
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    private void initGraph(GraphView graphView){
        //ECG-Graph:
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        //graphView.getViewport().setMaxX(128*3);
        ecg_GraphView.getViewport().setMaxX(500);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-3400);
        graphView.getViewport().setMaxY(3400);

        graphView.getViewport().setScrollable(false);
        graphView.getViewport().setScrollableY(false);
        //number of labels on x nd y axis
        //graphView.getGridLabelRenderer().setNumHorizontalLabels(0);
        //graphView.getGridLabelRenderer().setNumVerticalLabels(0);

        //ecg_GraphView.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graphView.getGridLabelRenderer().setHighlightZeroLines(false);
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        graphView.setTitle("ECG-Graph");
        graphView.setTitleColor(Color.WHITE);
        graphView.setTitleTextSize(30);
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
    public interface OnFragmentInteractionListener {
        LineGraphSeries<DataPoint> ECGPointsit(LineGraphSeries<DataPoint> points);
        void setHeartData(TextView interval,ImageView imag,Animation animation);
        void setBatteryview(TextView b);
    }
}
