package fi.digi.savonia.movesense.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.polidea.rxandroidble2.RxBleDevice;

import fi.digi.savonia.movesense.InfoAdapter;
import fi.digi.savonia.movesense.R;
import fi.digi.savonia.movesense.ViewPageAdapter;


public class InfoActivity extends Fragment implements View.OnClickListener {

    // Create object of ViewPager2
    private ViewPager2 viewPager2;
    private Button st_button;
    private TabLayout tabLayout;
    private OnFragmentInteractionListener mListener;
    InfoAdapter viewPager2Adapter;

    public InfoActivity(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_info, container, false);
        //mListener.initContext
        initViewPager(view);



        // To get swipe event of viewpager2
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            // triggered when there is
            // scroll state will be changed
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        return view;
    }
    private void initViewPager(View view){
        // Initializing the viewpager2 object
        // It will find the view by its id which
        // you have provided into XML file
        viewPager2 = view.findViewById(R.id.view_pager2);
        st_button=view.findViewById(R.id.st_button);
        tabLayout=view.findViewById(R.id.tab_layout);
        st_button.setOnClickListener(this);
        // Object of ViewPager2Adapter
        // this will passes the
        // context to the constructor
        // of ViewPager2Adapter


        // adding the adapter to viewPager2
        // to show the views in recyclerview
        viewPager2.setAdapter(viewPager2Adapter);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            viewPager2Adapter = new InfoAdapter(context);
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

    @Override
    public void onClick(View view) {
        mListener.onSt_ButtonPressed();
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
        void onSt_ButtonPressed();
    }

}