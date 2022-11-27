package fi.digi.savonia.movesense;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fi.digi.savonia.movesense.Fragments.ECG_Fragment;
import fi.digi.savonia.movesense.Fragments.Gyro_Fragment;
import fi.digi.savonia.movesense.Fragments.LinearAcc_Fragment;
import fi.digi.savonia.movesense.Fragments.Magneto_Fragment;
import fi.digi.savonia.movesense.Fragments.Temperature_Fragment;

public class ViewPageAdapter extends FragmentStateAdapter {
    Temperature_Fragment tf = new Temperature_Fragment();
    LinearAcc_Fragment lf=new LinearAcc_Fragment();
    ECG_Fragment ecg =new ECG_Fragment();
    Gyro_Fragment gyro = new Gyro_Fragment();
    Magneto_Fragment magn = new Magneto_Fragment();
    private int count;

    public ViewPageAdapter(FragmentActivity fa) {
        super(fa);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if(position==0) {
            try {
                return ecg;
            }catch (IllegalArgumentException e){
                Log.e("TAG", "createFragment: ",e );
            }

        }
        else if(position==1){
            try {
                //ecg.makenull();
                return tf;
            }catch (IllegalArgumentException e){ Log.e("TAG", "createFragment: ",e );}
        }
        else if(position==2) {
            try {
                return lf;
            }catch (IllegalArgumentException e){
                Log.e("TAG", "createFragment: ",e );
            }

        }
        else if(position==3){
            try {
                return gyro;
            }catch (IllegalArgumentException e){ Log.e("TAG", "createFragment: ",e );}
        }
        else {
            try {
                return magn;
            }catch (IllegalArgumentException e){
                Log.e("TAG", "createFragment: ",e );
            }

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
