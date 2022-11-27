package fi.digi.savonia.movesense.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import fi.digi.savonia.movesense.R;

public class LinearAcc_Fragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    TextView xAxisTextView;
    TextView yAxisTextView;
    TextView zAxisTextView;
    LineChart mChart;
    LineData mLineData;
    public LinearAcc_Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.linearacc_layout, container, false);
        mChart=view.findViewById(R.id.linearAcc_lineCharti);
        initLinAccChart(mChart);
        mListener.setAccLine(mLineData,mChart);
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
        void setAccLine(LineData ln, LineChart lc);
    }

}
