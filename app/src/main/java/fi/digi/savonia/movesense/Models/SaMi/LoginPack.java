package fi.digi.savonia.movesense.Models.SaMi;

public class LoginPack {
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
