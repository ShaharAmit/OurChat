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
import java.util.Collections;
import java.util.Comparator;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.events.ReplaceMainFragmentEvent;
import shahar.shenkar.ourchat.objects.Model;


public class CalenderFragment extends Fragment {
    Model model = Model.getInstance();
    Activity activity;
    ArrayList<String> datesList;
    ArrayAdapter<String> adapter ;
    ListView listView;
    ValueEventListener ls;
    DatabaseReference ref;
    Button plus;


    public CalenderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View view =  inflater.inflate(R.layout.fragment_calendar, container, false);
        listView = (ListView)view.findViewById(R.id.datesList);
        datesList = new ArrayList<>();
        adapter= new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, datesList);
        plus = (Button)view.findViewById(R.id.addEvents);

        plus.setOnClickListener(click-> EventBus.getDefault().post(new ReplaceMainFragmentEvent("AddEvents")));

        ref = model.database.getReference().child("Users/"+model.userUid +"/calendar");
        ls = ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String date = "";
                    String event = "";
                    for (DataSnapshot dates : dataSnapshot.getChildren()) {
                        event = "";
                        date = dates.getKey();
                        for (DataSnapshot events : dates.getChildren()) {
                            for (DataSnapshot eventDetails : events.getChildren()) {
                                event += eventDetails.getValue() + "  ";
                            }
                            if (!date.equals("")) {
                                String date1 = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4, 8);
                                datesList.add(date1 + " - " + event);
                                model.safeMove = true;
                                updateList();
                                event="";
                            }
                        }

                    }

                    ref.removeEventListener(this);
                    ref=null;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        return view;
    }
    private void updateList(){
        if (model.safeMove) {
            Collections.sort(datesList,myComparator);
            adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, datesList);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        activity = null;
        if(ref!=null) {
            ref.removeEventListener(ls);
        }
    }

    Comparator myComparator = (o, t1) -> {
        String s1 = o.toString();
        String s2 = t1.toString();
        int year1 = Integer.parseInt(s1.substring(6,10));
        int month1 = Integer.parseInt(s1.substring(3,5));
        int day1 = Integer.parseInt(s1.substring(0,2));
        int year2 = Integer.parseInt(s2.substring(6,10));
        int month2 = Integer.parseInt(s2.substring(3,5));
        int day2 = Integer.parseInt(s2.substring(0,2));
        if(year1 > year2) {
            return 1;
        } else if (year1 == year2 && month1 > month2) {
            return 1;
        } else if(year1 == year2 && month1 == month2 && day1 >= day2) {
            return 1;
        }
        return -1;
    };
}
