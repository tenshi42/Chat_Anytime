package fr.fapshop.chatanytime;

import android.support.annotation.NonNull;

import fr.fapshop.chatanytime.models.Blockchain;

public class BlockchainMessagesInfos implements Comparable<BlockchainMessagesInfos>{


    public Blockchain blockchain;


    public BlockchainMessagesInfos(Blockchain blockchain){
        this.blockchain = blockchain;
    }

    @Override
    public int compareTo(@NonNull BlockchainMessagesInfos blockchainMessagesInfos) {
        return 0;
    }
}
