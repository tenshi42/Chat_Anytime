package fr.fapshop.chatanytime;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class RoomListAdapter extends ArrayAdapter<RoomInfos> {
    private Activity activity;
    private ArrayList<RoomInfos> lRooms;
    private static LayoutInflater inflater = null;

    public RoomListAdapter (Activity activity, int textViewResourceId,ArrayList<RoomInfos> _rooms) {
        super(activity, textViewResourceId, _rooms);
        try {
            this.activity = activity;
            this.lRooms = _rooms;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

    public int getCount() {
        return lRooms.size();
    }

    public RoomInfos getItem(RoomInfos position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.room_list_elem, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) vi.findViewById(R.id.tvName);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.display_name.setText(lRooms.get(position).name);


        } catch (Exception e) {


        }
        return vi;
    }
}