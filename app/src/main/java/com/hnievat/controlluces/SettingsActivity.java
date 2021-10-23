package com.hnievat.controlluces;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.slider.RangeSlider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    Button btn4, btn5;
    String address = null;
    String receivedString;
    TextView lumn, lowerThrVal, higherThrVal;
    ProgressBar wheel;
    RangeSlider slider;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private final boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static float fakeLowerTh=130, fakeHigherTh=440;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        btn4 = findViewById(R.id.button6);
        btn5 = findViewById(R.id.button7);
        lumn = findViewById(R.id.textView2);
        slider = findViewById(R.id.slider1);
        lowerThrVal = findViewById(R.id.lowerThrValue);
        higherThrVal = findViewById(R.id.higherThrValue);

        slider.setValues(fakeLowerTh,fakeHigherTh);
        List<Float> sliderVals = slider.getValues();
        float lowerVal = Collections.min(sliderVals);
        float higherVal = Collections.max(sliderVals);

        lowerThrVal.setText("Encender en: " + (int)lowerVal);
        higherThrVal.setText("Apagar en: " + (int)higherVal);

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //ledControl.sendSignal("C"); //tx thres
                Log.d("hola", "C aun no enviado");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<Float> sliderVals = slider.getValues();
                float lowerVal = Collections.min(sliderVals);
                float higherVal = Collections.max(sliderVals);
                Log.d("hola", "a punto de enviar C y valores");
                ledControl.sendSignal(String.format("C<%d,%d>", (int) lowerVal, (int) higherVal));
                //Log.d("hola", "C y valores enviados, a punto de recibir confirmacion");
                String rxThres = ledControl.receiveSignal();
                Log.d("hola", "señal recibida");
                rxThres = rxThres.replaceAll("[^<\\d,>]+","");
                Log.d("hola", "se ha recibido: " + rxThres);
                lumn.setText(rxThres);
                Pattern patternRxThres = Pattern.compile("(<[0-9]+,[0-9]+>)");
                Matcher matcherRxThres = patternRxThres.matcher(rxThres);
                if (matcherRxThres.find()) {
                    Log.d("hola", "adentro de find");
                    rxThres = rxThres.replace("<", "");
                    rxThres = rxThres.replace(">", "");
                    String[] rxThresAr = rxThres.split(",", 0);
                    if (Integer.parseInt(rxThresAr[0]) == (int) lowerVal && Integer.parseInt(rxThresAr[1]) == (int) higherVal) {
                        msg("Niveles guardados");
                    } else {
                        ledControl.sendSignal("D"); //rx thres
                        rxThres = ledControl.receiveSignal();
                        rxThres = rxThres.replaceAll("[^<\\d,>]+","");
                        lumn.setText(rxThres);
                        matcherRxThres = patternRxThres.matcher(rxThres);
                        if (matcherRxThres.find()) {
                            Log.d("hola", "adentro de find");
                            rxThres = rxThres.replace("<", "");
                            rxThres = rxThres.replace(">", "");
                            rxThresAr = rxThres.split(",", 0);
                            if (Integer.parseInt(rxThresAr[0]) == (int) lowerVal && Integer.parseInt(rxThresAr[1]) == (int) higherVal) {
                                msg("Niveles guardados");
                            } else {
                                msg("Error al guardar niveles");
                            }
                        }
                    }
                }
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                ledControl.sendSignal("D"); //rx thres
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String rxThres = ledControl.receiveSignal();
                Pattern patternRxThres = Pattern.compile("(<[0-9]+,[0-9]+>)");
                Matcher matcherRxThres = patternRxThres.matcher(rxThres);
                while (rxThres.matches("(?!<[0-9]+,[0-9]+>)")) {
                    ledControl.sendSignal("D"); //rx thres
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    rxThres = ledControl.receiveSignal();
                }
                lumn.setText(rxThres);
                rxThres = lumn.getText().toString();
                rxThres = rxThres.replaceAll("[^<\\d,>]+","");
                if (matcherRxThres.find()) {
                    rxThres = rxThres.replace("<", "");
                    rxThres = rxThres.replace(">", "");
                    String[] rxThresAr = rxThres.split(",", 0);
                    try {
                        slider.setValues((float) Integer.parseInt(rxThresAr[0]),(float) Integer.parseInt(rxThresAr[1]));
                    } catch (NumberFormatException e) {
                        slider.setValues(130.0f,500.0f);
                    }
                    lowerThrVal.setText("Encender en: " + Integer.parseInt(rxThresAr[0]));
                    higherThrVal.setText("Apagar en: " + Integer.parseInt(rxThresAr[1]));
                }
                msg("Niveles recibidos");
            }
        });

        slider.setOnTouchListener (new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                List<Float> sliderVals = slider.getValues();
                float lowerVal = Collections.min(sliderVals);
                float higherVal = Collections.max(sliderVals);
                lowerThrVal.setText("Encender en: " + (int)lowerVal);
                higherThrVal.setText("Apagar en: " + (int)higherVal);
                return false;
            }
        });

    }

    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            String varLuz = receiveSignal();
            int intVal = 0;
            try {
                intVal=Integer.parseInt(varLuz.replaceAll("[\\D]", ""));
            } catch (NumberFormatException e) {
                intVal=0;
            }
            lumn.setText(String.valueOf(intVal));
            wheel.setProgress(intVal);
        }
    };

    private String receiveSignal () {
        byte[] bytes = new byte[12];
        if ( btSocket != null ) {
            try {
                btSocket.getInputStream().read(bytes, 0, 12);
                receivedString = new String(bytes, StandardCharsets.UTF_8);
                if (receivedString == "") {
                    btSocket.getInputStream().read(bytes, 0, 12);
                    receivedString = new String(bytes, StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                msg("Error");
            }
        }
        return receivedString;
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause(){

        super.onPause();
        if(progress != null)
            progress.dismiss();
    }

    public void onResume() {
        super.onResume();
    }

    public void onRestart() {

        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blankmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
