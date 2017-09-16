package shahar.shenkar.ourchat.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.objects.Model;
import shahar.shenkar.ourchat.objects.PbMethods;

public class AddEventsFragment extends Fragment {
    Button add;
    DatePicker date;
    EditText course;
    EditText name;
    EditText time;

    String evDate;
    String evTime;
    String evName;
    String evCourse;

    Activity activity;
    Model model = Model.getInstance();
    PbMethods pbMethods = PbMethods.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_events, container, false);
        add = (Button)view.findViewById(R.id.addEvents);
        date = (DatePicker) view.findViewById(R.id.date);
        course = (EditText)view.findViewById(R.id.course);
        name = (EditText)view.findViewById(R.id.name);
        time = (EditText)view.findViewById(R.id.time);

        add.setOnClickListener(click->{
            pbMethods.CloseKeyBoard(activity);
            if(checkInputs()) {
                DatabaseReference ref = model.database.getReference().child("Users/"+model.userUid+"/calendar/"+evDate);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long i = dataSnapshot.getChildrenCount();
                        Event e = new Event(evTime,evCourse,evName);
                        ref.child(Long.toString(i)).setValue(e);
                        Toast.makeText(activity, "Event added",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else {
                Toast.makeText(activity, "illegal input (check hint)",
                        Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private boolean checkInputs() {
        if(!this.course.getText().toString().isEmpty() && !this.name.getText().toString().isEmpty() && !this.time.getText().toString().isEmpty()) {
            evTime = this.time.getText().toString();

            try {
                if (Integer.parseInt(evTime.substring(0, 2))>23 || Integer.parseInt(evTime.substring(0, 2))<0 || Integer.parseInt(evTime.substring(3, 5))>59 || Integer.parseInt(evTime.substring(3, 5))<0)
                    return false;
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                return false;
            }
            int iDay = this.date.getDayOfMonth();
            int iMonth = this.date.getMonth()+1;
            int iYear = this.date.getYear();

            String sDay;
            String sMonth;

            if (iDay < 10)
                sDay = "0" + Integer.toString(iDay);
            else
                sDay = Integer.toString(iDay);

            if (iMonth < 10)
                sMonth = "0" + Integer.toString(iMonth);
            else
                sMonth = Integer.toString(iMonth);

            evDate = sDay + sMonth + Integer.toString(iYear);
            evName = this.name.getText().toString();
            evCourse = this.course.getText().toString();;
            evTime = this.time.getText().toString();


            return true;
        }
        return false;
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

    @IgnoreExtraProperties
    private class Event {

        public String time;
        public String course;
        public String name;

        Event() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        Event(String time, String course, String name) {
            this.time = time;
            this.name = name;
            this.course = course;
        }
    }
}
