package fi.digi.savonia.movesense.Tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.IOException;

import fi.digi.savonia.movesense.Models.SaMi.LoginPack;
import fi.digi.savonia.movesense.Models.SaMi.SamiMeasurementPackage;
import fi.digi.savonia.movesense.Models.SaMi.ThinsboardPackage;
import fi.digi.savonia.movesense.Tools.Listeners.HttpActionListener;
import fi.digi.savonia.movesense.Tools.Listeners.SamiMeasurementsActionListener;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SamiMeasurementHelper implements HttpActionListener{

    private SamiMeasurementsActionListener samiMeasurementsActionListener;

    HttpHelper httpHelper;
    Gson gson = new GsonBuilder().create();
    private final String URL = "https://sami.savonia.fi/Service/3.0/MeasurementsService.svc/json/measurements/save";
    private final String URL_thing = "https://thingsboard.cloud/api/v1/integrations/http/e7544336-c913-daf9-3619-1d049abeb773";
    private final String sMediaType = "application/json";
    private final MediaType mediaType;
    private final int requestID=19;
    private final int requestIDTh=25;


    public SamiMeasurementHelper()
    {
        this.mediaType = MediaType.parse(sMediaType);
        httpHelper = HttpHelper.GetInstance();
        httpHelper.SetListener(this);
    }

    public void SetListener(SamiMeasurementsActionListener samiMeasurementsActionListener)
    {
        this.samiMeasurementsActionListener = samiMeasurementsActionListener;
    }
/*
    public void SendMeasurement(SamiMeasurementPackage measurementPackage)
    {
        String data = gson.toJson(measurementPackage);
        //httpHelper.EnqueuePostRequest(URL, RequestBody.create(data,mediaType),requestID);

    }

 */
    public void SendThings(ThinsboardPackage thinsboardPackage){
        String data =gson.toJson(thinsboardPackage);
        //httpHelper.EnqueuePostRequest(URL_thing, RequestBody.create(data,mediaType),requestIDTh);
    }
/*
    public void sendLogin(LoginPack loginPack){
        String data =gson.toJson(loginPack);
        httpHelper.EnqueuePostRequest(URL_thing, RequestBody.create(data,mediaType),requestIDTh);
    }

 */


    @Override
    public void onResult(String result, int id) {
/*
        if(id == requestID)
        {
            boolean success = JsonParser.parseString(result).getAsJsonObject().get("SaveMeasurementsResult").getAsJsonObject().get("Success").getAsBoolean();
            if(success)
            {
                samiMeasurementsActionListener.onSuccess();
            }
            else {
                samiMeasurementsActionListener.onError();
            }

        }


        if(id == requestIDTh)
        {
            boolean success = JsonParser.parseString(result).getAsJsonObject().get("SaveMeasurementsResult").getAsJsonObject().get("Success").getAsBoolean();
            if(success)
            {
                samiMeasurementsActionListener.onSuccess();
            }
            else {
                samiMeasurementsActionListener.onError();
            }

        }*/
    }

    @Override
    public void onError(IOException error, int id) {

        if(id == requestID)
        {
            samiMeasurementsActionListener.onError();
        }

        if(id == requestIDTh)
        {
            samiMeasurementsActionListener.onError();
        }
    }

}
