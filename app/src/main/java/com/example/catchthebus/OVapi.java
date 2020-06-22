package com.example.catchthebus;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OVapi {

    String tpcResponse;
    String lineResponse;
    String journeyResponse;

    public String getLineResponse() {
        return lineResponse;
    }

    public void setLineResponse(String lineResponse) {
        this.lineResponse = lineResponse;
    }

    public String getJourneyResponse() {
        return journeyResponse;
    }

    public void setJourneyResponse(String journeyResponse) {
        this.journeyResponse = journeyResponse;
    }

    public String getTpcResponse() {
        return tpcResponse;
    }

    public void setTpcResponse(String tpcResponse) {
        this.tpcResponse = tpcResponse;
    }



    public OVapi() {
        // TODO Auto-generated constructor stub
    }

    public void doRequestByTimeCodePoint(final String tpc) throws IOException, InterruptedException {
        String url = "https://v0.ovapi.nl/tpc/"+tpc+"/departures";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        final String myResponse = response.body().string();
        tpcResponse = myResponse;

        /*client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                System.out.println("Success");
                System.out.println(myResponse);
                tpcResponse = myResponse;
            }
        });*/
    }

    /*
     * GET /line/$DataOwnerCode_$LinePlanningNumber_$LineDirection
     */

    public void doRequestByLine(String dataOwnerCode, String
            linePlanningNumber, String lineDirection)
            throws IOException, InterruptedException{
        String url = "http://v0.ovapi.nl/line/"+dataOwnerCode+"_"+linePlanningNumber+"_"+lineDirection;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                setLineResponse(myResponse);
            }
        });
    }

    /*
     * GET /journey/$DataOwnerCode_$LocalServiceLevelCode
     * _$LinePlanningNumber_%JourneyNumber_$FortifyOrderNumber
     */

    public void doRequestByJourney(String dataOwnerCode, int localServiceLevelCode,
                                            String linePlanningNumber, int journeyNumber, int fortifyOrderNumber)throws IOException, InterruptedException{
        //System.out.println("and now we are hopefully here");
        String localServiceLevelCodeString = Integer.toString(localServiceLevelCode);
        String journeyNumberString = Integer.toString(journeyNumber);
        String fortifyOrderNumberString = Integer.toString(fortifyOrderNumber);
        String url = "https://v0.ovapi.nl/journey/"+dataOwnerCode+"_"+localServiceLevelCodeString+"_"+linePlanningNumber+"_"+journeyNumberString+"_"+fortifyOrderNumberString;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        //System.out.println("here"+response);
        final String myResponse = response.body().string();
        journeyResponse = myResponse;
    }
}