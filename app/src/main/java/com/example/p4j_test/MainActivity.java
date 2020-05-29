package com.example.p4j_test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Date now = new Date();
    String[] threeLineElement = new String[3];

    TextView textLat;
    TextView textLon;
    TextView textSatName;
    TextView textAzi;
    TextView textAOS;
    TextView textEOS;
    TextView textTimeMaxHeight;
    TextView textTimeTime;
    TextView textFrequency;
    TextView textMaxElevation;

    final boolean windBack = false;

    private static final SimpleDateFormat TIME_FORMAT;
    static {
        TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLat = findViewById(R.id.tv_lat);
        textLon = findViewById(R.id.tv_long);
        textAzi = findViewById(R.id.tv_azi);
        textSatName = findViewById(R.id.tv_SatName);
        textAOS = findViewById(R.id.tv_aos);
        textEOS = findViewById(R.id.tv_eos);
        textTimeMaxHeight = findViewById(R.id.tv_TimeMaxHeight);
        textTimeTime = findViewById(R.id.tv_TimeTime);
        textFrequency = findViewById(R.id.tv_Frequency);
        textMaxElevation = findViewById(R.id.tv_MaxElevation);

        threeLineElement[0] = "ISS (ZARYA)";
        threeLineElement[1]="1 25544U 98067A   20149.30823137  .00001252  00000-0  30477-4 0  9998";
        threeLineElement[2]="2 25544  51.6439  86.8571 0002404   1.8404 134.1840 15.49398783228934";

        final TLE tle = new TLE(threeLineElement);
        final Satellite sat = SatelliteFactory.createSatellite(tle);
        final GroundStationPosition GSP = new GroundStationPosition(46.5192,14.5665,180.0);



        Double lat = sat.getPosition(GSP, now).getLatitude()*180.0/Math.PI;
        Double lon = sat.getPosition(GSP, now).getLongitude()*180.0/Math.PI;
        final Double azi = sat.getPosition(GSP, now).getAzimuth();

        if(lon > 180.0f){
            lon = -(360.0f - lon);
        }

        textLat.setText("Latitude: " + lat);
        textLon.setText("Longitude: " + lon);
        textAzi.setText("Azimuth: " + azi + "°");


        PassPredictor passPredictor = null;
        try {
            passPredictor = new PassPredictor(tle, GSP);
        } catch (InvalidTleException e) {
            e.printStackTrace();
        } catch (SatNotFoundException e) {
            e.printStackTrace();
        }

        try {
            final SatPassTime passTime = passPredictor.nextSatPass(now, true);

            textSatName.setText("" + tle.getName());
            textAOS.setText("AOS: " + passTime.getAosAzimuth() + "°");
            textEOS.setText("EOS: " + passTime.getLosAzimuth() + "°");
            textTimeTime.setText("Start/End: " + formatTime(passTime.getStartTime()) + " : " + formatTime(passTime.getEndTime()));
            Log.d("TEST: ", "" + passTime.getStartTime());
            textFrequency.setText("Up/Down: 438.000 MHz");
            textMaxElevation.setText("Max Elevation: " + (int)passTime.getMaxEl() + "°");
            textTimeMaxHeight.setText("Max Elevation @: " + formatTime(passTime.getTCA()));

        } catch (InvalidTleException e) {
            e.printStackTrace();

        } catch (SatNotFoundException e) {
            e.printStackTrace();
        }


    }




    private synchronized static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }
}
