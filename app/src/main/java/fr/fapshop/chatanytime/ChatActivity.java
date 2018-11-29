package fr.fapshop.chatanytime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import fr.fapshop.chatanytime.models.Block;
import fr.fapshop.chatanytime.models.Blockchain;

public class ChatActivity extends AppCompatActivity {

    private String roomName;
    private String pseudo;

    private String mConnectedDeviceName = null;
    private Activity myActivity;

    private RoomClient[] currentRoomClients;
    private BluetoothChatService[] bluetoothChatServices;

    private ArrayList<Block> messages;
    private int currentOpenedConnection;
    private boolean needRenew = true;

    private Blockchain blockchain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myActivity = this;

        blockchain = new Blockchain(1);

        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        pseudo = intent.getStringExtra("pseudo");
        boolean create = intent.getBooleanExtra("create", false);

        currentRoomClients = new RoomClient[3];

        resetBluetoothConnections();
        initBluetoothConnections(roomName, pseudo);

        if(!create){
            tryConnect();
        }
        else{
            blockchain.addFirstBlock("Room " + roomName + " created !", pseudo);
        }

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    renewClients();
                }
            },
            60000);
    }

    public void initBluetoothConnections(String roomName, String pseudo){
        MainActivity.mBluetoothAdapter.setName("CA:"+roomName+":"+pseudo);
        bluetoothChatServices = new BluetoothChatService[3];
        openBluetoothConnection();
    }

    private void resetBluetoothConnections(){
        for(int i = 0 ; i < 3 ; i++){
            if(bluetoothChatServices[i] != null) {
                if(bluetoothChatServices[i].getState() != BluetoothChatService.STATE_NONE)
                    bluetoothChatServices[i].stop();
                break;
            }
        }
    }

    private boolean openBluetoothConnection(){
        boolean ret = false;
        for(int i = 0 ; i < 3 ; i++){
            if(bluetoothChatServices[i] != null) {
                if(bluetoothChatServices[i].getState() != BluetoothChatService.STATE_NONE)
                    continue;
            }
            currentOpenedConnection = i;
            bluetoothChatServices[i] = new BluetoothChatService(myActivity, mHandler, i);
            bluetoothChatServices[i].start();
            ret = true;
            break;
        }
        return ret;
    }

    private boolean needSearching(){
        int c = 0;
        for(int i = 0 ; i < 3 ; i++) {
            if (bluetoothChatServices[i] != null) {
                if (bluetoothChatServices[i].getState() == BluetoothChatService.STATE_NONE)
                    c++;
            }
            else{
                c++;
            }
        }
        return c < 1;
    }

    private RoomClient getClientToConnect(){
        RoomClient ret = null;

        ArrayList<RoomClient> tmpClients = (ArrayList<RoomClient>)MainActivity.currentRoom.clients.clone();
        Collections.shuffle(tmpClients);

        for(RoomClient rc : tmpClients){
            boolean exist = false;
            for(int i = 0 ; i < 3 ; i ++){
                if(currentRoomClients[i].addr.equals(rc.addr)){
                    exist = true;
                    break;
                }
            }
            if(!exist){
                ret = rc;
                break;
            }
        }

        return ret;
    }

    private void tryConnect(){
        if(!needSearching())
            return;

        RoomClient tmpRC = getClientToConnect();
        if(tmpRC != null) {
            bluetoothChatServices[currentOpenedConnection].connect((BluetoothDevice) MainActivity.currentRoom.clients.get(0).connectionObject, false);

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if (bluetoothChatServices[currentOpenedConnection].getState() == BluetoothChatService.STATE_NONE) {
                                tryConnect();
                            }
                            else {
                                if(messages == null)
                                    getBlocks(bluetoothChatServices[currentOpenedConnection]);
                                if(openBluetoothConnection())
                                    tryConnect();
                            }
                        }
                    },
                    5000);
        }
        else {
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        tryConnect();
                    }
                },
                5000);
        }
    }

    private void renewClients(){
        if(!needRenew)
            return;
        MainActivity.currentRoom = new RoomInfos(roomName);
        MainActivity.mBluetoothAdapter.cancelDiscovery();
        MainActivity.mBluetoothAdapter.startDiscovery();
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    renewClients();
                }
            },
            60000);
    }

    private void getBlocks(BluetoothChatService chatService){
        JSONObject j = new JSONObject();
        try {
            j.put("index", -1);
            j.put("cmd", "getBlocks");
            j.put("data","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messages = new ArrayList<>();
        chatService.write(j.toString().getBytes());
    }

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
                    byte[] writeBuf = ((HandlerObject)msg.obj).buffer;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = ((HandlerObject)msg.obj).buffer;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(myActivity, readMessage, Toast.LENGTH_LONG).show();
                    treatMessage(readMessage, ((HandlerObject)msg.obj).sender);
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

    private void treatMessage(String msg, BluetoothChatService sender){
        try {
            JSONObject j = new JSONObject(msg);
            int id = j.getInt("index");
            String cmd = j.getString("cmd");

            if(id == -1){
                if(cmd.equals("getBlocks")){
                    JSONArray tmp = blockchain.toJson();
                    JSONObject jo = new JSONObject();
                    jo.put("index", -1);
                    jo.put("cmd", "recvBlocks");
                    jo.put("data", tmp);

                    sender.write(jo.toString().getBytes());
                }
                else if(cmd.equals("recvBlocks")){
                    JSONArray data = j.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Block tmpBlock = Block.jsonToBlock(jsonObject);
                        blockchain.addBlock(tmpBlock, true);
                    }
                    Toast.makeText(this, "get blocks !!!!!!", Toast.LENGTH_LONG);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MainActivity.mBluetoothAdapter.cancelDiscovery();
    }
}
