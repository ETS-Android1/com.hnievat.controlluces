package com.hnievat.controlluces;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.hnievat.controlluces.ui.login.LoginActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ledControl extends AppCompatActivity {

    public static MyDatabase myDatabase;
    public static String EXTRA_ADDRESS = "device_address";
    private static BluetoothSocket btSocket;
    private static String receivedString;
    Button btn2, btn3;
    SwitchMaterial switch2;
    String address = null;
    TextView lumn;
    ProgressBar wheel;
    RangeSlider slider;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String settings_extra_address;

    public float fakeLowerTh=130, fakeHigherTh=440;
    public boolean shouldRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        settings_extra_address = address;

        myDatabase=Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"infodb").allowMainThreadQueries().build();

        Toolbar myToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);

        btn3 = findViewById(R.id.button5);
        lumn = findViewById(R.id.textView2);
        wheel = findViewById(R.id.sensorBar);
        switch2 = findViewById(R.id.switch2);

        new ConnectBT().execute();

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                String NDEF_PREF = "sharedPreferences";
                SharedPreferences sharedPreferences = getSharedPreferences(NDEF_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if(isChecked) {
                    sendSignal("A"); //on
                    long date=System.currentTimeMillis();
                    MyDatalist myDataList=new MyDatalist();
                    myDataList.setDate(date);
                    myDataList.setOn(true);
                    myDataList.setManual(true);
                    myDatabase.myDao().addData(myDataList);
                    editor.putLong("last_on", date);
                    editor.apply();
                } else {
                    sendSignal("A"); //off
                    long date=System.currentTimeMillis();
                    MyDatalist myDataList=new MyDatalist();
                    myDataList.setDate(date);
                    myDataList.setOn(false);
                    myDataList.setManual(true);
                    myDatabase.myDao().addData(myDataList);
                    long last_on = sharedPreferences.getLong("last_on", 0);
                    long newOnTime = 0;
                    if (last_on != 0) {
                        newOnTime = date - last_on;
                    }
                    long totalOnTime = sharedPreferences.getLong("total_on_time", 0);
                    totalOnTime = totalOnTime + newOnTime;
                    editor.putLong("total_on_time", totalOnTime);
                    editor.apply();
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("B"); //varluz
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    public static String receiveSignal() {
        if ( btSocket != null ) {
            Log.d("hola", "btsocket no es null");
            try {
                Log.d("hola", "adentro del primer try");
                int data;
                InputStream mmInStream = btSocket.getInputStream();
                if (mmInStream.available()>0) {
                    try {
                        byte[] bytes = new byte[128];
                        data = mmInStream.read(bytes);
                        receivedString = new String(bytes, 0, data);
                    } catch (IOException e) {
                        Log.e("hola", "desconectado", e);
                    }
                } else {
                    SystemClock.sleep(100);
                }
            } catch (IOException e) {
                Log.d("hola","error en receiveSignal()");
            }
        }
        Log.d("hola","a punto de salir de receiveSignal()");
        return receivedString;
    }

    static void sendSignal(String number) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (IOException e) {
                //msg("Error");
            }
        }
    }

    private void Disconnect () {
        String NDEF_PREF = "sharedPreferences";
        SharedPreferences sharedPreferences = getSharedPreferences(NDEF_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("device_address", null);
        editor.apply();
        if ( btSocket!=null ) {
            try {
                btSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }
        finish();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        private boolean ConnectSuccess = true;
        String btAddress = settings_extra_address;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(ledControl.this, "Conectando...", "Espere un momento");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( btSocket==null || !isBtConnected ) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(btAddress);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Conexión fallida. Verifique que el dispositivo seleccionado sea el correcto");
                String NDEF_PREF = "sharedPreferences";
                SharedPreferences sharedPreferences = getSharedPreferences(NDEF_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("device_address", null);
                editor.apply();
                finish();
            } else {
                msg("Conectado");
                isBtConnected = true;
            }

            progress.dismiss();
        }
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

    private void showSearch() {
        Intent intent = new Intent(this, DeviceList.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuactions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_disconnect:
                Disconnect();
                return true;

            case R.id.logOutButton:
                if ( btSocket!=null ) {
                    try {
                        btSocket.close();
                    } catch(IOException e) {
                        msg("Error");
                    }
                }
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_stats:
                Intent statsIntent = new Intent(this, statsActivity.class);
                startActivity(statsIntent);
                return true;

            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                settingsIntent.putExtra(EXTRA_ADDRESS, settings_extra_address);
                startActivity(settingsIntent);
                return true;

            case R.id.action_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}