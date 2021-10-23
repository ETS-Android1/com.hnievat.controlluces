package com.hnievat.controlluces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.TimeUnit;

public class statsActivity extends AppCompatActivity {

    Button btn6;
    AlertDialog.Builder builder;
    TextView txtTotalOnTime, txtTotalWh, txtTotalCost, txtDeviceWh, txtWhRate;
    LinearLayout btnDeviceWh, btnWhRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Toolbar myToolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        btn6 = findViewById(R.id.button6);
        builder = new AlertDialog.Builder(this);
        txtTotalOnTime = findViewById(R.id.horasNumText);
        txtTotalWh = findViewById(R.id.consumoNumText);
        txtTotalCost = findViewById(R.id.costoNumText);
        txtDeviceWh = findViewById(R.id.consumoDispNumText);
        txtWhRate = findViewById(R.id.costoElecDispNumText);
        btnDeviceWh = findViewById(R.id.linl10);
        btnWhRate = findViewById(R.id.linl12);

        String NDEF_PREF = "sharedPreferences";
        SharedPreferences sharedPreferences = getSharedPreferences(NDEF_PREF, Context.MODE_PRIVATE);
        //recibe datos existentes de sharedprefs
        long totalOnTime = sharedPreferences.getLong("total_on_time", 0);
        String readableOnTime = TimeUnit.MILLISECONDS.toHours(totalOnTime) + "h " +
                (TimeUnit.MILLISECONDS.toMinutes(totalOnTime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalOnTime))) + "m";
        txtTotalOnTime.setText(readableOnTime);
        float deviceWh = sharedPreferences.getFloat("device_wh", 0);
        txtDeviceWh.setText(String.format("%.2f", deviceWh) + " W/h");
        float whRate = sharedPreferences.getFloat("wh_rate", 0);
        txtWhRate.setText("$" + String.format("%.3f", whRate));
        //calcula datos derivados
        double totalWh = (totalOnTime / 3600000.0) * deviceWh;
        txtTotalWh.setText(String.format("%.2f", totalWh) + " W/h");
        double totalCost = totalWh * whRate;
        txtTotalCost.setText("$" + String.format("%.2f", totalCost));

        btnDeviceWh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(statsActivity.this);
               builder.setTitle("Watts/hora del dispositivo");
               final EditText input = new EditText(statsActivity.this);
               LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.MATCH_PARENT,
                       LinearLayout.LayoutParams.MATCH_PARENT);
               input.setLayoutParams(lp);
               builder.setView(input);

               builder.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       SharedPreferences.Editor editor = sharedPreferences.edit();
                       editor.putFloat("device_wh", Float.parseFloat(input.getText().toString()));
                       editor.apply();
                       float deviceWh = sharedPreferences.getFloat("device_wh", 0);
                       txtDeviceWh.setText(String.format("%.2f", deviceWh) + " W/h");
                       double totalWh = (totalOnTime / 3600000.0) * deviceWh;
                       txtTotalWh.setText(String.format("%.2f", totalWh) + " W/h");
                       double totalCost = totalWh * whRate;
                       txtTotalCost.setText("$" + String.format("%.2f", totalCost));
                   }
               });
               builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               builder.show();
           }
        });

        btnWhRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(statsActivity.this);
                builder.setTitle("Tarifa del Watt/hora");
                final EditText input = new EditText(statsActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("wh_rate", Float.parseFloat(input.getText().toString()));
                        editor.apply();
                        float whRate = sharedPreferences.getFloat("wh_rate", 0);
                        txtWhRate.setText("$" + String.format("%.3f", whRate));
                        double totalWh = (totalOnTime / 3600000.0) * deviceWh;
                        txtTotalWh.setText(String.format("%.2f", totalWh) + " W/h");
                        double totalCost = totalWh * whRate;
                        txtTotalCost.setText("$" + String.format("%.2f", totalCost));
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(statsActivity.this);
                builder.setMessage("El historial y las estadísticas actuales serán eliminados permanentemente")
                        .setTitle("¿Restablecer historial y estadísticas?");
                builder.setPositiveButton("Restablecer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ledControl.myDatabase.myDao().deleteAll();
                        Toast.makeText(getApplicationContext(), "Historial y estadísticas eliminados",
                                Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("total_on_time", 0);
                        editor.apply();
                        long totalOnTime = sharedPreferences.getLong("total_on_time", 0);
                        String readableOnTime = TimeUnit.MILLISECONDS.toHours(totalOnTime) + "h " +
                                (TimeUnit.MILLISECONDS.toMinutes(totalOnTime) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalOnTime))) + "m";
                        txtTotalOnTime.setText(readableOnTime);
                        float deviceWh = sharedPreferences.getFloat("device_wh", 0);
                        txtDeviceWh.setText(String.format("%.2f", deviceWh) + " W/h");
                        float whRate = sharedPreferences.getFloat("wh_rate", 0);
                        txtWhRate.setText("$" + String.format("%.3f", whRate));
                        //calcula datos derivados
                        double totalWh = (totalOnTime / 3600000.0) * deviceWh;
                        txtTotalWh.setText(String.format("%.2f", totalWh) + " W/h");
                        double totalCost = totalWh * whRate;
                        txtTotalCost.setText("$" + String.format("%.2f", totalCost));
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void onResume() {
        super.onResume();
        String NDEF_PREF = "sharedPreferences";
        SharedPreferences sharedPreferences = getSharedPreferences(NDEF_PREF, Context.MODE_PRIVATE);
        long totalOnTime = sharedPreferences.getLong("total_on_time", 0);
        String readableOnTime = TimeUnit.MILLISECONDS.toHours(totalOnTime) + "h " +
                (TimeUnit.MILLISECONDS.toMinutes(totalOnTime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalOnTime))) + "m";
        txtTotalOnTime.setText(readableOnTime);
        float deviceWh = sharedPreferences.getFloat("device_wh", 0);
        txtDeviceWh.setText(String.format("%.2f", deviceWh) + " W/h");
        float whRate = sharedPreferences.getFloat("wh_rate", 0);
        txtWhRate.setText("$" + String.format("%.3f", whRate));
        //calcula datos derivados
        double totalWh = (totalOnTime / 3600000.0) * deviceWh;
        txtTotalWh.setText(String.format("%.2f", totalWh) + " W/h");
        double totalCost = totalWh * whRate;
        txtTotalCost.setText("$" + String.format("%.2f", totalCost));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.statsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history:
                Intent historyIntent = new Intent(this, historyActivity.class);
                startActivity(historyIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}