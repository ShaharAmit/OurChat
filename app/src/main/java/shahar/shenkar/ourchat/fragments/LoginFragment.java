package shahar.shenkar.ourchat.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.events.LoginEvent;
import shahar.shenkar.ourchat.events.ReplaceMainFragmentEvent;
import shahar.shenkar.ourchat.objects.Model;
import shahar.shenkar.ourchat.objects.PbMethods;

public class LoginFragment extends Fragment {
    boolean clickedToSign = false;
    Button loginbt;
    Button signUpBt;
    EditText userMail;
    EditText userPass;
    EditText userName;
    EditText id;
    Button signU;
    Button signI;
    Activity activity;
    DatabaseReference institutionRef;
    DatabaseReference reference;
    ArrayList<Calendar> cal = new ArrayList<Calendar>();
    ArrayList<Calendar> localCal = new ArrayList<Calendar>();

    public static String uname;
    boolean signin = false;
    boolean signup = false;

    Model model = Model.getInstance();
    PbMethods pbMethods = PbMethods.getInstance();

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        activity = getActivity();
        loginbt = (Button)view.findViewById(R.id.loginBt);
        signUpBt = (Button)view.findViewById(R.id.signUpBt);
        userMail = (EditText)view.findViewById(R.id.userMail);
        userPass = (EditText)view.findViewById(R.id.userPass);
        userName = (EditText)view.findViewById(R.id.userName);
        id = (EditText)view.findViewById(R.id.ID);
        signU= (Button)view.findViewById(R.id.signU);
        signI = (Button)view.findViewById(R.id.signI);

        userMail.setVisibility(View.GONE);
        userPass.setVisibility(View.GONE);
        userName.setVisibility(View.GONE);
        id.setVisibility(View.GONE);
        signU.setVisibility(View.GONE);
        signI.setVisibility(View.GONE);

