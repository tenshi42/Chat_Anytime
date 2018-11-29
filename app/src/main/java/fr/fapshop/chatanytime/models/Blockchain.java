package fr.fapshop.chatanytime.models;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {


    private int difficulty;
    private List<Block> blocks;

    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        blocks = new ArrayList<>();
        // create the first block
        /*Block b = new Block(0, System.currentTimeMillis(), null, "First Block");
        b.mineBlock(difficulty);
        blocks.add(b);*/
    }

    public void addFirstBlock(String data, String user){
        Block b = new Block(0, System.currentTimeMillis(), null, data, user);
        b.mineBlock(difficulty);
        blocks.add(b);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Block latestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public Block newBlock(String data) {
        Block latestBlock = latestBlock();
        return new Block(latestBlock.getIndex() + 1, System.currentTimeMillis(),
                latestBlock.getHash(), data, latestBlock.getUser());
    }

    public void addBlock(Block b) {
        if (b != null) {
            b.mineBlock(difficulty);
            blocks.add(b);
        }
    }

    public void addBlock(Block b, boolean _new) {
        blocks.add(b);
    }

    public boolean isFirstBlockValid() {
        Block firstBlock = blocks.get(0);

        if (firstBlock.getIndex() != 0) {
            return false;
        }

        if (firstBlock.getPreviousHash() != null) {
            return false;
        }

        if (firstBlock.getHash() == null ||
                !Block.calculateHash(firstBlock).equals(firstBlock.getHash())) {
            return false;
        }

        return true;
    }

    public boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if (newBlock != null  &&  previousBlock != null) {
            if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
                return false;
            }

            if (newBlock.getPreviousHash() == null  ||
                    !newBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            if (newBlock.getHash() == null  ||
                    !Block.calculateHash(newBlock).equals(newBlock.getHash())) {
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean isBlockChainValid() {
        if (!isFirstBlockValid()) {
            return false;
        }

        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);

            if (!isValidNewBlock(currentBlock, previousBlock)) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Block block : blocks) {
            builder.append(block).append("\n");
        }

        return builder.toString();
    }

    public JSONArray toJson(){
        JSONArray j = new JSONArray();
        for(Block block : blocks){
            j.put(block.toJson());
        }
        return j;
    }

    public String toJsonString(){
        return toJson().toString();
    }

    public List<Block> getBlocks(){
        return blocks;
    }

}
