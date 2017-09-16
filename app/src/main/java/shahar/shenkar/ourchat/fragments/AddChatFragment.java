package shahar.shenkar.ourchat.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.objects.Model;
import shahar.shenkar.ourchat.objects.PbMethods;

public class AddChatFragment extends Fragment {
    Spinner courseSP;
    Spinner usersSP;
    ListView usersListLV;
    Button addCourseBT;
    EditText courseName;

    List<String> courseSPlist =  new ArrayList<String>();
    List<String> usersSPlist =  new ArrayList<String>();
    List<String> usersIDlist =  new ArrayList<String>();
    List<String> usersPulllist =  new ArrayList<String>();
    ArrayList<String> usersListAL  = new ArrayList<>();

    ArrayAdapter<String> courseSPadapter;
    ArrayAdapter<String> usersSPadapter;
    ArrayAdapter<String> usersListLVadapter;

    int selectedCourse;


    Model model = Model.getInstance();
    PbMethods pbMethods = PbMethods.getInstance();

    Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_chat, container, false);
        activity = getActivity();
        courseSP = (Spinner)view.findViewById(R.id.Course);
        usersSP = (Spinner)view.findViewById(R.id.Users);
        usersListLV = (ListView)view.findViewById(R.id.UsersList);
        addCourseBT = (Button)view.findViewById(R.id.addCourse);
        courseName = (EditText)view.findViewById(R.id.courseName);

        addCourseBT.setOnClickListener(click->{
            if(!courseName.getText().toString().isEmpty() && usersPulllist.size()>0) {
                DatabaseReference ref = model.database.getReference().child("Institution/"+model.institution+"/"+courseSPlist.get(selectedCourse)+"/groups");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isExist = false;
                        for (DataSnapshot courses : dataSnapshot.getChildren()) {
                            if(courses.getKey().equals(courseName.getText().toString())) {
                                isExist=true;
                            }
                        }
                        if (!isExist) {
                            ref.child(courseName.getText().toString()+"/users/"+model.userUid+"/name").setValue(model.name);
                            DatabaseReference myRef = model.database.getReference().child("Users/" + model.userUid + "/courses/" + courseSPlist.get(selectedCourse));
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int n=0;
                                    for (DataSnapshot coursesMe : dataSnapshot.getChildren()) {
                                        n++;
                                    }
                                    myRef.child(String.valueOf(n)).setValue(courseName.getText().toString());
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            for (int j=0; j<usersPulllist.size(); j++) {
                                ref.child(courseName.getText().toString()+"/users/"+usersIDlist.get(Integer.parseInt(usersPulllist.get(j)))+"/name").setValue(usersSPlist.get(Integer.parseInt(usersPulllist.get(j))+1));
                                DatabaseReference userRef = model.database.getReference().child("Users/"+usersIDlist.get(Integer.parseInt(usersPulllist.get(j)))+"/courses/" + courseSPlist.get(selectedCourse));
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int t=0;
                                        for (DataSnapshot courses : dataSnapshot.getChildren()){
                                            t++;
                                        }
                                        userRef.child(String.valueOf(t)).setValue(courseName.getText().toString());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        model.database.getReference().child("Users/"+model.userUid+"/courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot courses : dataSnapshot.getChildren()) {
                    courseSPlist.add(courses.getKey());
                    updateList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
    private void updateList() {
        if (activity != null)
            courseSPadapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, courseSPlist);
        courseSP.setAdapter(courseSPadapter);
        courseSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCourse = i;
                usersSPlist.clear();
                usersSPlist.add("users");
                usersIDlist.clear();
                usersPulllist.clear();
                model.database.getReference().child("Institution/" + model.institution + "/" + courseSPlist.get(i) + "/groups/Main/users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot id : dataSnapshot.getChildren()) {
                            for (DataSnapshot name : id.getChildren()) {
                                if (!id.getKey().equals(model.userUid)) {
                                    usersSPlist.add(name.getValue().toString());
                                    usersIDlist.add(id.getKey());
                                    UpdateUsersList();
                                }
                                Log.d("Users: ", id.getKey() + " " + name.getValue());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    private void UpdateUsersList() {
        if (activity != null)
            usersSPadapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, usersSPlist);
        usersSP.setAdapter(usersSPadapter);
        usersSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0) {
                    if (!usersListAL.contains(usersSPlist.get(i))) {
                        usersPulllist.add(String.valueOf(i-1));
                        usersListAL.add(usersSPlist.get(i));
                        UpdateUsersListView();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void UpdateUsersListView() {
        if (activity != null)
            usersListLVadapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, usersListAL);
        usersListLV.setAdapter(usersListLVadapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        activity = null;
    }
}
