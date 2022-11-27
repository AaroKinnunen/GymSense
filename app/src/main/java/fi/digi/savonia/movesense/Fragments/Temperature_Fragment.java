package fi.digi.savonia.movesense.Fragments;

import android.content.Context;
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

//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;

import fi.digi.savonia.movesense.R;

public class Temperature_Fragment extends Fragment {
    TextView temperatureView;
    //TextView heart_t;
    //TextView heart_rInterval;
    TextView avgx;

    private OnFragmentInteractionListener mListener;

    public Temperature_Fragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.temperature_layout, container, false);
        temperatureView=view.findViewById(R.id.tempview);
        avgx=view.findViewById(R.id.avg);
        //heart_t=view.findViewById(R.id.heart_ratev);
        //heart_rInterval=view.findViewById(R.id.heart_rateintervall);
        try {
            mListener.setTemperature(temperatureView,avgx);
        }catch (IllegalArgumentException e){
            Log.d("TAG", "onCreateView: ",e);
        }
        //mListener.setHeartData(heart_t,heart_rInterval);
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        void setTemperature(TextView textView, TextView avg);
        //void setHeartData(TextView hrate,TextView interval);
    }

}
