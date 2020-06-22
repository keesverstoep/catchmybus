package com.example.catchthebus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StopsFromCSVParser {
    static ArrayList<Stop> stops;
    static InputStream inputStream;

    public StopsFromCSVParser() {
        stops = new ArrayList<Stop>();
    }

    public void setInputStream(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public static ArrayList<Stop> firstParseStops() throws IOException {
        //InputStream is = getResources().openRawResource(R.raw.newstopsfull2);
        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            line = br.readLine();
            while(true) {
                line = br.readLine();
                if(line == null) {
                    break;
                }
                Stop stop = new Stop();
                String [] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                stop.setStop_id(values[0]);
                stop.setStop_code(values[1]);
                stop.setStop_name(values[2]);
                String [] arr = values[2].split(",");
                stop.setTown(arr[0].substring(1));
                stop.setStop(arr[1].substring(1, arr[1].length()-1));
                stop.setStop_lat(values[3]);
                stop.setStop_lon(values[4]);
                stop.setStop_metadata(values[5]);
                for (int i = 5; i < values.length; i++) {
                    stop.appendStop_metadata(values[i]);
                }
                stops.add(stop);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stops;
    }

}
