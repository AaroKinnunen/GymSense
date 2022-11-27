package fi.digi.savonia.movesense.Models.SaMi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ThinsboardPackage {
    //public String deviceName;
   // public String deviceType;
    public String key;
    public SamiMeasurement[] measurements;


    public void SetKey(String key)
    {
        this.key = key;
    }


/*
    public void SetdeviceName(String devname)
    {
        this.deviceName = devname;
    }
    public void SetdeviceType(String devtype)
    {
        this.deviceType = devtype;
    }

 */


    public void SetMeasurements(SamiMeasurement[] measurements)
    {
        this.measurements = measurements;
    }
}
