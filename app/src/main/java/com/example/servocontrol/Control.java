package com.example.servocontrol;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.UUID;

public class Control extends AppCompatActivity {

    ImageButton btnPravo, btnLevo, btnDesno;
    Button btnDis;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drugi);

        Intent newIntent = getIntent();
        address = newIntent.getStringExtra(DeviceList.EXTRA_ADDRESS);
        btnPravo = findViewById(R.id.btnPravo);
        btnLevo = findViewById(R.id.btnLevo);
        btnDesno = findViewById(R.id.btnDesno);
        btnDis = findViewById(R.id.btnDisc);

        new ConnectBT().execute();

        btnPravo.setOnTouchListener(new View.OnTouchListener(){
            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    akcija('p');
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        btnPravo.setOnClickListener(e -> {
            akcija('s');
        });

        btnLevo.setOnTouchListener(new View.OnTouchListener(){
            private Handler mHandler;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 30);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    akcija('l');
                    mHandler.postDelayed(this, 30);
                }
            };
        });


        btnDesno.setOnTouchListener(new View.OnTouchListener(){
            private Handler mHandler;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 30);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    akcija('d');
                    mHandler.postDelayed(this, 30);
                }
            };
        });

        btnDis.setOnClickListener(e ->{
            disconnect();
        });
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>{
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Control.this, "Povezujem se...", "Cekaj jebem ti krvotok");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try{
                if(btSocket == null){
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dis = myBluetooth.getRemoteDevice(address);
                    btSocket = dis.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException ex){
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!ConnectSuccess)
            {
                msg("SPP bluetooth??");
                finish();
            }
            else
            {
                msg("Povezan.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    private void disconnect() {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout
    }

    private void akcija(Character c){
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write(c);
            }
            catch (IOException e) {
                msg("Error");
            }
        }
    }
}
