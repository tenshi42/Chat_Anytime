package fr.fapshop.chatanytime.models;

import android.util.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

import fr.fapshop.chatanytime.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Block {

    private int index;
    private long timestamp;
    private String hash;
    private String previousHash;
    private String data;
    private String user;
    private int nonce;

    public Block(int index, long timestamp, String previousHash, String data, String user) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
        nonce = 0;
        hash = Block.calculateHash(this);
        this.user = user;
    }

    public Block(int index, long timestamp, String hash, String previousHash, String data, String user) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
        nonce = 0;
        this.hash = hash;
        this.user = user;
    }

    public int getIndex() {
        return index;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public String getUser() {
        return user;
    }

    public String str() {
        return index + timestamp + previousHash + data + nonce + user;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Block #").append(index).append(" [previousHash : ").append(previousHash).append(", ").
                append("timestamp : ").append(new Date(timestamp)).append(", ").append("data : ").append(data).append(", ").
                append("hash : ").append(hash).append("]");
        return builder.toString();
    }

    public JSONObject toJson(){
        JSONObject j = new JSONObject();
        try {
            j.put("index", index);
            j.put("previousHash", previousHash);
            j.put("timestamp", timestamp);
            j.put("data", data);
            j.put("hash", hash);
            j.put("user", user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return j;
    }

    public String toJsonString(){
        return toJson().toString();
    }

    public static String calculateHash(Block block) {
        if (block != null) {
            MessageDigest digest = null;

            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }

            String txt = block.str();
            final byte bytes[] = digest.digest(txt.getBytes());
            final StringBuilder builder = new StringBuilder();

            for (final byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    builder.append('0');
                }

                builder.append(hex);
            }

            return builder.toString();
        }

        return null;
    }

    public void mineBlock(int difficulty) {
        nonce = 0;

        while (!getHash().substring(0,  difficulty).equals(Utils.zeros(difficulty))) {
            nonce++;
            hash = Block.calculateHash(this);
        }
    }

    public static Block jsonToBlock(JSONObject jsonObject){
        Block tmpBlock = null;
        try {
            int tmpIndex = jsonObject.getInt("index");
            String tmpData = jsonObject.getString("data");
            String tmpUser = jsonObject.getString("user");
            long tmpTimestamp = jsonObject.getLong("timestamp");
            String tmpHash = jsonObject.getString("hash");
            String tmpPreviousHash = jsonObject.getString("previousHash");
            tmpBlock = new Block(tmpIndex, tmpTimestamp, tmpHash, tmpPreviousHash, tmpData, tmpUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tmpBlock;
    }

    public String getTimeStampString(){
        return String.valueOf(timestamp);
    }
}
