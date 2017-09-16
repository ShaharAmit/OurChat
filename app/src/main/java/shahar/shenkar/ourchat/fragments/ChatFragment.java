package shahar.shenkar.ourchat.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.objects.ChatMessage;
import shahar.shenkar.ourchat.objects.MessageAdapter;
import shahar.shenkar.ourchat.objects.Model;
import shahar.shenkar.ourchat.objects.PbMethods;

public class ChatFragment extends Fragment {
    Model model = Model.getInstance();
    PbMethods pbMethods = PbMethods.getInstance();
    Activity activity;
    FirebaseListAdapter<ChatMessage> adapter;

    private EditText input;
    private FloatingActionButton fab;
    DatabaseReference ref;
    DatabaseReference ref2;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ListView listView = (ListView) view.findViewById(R.id.list);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        input = (EditText) view.findViewById(R.id.input);


        ref = model.database.getReference().child("Institution/" + model.institution +"/" + model.courseMain + "/groups/" + model.courseGroup +"/messages");
        ref2 = model.database.getReference().child("notifications");

        adapter = new MessageAdapter( activity, ChatMessage.class, R.layout.item_in_message, ref);
        listView.setAdapter(adapter);

        SetSendListener();

        return view;
    }
    public void SetSendListener(){
        fab.setOnClickListener(view -> {
            pbMethods.CloseKeyBoard(activity);

            if (!input.getText().toString().trim().equals("")) {

                ref.push().setValue(new ChatMessage(
                        input.getText().toString(),
                        model.currentUser.getDisplayName(),
                        model.userUid,
                        model.currentCourse)
                );

                ref2.push().setValue(new ChatMessage(
                        input.getText().toString(),
                        model.currentUser.getDisplayName(),
                        model.userUid,
                        model.currentCourse)
                );

                input.setText("");
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        fab.setOnClickListener(null);
        activity = null;
        adapter.cleanup();
    }

}