        InitListeners();
        return view;
    }

    private void InitListeners() {
        loginbt.setOnClickListener(v -> {
            if(signup){
                userName.setVisibility(View.GONE);
                id.setVisibility(View.GONE);
                signI.setVisibility(View.VISIBLE);
                signup = false;
            } else if(!signin) {
                signI.setVisibility(View.VISIBLE);
                userMail.setVisibility(View.VISIBLE);
                userPass.setVisibility(View.VISIBLE);
            }
            signin = true;
            signI.setOnClickListener(click -> {
                if(!clickedToSign) {
                    clickedToSign = true;
                    pbMethods.CloseKeyBoard(activity);
                    if (!userMail.getText().toString().isEmpty() && !userPass.getText().toString().isEmpty()) {
                        model.mAuth.signInWithEmailAndPassword(userMail.getText().toString(), userPass.getText().toString()).addOnCompleteListener(activity, task -> {
                            if (task.isSuccessful()) {
                                model.currentUser = model.mAuth.getCurrentUser();
                                getUserId();
                            } else {
                                Toast.makeText(activity, "Wrong sign in info",
                                        Toast.LENGTH_LONG).show();
                                clickedToSign = false;
                            }
                        });
                    } else {
                        Toast.makeText(activity, "Please fill all the forms",
                                Toast.LENGTH_LONG).show();
                        clickedToSign = false;
                    }
                }
            });
        });

        signUpBt.setOnClickListener(v -> {
            if (signin) {
                userName.setVisibility(View.VISIBLE);
                id.setVisibility(View.VISIBLE);
                signU.setVisibility(View.VISIBLE);
                signI.setVisibility(View.GONE);
                signin = false;
            } else if (!signup) {
                signU.setVisibility(View.VISIBLE);
                userMail.setVisibility(View.VISIBLE);
                userPass.setVisibility(View.VISIBLE);
                userName.setVisibility(View.VISIBLE);
                id.setVisibility(View.VISIBLE);
            }
            signup = true;

            signU.setOnClickListener(click -> {
                if(!clickedToSign) {
                    clickedToSign = true;
                    pbMethods.CloseKeyBoard(activity);
                    if (!userMail.getText().toString().isEmpty() && !userPass.getText().toString().isEmpty() && !userName.getText().toString().isEmpty() && !id.getText().toString().isEmpty()) {
                        model.mAuth.createUserWithEmailAndPassword(userMail.getText().toString(), userPass.getText().toString())
                                .addOnCompleteListener(activity, task -> {
                                    if (task.isSuccessful()) {
                                        uname = userName.getText().toString();

                                        model.currentUser = model.mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(uname)
                                                .build();

                                        if (model.currentUser != null) {
                                            model.currentUser.updateProfile(profileUpdates);
                                            try {
                                                model.userUid = model.currentUser.getUid();
                                                createUser(model.userUid, userName.getText().toString(), SHA1(id.getText().toString()));
                                                model.name = userName.getText().toString();
                                            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    } else {
                                        Toast.makeText(activity, "User already exist",
                                                Toast.LENGTH_LONG).show();
                                        clickedToSign = false;
                                    }
                                });

                    } else {
                        Toast.makeText(activity, "Please fill all the forms",
                                Toast.LENGTH_LONG).show();
                        clickedToSign = false;
                    }
                }
            });
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        model.currentUser = model.mAuth.getCurrentUser();
        if (model.currentUser != null){
            getUserId();
        }
    }

    private void getUserId() {
        reference = model.database.getReference();
        model.userUid = model.currentUser.getUid();
        model.database.getReference().child("Users/"+model.userUid+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model.name = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference.child("Users/" + model.userUid + "/id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model.id = dataSnapshot.getValue().toString();
                AddUserDetails();
                reference.removeEventListener(this);
                reference = null;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
             }
        });

    }

    private void AddUserDetails() {
        DatabaseReference usersRef = model.database.getReference().child("Users/"+model.userUid);
        institutionRef = model.database.getReference().child("InstitutionData/users/"+model.id);
        institutionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if(child.getKey().equals("courses")){
                        for (DataSnapshot courseChild : child.getChildren()) {
                            usersRef.child(child.getKey()+"/"+courseChild.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<String> a = new ArrayList<String>();
                                    if(dataSnapshot.getValue()!=null)
                                        for (DataSnapshot chat : dataSnapshot.getChildren()) {
                                            a.add(chat.getValue().toString());
                                        }
                                    else {
                                        a.add("Main");
                                    }
                                    usersRef.child(child.getKey()+"/"+courseChild.getValue().toString()).setValue(a);
                                    AddUserToChat(a , courseChild.getValue().toString());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } else if(child.getKey().equals("institution")) {
                        usersRef.child(child.getKey()).setValue(child.getValue());
                        model.institution=child.getValue().toString();
                    } else if(child.getKey().equals("signed")) {
                        usersRef.child(child.getKey()).setValue(child.getValue());
                    }
                }
                searchCal(institutionRef.child("calendar"),"cal");
                searchCal(usersRef.child("calendar"),"localCal");
                institutionRef.removeEventListener(this);
                institutionRef = null;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error ", databaseError.getDetails());
            }
        });
    }

    private void AddUserToChat(ArrayList<String> courses, String course) {
        for (int i=0; i<courses.size(); i++) {
            model.database.getReference().child("Institution/" + model.institution + "/" + course + "/groups/" +courses.get(i)+"/users/"+model.userUid+"/name").setValue(model.name);
        }
    }

    private void compareCal(DatabaseReference ref) {
        int t,l,r,m=0;
        for (int i=0; i<cal.size(); i++) {
            t=0;l=0;
            for (int j=0; j<localCal.size(); j++) {
                if(!(localCal.get(j).date.equals(cal.get(i).date)))
                    t++;
                else {
                    l=t;
                }
            }
            for(int k=0; k<cal.get(i).getParams().size(); k++) {
                String course = cal.get(i).getParams().get(k).course,
                        name=cal.get(i).getParams().get(k).name,
                        time=cal.get(i).getParams().get(k).time;
                r=0;
                if(t==localCal.size()) {
                    localCal.add(new Calendar(cal.get(i).date));
                    m=t;
                } else {
                    m=l;
                }
                for (int z=0; z<localCal.get(m).getParams().size(); z++) {
                    if(!course.equals(localCal.get(m).getParams().get(z).course)||
                            !name.equals(localCal.get(m).getParams().get(z).name)||
                            !time.equals(localCal.get(m).getParams().get(z).time)) {
                        r++;
                    }
                }
                if(r==localCal.get(m).getParams().size())
                    localCal.get(m).getParams().add(new CalendarParams(course, name, time));
            }

        }
        for (int i=0; i<localCal.size(); i++) {
            for (int k=0; k<localCal.get(i).getParams().size(); k++) {
                ref.child(localCal.get(i).date + "/" + k).setValue(localCal.get(i).getParams().get(k));
            }
        }
        EventBus.getDefault().post(new LoginEvent(true));
    }

    private void searchCal(DatabaseReference ref,String calen) {
        ArrayList<Calendar> t = new ArrayList<Calendar>();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                String course="",name="",time="";
                for (DataSnapshot date : dataSnapshot.getChildren()) {
                    if(!date.getKey().equals("")) {
                        t.add(new Calendar(date.getKey()));
                        for (DataSnapshot events : date.getChildren()) {
                            for (DataSnapshot params : events.getChildren()) {
                                if (params.getKey().equals("course"))
                                    course = params.getValue().toString();
                                if (params.getKey().equals("name"))
                                    name = params.getValue().toString();
                                if (params.getKey().equals("time"))
                                    time = params.getValue().toString();
                            }
                            try {
                                if(!course.equals("")&&!name.equals("")&&!time.equals(""))
                                    t.get(i).getParams().add(new CalendarParams(course, name, time));
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    i++;
                }
                if(calen.equals("cal")){
                    cal = t;
                } else {
                    localCal = t;
                    compareCal(ref);
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return asHex(sha1hash);
    }
    public static String asHex(byte buf[])
    {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);

        for(int i=0; i< buf.length; i++)
        {
            if(((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }
    @Subscribe
    public void onEvent(LoginEvent event) {
        if(event.getBool()) {
            model.currentUser = model.mAuth.getCurrentUser();
            if (model.currentUser!=null) {
                model.userUid = model.currentUser.getUid();
                EventBus.getDefault().post(new ReplaceMainFragmentEvent("Chats"));
            }
        } else {
            Toast.makeText(activity, "ID already in use",
                    Toast.LENGTH_LONG).show();
            clickedToSign=false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        activity = null;
    }

    public void createUser(String userID, String username, String id){
        User u = new User(username,id);
        reference  = model.database.getReference();
        institutionRef = reference.child("InstitutionData/users/"+id);
        DatabaseReference usersRef = reference.child("Users/"+userID);
        model.id = id;
        institutionRef.child("signed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.getValue().equals(false)) {
                    institutionRef.child("signed").setValue(true);
                    usersRef.setValue(u);
                    AddUserDetails();
                } else {
                    model.currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(model.currentUser!=null) {
                        model.currentUser.delete().addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                EventBus.getDefault().post(new LoginEvent(false));
                            }
                        });
                    }
                }
                institutionRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @IgnoreExtraProperties
    private class User {

        public String name;
        public String id;

        User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        User(String name, String id) {
            this.id = id;
            this.name = name;
        }
    }
    @IgnoreExtraProperties
    private class Calendar {

        public String date="";
        public ArrayList<CalendarParams> params = new ArrayList<CalendarParams>();

        Calendar() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        Calendar(String date) {
            this.date = date;
        }

        public ArrayList<CalendarParams> getParams() {
            return this.params;
        }
    }
    @IgnoreExtraProperties
    private class CalendarParams {

        public String course;
        public String name;
        public String time;

        CalendarParams() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        CalendarParams(String course, String name, String time) {
            this.course = course;
            this.name = name;
            this.time = time;
        }
    }
}
