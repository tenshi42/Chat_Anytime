package fr.fapshop.chatanytime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.fapshop.chatanytime.ui.createroom.CreateRoomFragment;

public class CreateRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CreateRoomFragment.newInstance())
                    .commitNow();
        }
    }
}
