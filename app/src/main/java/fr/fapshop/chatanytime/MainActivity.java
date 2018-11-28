package fr.fapshop.chatanytime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String BLE_PIN = "1234";
    private static final String TAG = "myAppDebug";
    private final UUID MY_UUID = UUID.fromString("203ed53a-8a64-4bfd-a496-7d835c863bff");
    private IntentFilter filter;
    private IntentFilter intentFilter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService;

    private String mConnectedDeviceName = null;
    private Activity myActivity;

    private ListView lvRooms;
    private ArrayList<RoomInfos> roomList;
    private ArrayList<RoomClient> currentRoomClients;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myActivity = this;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothAdapter.setName("CA:Room1:666");
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
        //mBluetoothAdapter.enable();

        mChatService = new BluetoothChatService(this, mHandler);
        mChatService.start();

        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        Button btnClient = findViewById(R.id.btnClient);
        Button btnServer = findViewById(R.id.btnServer);
        btnClient.setOnClickListener(this);
        btnServer.setOnClickListener(this);

        lvRooms = findViewById(R.id.lvRooms);
        lvRooms.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RoomInfos item = (RoomInfos)adapterView.getItemAtPosition(i);

                Toast.makeText(getBaseContext(), item.clients.toString(), Toast.LENGTH_LONG).show();
                connectToRoom(item);
            }
        });
        roomList = new ArrayList<>();

        registerReceiver(mReceiver, filter);
        while(!mBluetoothAdapter.isEnabled());
        boolean ok = mBluetoothAdapter.startDiscovery();
        Log.d(TAG, String.valueOf(ok));

        intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(broadCastReceiver, intentFilter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.btnClient: {
                // do something for button 1 click
                //Log.d(TAG, "gonna list !");
                //Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                /*if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        Log.d(TAG, "Op : dn : " + deviceName + " | mac : " + deviceHardwareAddress);
                    }
                }*/
                break;
            }

            case R.id.btnServer: {
                // do something for button 2 click
                Toast.makeText(myActivity, "send toast", Toast.LENGTH_LONG).show();
                mChatService.write("toast".getBytes());
                break;
            }

            //.... etc
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, "dn : " + deviceName + " | mac : " + deviceHardwareAddress);
                if(deviceName != null) {
                    String[] tmp = deviceName.split(":");
                    if(tmp.length != 3)
                        return;
                    Log.d(TAG, "onRecdddeive: " + String.valueOf(tmp.length) + " : " + tmp[1]);
                    if(!tmp[0].equals("CA"))
                        return;

                    String roomName = tmp[1];
                    boolean newRoom = true;
                    for(RoomInfos ri : roomList) {
                        if (ri.name.equals(roomName)) {
                            ri.clients.add(new RoomClient(deviceHardwareAddress, ConnectionProtocol.Bluetooth, device));
                            newRoom = false;
                            break;
                        }
                    }
                    if(newRoom){
                        RoomInfos tmpRoom = new RoomInfos(roomName);
                        tmpRoom.clients.add(new RoomClient(deviceHardwareAddress, ConnectionProtocol.Bluetooth, device));
                        roomList.add(tmpRoom);
                    }
                }


                RoomListAdapter roomListAdapter = new RoomListAdapter(myActivity, 0, roomList);

                lvRooms.setAdapter(roomListAdapter);
            }
        }
    };

    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action))
            {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = bluetoothDevice.getName();
                Log.d(TAG, "pairing : " + name);
                bluetoothDevice.setPin(BLE_PIN.getBytes());
                Log.e(TAG,"Auto-entering pin: " + BLE_PIN);
                bluetoothDevice.createBond();
                Log.e(TAG,"pin entered and request sent...");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(myActivity, readMessage, Toast.LENGTH_LONG).show();
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != myActivity) {
                        Toast.makeText(myActivity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != myActivity) {
                        Toast.makeText(myActivity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void connectToRoom(RoomInfos roomInfos){
        if(roomInfos.clients.get(0).protocol == ConnectionProtocol.Bluetooth){
            mChatService.connect((BluetoothDevice)roomInfos.clients.get(0).connectionObject, false);
        }
    }
}
