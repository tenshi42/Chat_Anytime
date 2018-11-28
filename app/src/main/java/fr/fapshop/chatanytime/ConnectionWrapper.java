package fr.fapshop.chatanytime;

public class ConnectionWrapper {
    private Object connectionObject;
    private ConnectionProtocol protocol;
    private boolean isHost;

    public ConnectionWrapper(Object connectionObject, ConnectionProtocol protocol, boolean isHost){
        this.connectionObject = connectionObject;
        this.protocol = protocol;
        this.isHost = isHost;
    }

    public void send(String msg){
        if(this.protocol.equals(ConnectionProtocol.Bluetooth)){
            BluetoothChatService tmp = (BluetoothChatService)connectionObject;
            tmp.write(msg.getBytes());
        }
    }
}
