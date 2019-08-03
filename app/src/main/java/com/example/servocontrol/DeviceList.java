package com.example.servocontrol;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ListView;
import java.util.Set;
import java.util.ArrayList;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

public class DeviceList  extends AppCompatActivity {
    private Button btnPaired;
    private ListView devicelist;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prvi);

        btnPaired = findViewById(R.id.btnConnect);
        devicelist = findViewById(R.id.lvDevices);

        btnPaired.setOnClickListener(e ->{
            pairedDevicesList();
        });

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null){
            Toast.makeText(getApplicationContext(), "Nes nije ok", Toast.LENGTH_LONG).show();
            finish();
        }else{
            if(myBluetooth.isEnabled()){

            }else{
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }
    }

    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice t : pairedDevices) {
                list.add(t.getName() + "\n" + t.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kurac", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener((adapterView, view, i, l) -> {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent(DeviceList.this, Control.class);
            intent.putExtra(EXTRA_ADDRESS, address);
            startActivity(intent);
        });
    }
}
