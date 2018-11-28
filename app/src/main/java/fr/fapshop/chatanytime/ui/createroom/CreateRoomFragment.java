package fr.fapshop.chatanytime.ui.createroom;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.fapshop.chatanytime.ChatActivity;
import fr.fapshop.chatanytime.MainActivity;
import fr.fapshop.chatanytime.R;

public class CreateRoomFragment extends Fragment implements View.OnClickListener {

    private CreateRoomViewModel mViewModel;
    private EditText etRoomName;
    private EditText etPseudo;

    public static CreateRoomFragment newInstance() {
        return new CreateRoomFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_room_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etRoomName = getView().findViewById(R.id.etRoomName);
        etPseudo = getView().findViewById(R.id.etPseudo);
        Button btnCreateRoom = getView().findViewById(R.id.btnRoomCreate);
        btnCreateRoom.setOnClickListener(this);
        if(!MainActivity.createRoom){
            etRoomName.setText(MainActivity.roomName);
            etRoomName.setActivated(false);
        }
        else{
            etRoomName.setText("");
            etRoomName.setActivated(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CreateRoomViewModel.class);
        // TODO: Use the ViewModel

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnRoomCreate){
            if(etPseudo.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please choose a pseudo !", Toast.LENGTH_LONG).show();
                return;
            }
            Intent i = new Intent(getActivity(), ChatActivity.class);
            i.putExtra("roomName", etRoomName.getText().toString());
            i.putExtra("pseudo", etPseudo.getText().toString());
            i.putExtra("create", MainActivity.createRoom);
            startActivity(i);
        }
    }
}
