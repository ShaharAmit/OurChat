package shahar.shenkar.ourchat.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.events.ReplaceMainFragmentEvent;
import shahar.shenkar.ourchat.objects.Model;

public class MenuFragment extends Fragment {
    Button btsignOut ;
    Button btDel;
    Model model = Model.getInstance();

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        model.safeMove = false;
        btsignOut = (Button) view.findViewById(R.id.btOut);
        btDel = (Button)view.findViewById(R.id.btDel);
        btsignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            EventBus.getDefault().post(new ReplaceMainFragmentEvent("login"));
        });
        btDel.setOnClickListener(v -> deleteChats());

        return view;
    }

    private void deleteChats() {
        model.database.getReference().child("Users/" + model.userUid + "/courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courses : dataSnapshot.getChildren()) {
                    for (DataSnapshot names : courses.getChildren()) {
                        Log.d("here!!!", dataSnapshot.getKey() + " " + names.getValue().toString());
                        model.database.getReference().child("Institution/" + model.institution + "/" + courses.getKey() + "/groups/" + names.getValue().toString() + "/users/" + model.userUid + "/name").setValue(null);
                    }
                }
                model.database.getReference().child("Users/" + model.userUid).setValue(null).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        model.database.getReference().child("InstitutionData/users/" + model.id + "/signed").setValue(false).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                model.currentUser.delete().addOnCompleteListener(task3 -> {
                                    if (task3.isSuccessful()) {
                                        EventBus.getDefault().post(new ReplaceMainFragmentEvent("login"));
                                    } else
                                        Log.d("Deleting", "Error deleting user! " + task3.getResult());
                                });
                            } else
                                Log.d("Deleting", "Error free SignUp!");
                        });
                    } else
                        Log.d("Deleting", "Error delete user details!");
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        btDel.setOnClickListener(null);
        btsignOut.setOnClickListener(null);
    }
}
