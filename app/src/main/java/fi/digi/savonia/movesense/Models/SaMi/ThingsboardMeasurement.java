package fi.digi.savonia.movesense.Models.SaMi;

import java.util.List;

public class ThingsboardMeasurement {


    public ThingsboardMeasurement(List<SamiData> data, String timestampISO8601)
    {
        Data = data.toArray(new SamiData[data.size()]);
        TimestampISO8601 = timestampISO8601;
    }

    public SamiData[] Data;
    public String TimestampISO8601;
}
