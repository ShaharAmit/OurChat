package shahar.shenkar.ourchat.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.events.changeTitle;
import shahar.shenkar.ourchat.events.ReplaceMainFragmentEvent;
import shahar.shenkar.ourchat.objects.Model;


public class ChatsListFragment extends Fragment {
    public ListView groupsList;
    public ArrayList<String> groups  = new ArrayList<>();
    public ArrayAdapter<String> listAdapter;
    Model model = Model.getInstance();
    Activity activity;
    DatabaseReference reference;
    ValueEventListener ls;
    Button plus;
    public ChatsListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_chats_list, container, false);
        activity = getActivity();


        groupsList = (ListView) view.findViewById(R.id.chatsList);
        model.safeMove =false;
        plus = (Button)view.findViewById(R.id.addChat);
        plus.setOnClickListener(click-> {
            EventBus.getDefault().post(new ReplaceMainFragmentEvent("AddChats"));
        });
        reference = model.database.getReference().child("Users/"+model.userUid +"/courses");
        if(!model.isChatting) {
            ls = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groups.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        for (DataSnapshot group : item.getChildren()) {
                            groups.add(item.getKey()+" - "+group.getValue().toString());
                        }
                    }
                    model.safeMove = true;
                    updateList();
                    reference.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            groupsList.setOnItemClickListener((parent, view1, position, id) -> {
                model.currentCourse = groups.get(position);
                model.courseMain = groups.get(position).split(" ")[0];
                model.courseGroup = groups.get(position).split(" - ")[1];
                System.out.println("main: " + model.courseMain + " group: " + model.courseGroup);
                model.selectedCourseNum = position;
                model.isChatting = true;
                EventBus.getDefault().post(new changeTitle(groups.get(position)));
                EventBus.getDefault().post(new ReplaceMainFragmentEvent("Chat"));

            });
        } else {
            EventBus.getDefault().post(new ReplaceMainFragmentEvent("Chat"));
        }
        return view;
    }
    private void updateList(){
        if (model.safeMove){
            if(activity!=null)
            listAdapter = new ArrayAdapter<>(activity,android.R.layout.simple_list_item_1, groups);
            groupsList.setAdapter(listAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        activity = null;
    }
}
