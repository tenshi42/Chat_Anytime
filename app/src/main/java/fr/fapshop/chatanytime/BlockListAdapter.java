package fr.fapshop.chatanytime;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;


public class BlockListAdapter extends ArrayAdapter<BlockchainMessagesInfos> {

    private Activity activity;
    private ArrayList<BlockchainMessagesInfos> currentBlockchain;
    private static LayoutInflater inflater = null;



    public BlockListAdapter (Activity activity, int textViewResourceId,ArrayList<BlockchainMessagesInfos> blockchain) {
        super(activity, textViewResourceId, blockchain);
        try {
            this.activity = activity;
            this.currentBlockchain = blockchain;
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }
    public int getCount() {
        return currentBlockchain.size();
    }

    public BlockchainMessagesInfos getItem(BlockchainMessagesInfos position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView blockUser;
        public TextView blockTimeStamp;
        public TextView blockMessage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final BlockListAdapter.ViewHolder holder;
        try {
            if (convertView == null) {
                //hangin
                v = inflater.inflate(R.layout.block_element, null);
                holder = new BlockListAdapter.ViewHolder();

                holder.blockUser = (TextView) v.findViewById(R.id.tvUser);
                holder.blockMessage = (TextView) v.findViewById(R.id.tvMessageData);
                holder.blockTimeStamp = (TextView) v.findViewById(R.id.tvTimeStampBlock);

                v.setTag(holder);
            } else {
                holder = (BlockListAdapter.ViewHolder) v.getTag();
            }

            holder.blockUser.setText(currentBlockchain.get(position).blockchain.getBlocks().get(position).getUser());
            holder.blockTimeStamp.setText(currentBlockchain.get(position).blockchain.getBlocks().get(position).getTimeStampString());
            holder.blockMessage.setText(currentBlockchain.get(position).blockchain.getBlocks().get(position).getData());

        } catch (Exception e) {


        }
        return v;
    }

}
