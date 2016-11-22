package com.example.macos.IciTest;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * Created by Microsoft on 11/22/16.
 */

public class VirableDataGenerator {
    private List<LatLng> locations;
    public static void main(String[] args) {
        VirableDataGenerator main = new VirableDataGenerator();
        main.generateVirableData();
    }

    private String generateVirableData(){
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        builder.append("[\n");
        int sec = 1;
        Date date = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        System.out.println(date.getTime());

        try {
            read_location_input();
        }catch (Exception e){

        }

        for(int i = 0; i < locations.size(); i++){
            cal.add(Calendar.SECOND, sec);
            Date changeDate = cal.getTime();
            builder.append("{\n");
            builder.append("\"DateTimeLoging\":\"" + changeDate.getTime() + "\",\n");
            builder.append("\"Id\":\"" + UUID.randomUUID() + "\",\n");
            builder.append("\"Latitude\":\"" + "" + locations.get(i).latitude + "\",\n");
            builder.append("\"Longitude\":\"" + "" + locations.get(i).longitude + "\",\n");
            builder.append("\"RoadId\":4,\n");
            builder.append("\"UserLoging\":\"dungdv\",\n");

            int zValue;
            if (i == 700){
                zValue = generatRandomPositiveNegitiveValue(16000, 8000);
            } if (i == 1500){
                zValue = generatRandomPositiveNegitiveValue(24000, 20000);
            }else if (i == 200){
                zValue = generatRandomPositiveNegitiveValue(18000, 12000);
            }else{
                zValue = generatRandomPositiveNegitiveValue(4000, 1000);
            }
            if(zValue < 1500 && zValue > -1500)
                zValue = 0;
            builder.append("\"ZaxisValue\":" + (double) zValue + "\n");

            if (i == locations.size() - 1)
                builder.append("}\n");
            else
                builder.append("}\n,");
        }

        builder.append("]\n");
        builder.append("]\n");

        System.out.println("data: " + builder.toString());
        try{
            PrintWriter writer = new PrintWriter("virable_generator_data.txt", "UTF-8");
            writer.println(builder.toString());
            writer.close();
            System.out.println("write to file success!");
        } catch (Exception e) {

        }
        return builder.toString();
    }

    //tao vi tri o? ga` - random
    private List<Integer> populateListProblem(){
        List<Integer> listProblem = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Random rand = new Random();
            int ii = rand.nextInt(10 + 1 -1) + 1;
        }
        return listProblem;
    }

    //tao gia tri truc Z - random
    private  int generatRandomPositiveNegitiveValue(int max , int min) {
        Random rand = new Random();
        int ii = rand.nextInt(max + 1 -min) + min;
        return ii;
    }

    public void read_location_input() throws Exception{
        locations = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("virable_example_location_input.txt"));
        String line = br.readLine();
        int i = 0;
        while (line != null) {
            i++;
            String[] temp = line.split(",");
            System.out.println("LatLng : " + i + " - " + line);
            locations.add(new LatLng(Double.parseDouble(temp[1]), Double.parseDouble(temp[0])));
            line = br.readLine();
        }
        br.close();

    }
}
