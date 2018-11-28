package fr.fapshop.chatanytime;

import android.support.annotation.NonNull;

import java.util.ArrayList;

class RoomClient{
    public String addr;
    public ConnectionProtocol protocol;
    public Object connectionObject;

    public RoomClient(String addr, ConnectionProtocol protocol, Object connectionObject){
        this.addr = addr;
        this.protocol = protocol;
        this.connectionObject = connectionObject;
    }
}

public class RoomInfos implements Comparable<RoomInfos>{
    public String name;
    public ArrayList<RoomClient> clients;

    public RoomInfos(String name){
        this.name = name;
        this.clients = new ArrayList<>();
    }

    public boolean equals(Object o) {
        if(o instanceof RoomInfos) {
            RoomInfos c = (RoomInfos)o;
            return name.equals(c.name);
        }
        return false;
    }

    public int hashCode() {
        return name.hashCode();
    }


    @Override
    public int compareTo(@NonNull RoomInfos roomInfos) {
        return name.compareTo(roomInfos.name);
    }
}
