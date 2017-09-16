package shahar.shenkar.ourchat.objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Model {
    private static Model ourInstance = null;

    public static Model getInstance() {
        if(ourInstance == null) {
            ourInstance = new Model();
        }
        return ourInstance;
    }

    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference dbRef;
    public DatabaseReference currentCoursedbRef;
    public StorageReference strogRef = FirebaseStorage.getInstance().getReference();
    public String currentCourse;
    public int selectedCourseNum= 0;
    public boolean safeMove  = false;
    public String userUid;
    public String widgetCourse;
    public boolean isChatting = false;
    public FirebaseUser currentUser;
    public String institution;
    public String courseMain;
    public String courseGroup;
    public String id;
    public String name;
}
