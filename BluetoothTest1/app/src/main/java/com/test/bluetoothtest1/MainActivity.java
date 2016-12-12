package com.test.bluetoothtest1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 200;

    private BluetoothAdapter mBTAdapter;
    private BTService _btService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ContextUtil.CONTEXT = getApplicationContext();

        _btService = new BTService(getApplicationContext());

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBTAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "device dose not support Bluetooth", Toast.LENGTH_LONG).show();


        }
        else
        {
            if(!mBTAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }
        }



        Button btnScan = (Button)findViewById(R.id.button);
        btnScan.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.v("Log","btnScan");
                discovery();
            }
        });

        Button btnVib = (Button)findViewById(R.id.button2);
        btnVib.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.v("Log","Vibration");

                byte[] packet = new byte[3];

                packet[0] = (byte)0xff;
                packet[1] = (byte)2;
                packet[2] = (byte)0xfe;

                _btService.sendByte(packet);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.i("MainActivity.java | onActivityResult", "|==" + requestCode + "|" + resultCode + "(ok = " + RESULT_OK + ")|" + data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_ENABLE_BT)
        {
            discovery();
        }
        else if (requestCode == REQUEST_CONNECT_DEVICE_INSECURE)
        {
            String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

            // Log.v("MainActivity.java | onActivityResult", "|==" + address + "|");
            if (TextUtils.isEmpty(address))
                return;

            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
            _btService.connect(device);
        }
    }



    private void discovery()
    {

        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }



}
