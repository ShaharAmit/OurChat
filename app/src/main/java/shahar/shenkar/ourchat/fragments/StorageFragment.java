package shahar.shenkar.ourchat.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import shahar.shenkar.ourchat.R;
import shahar.shenkar.ourchat.events.FileEvent;
import shahar.shenkar.ourchat.objects.Model;


public class StorageFragment extends Fragment {
    Model model = Model.getInstance();
    Activity activity;
    ListView storageContainer;
    DatabaseReference dref;
    StorageReference sref;
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;

    public StorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        // Inflate the layout for this fragment
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        storageContainer = (ListView) view.findViewById(R.id.storageContainer);

        items = new ArrayList<String>();

        dref = model.database.getReference().child("Institution/" + model.institution +"/" + model.courseMain + "/groups/" + model.courseGroup + "/storage");

        sref = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ourchat-373c0.appspot.com/" + model.institution + "/Courses/" + model.courseMain + model.courseGroup);

        CallStorage();

        return view;
    }

    private void updateList() {
        itemsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, items);
        storageContainer.setAdapter(itemsAdapter);

        storageContainer.setOnItemLongClickListener((parent, view, position, id) -> {

            sref.child(items.get(position));
            final File file = new File(getActivity().getExternalCacheDir(), items.get(position));
            if (!file.exists()) {
                System.out.println("Downloading file: " + file.getName());
                EventBus.getDefault().post(new FileEvent(items.get(position),"download",file));
            }
            else {
                EventBus.getDefault().post(new FileEvent(items.get(position),"open",file));
            }

            return true;
        });
    }

    public void CallStorage() {
        items.clear();
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    items.add(ds.getValue().toString());
                }
                updateList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }






    @Subscribe
    public void fileAction(final FileEvent event) {
        if (event.getAction().equals("download")) {
            sref.getFile(event.getEventFile()).addOnCompleteListener(task -> {
                System.out.println("Download complete: " + event.getEventFile().getName());
                EventBus.getDefault().post(new FileEvent(event.getFileFullName(),"open",event.getEventFile()));
            });
        }
        else {
            Intent target = new Intent(Intent.ACTION_VIEW);
            String type = "";
            String word = event.getFileFullName().substring(event.getFileFullName().indexOf(".") + 1);
            switch (word) {
                case "docx":
                case "doc":
                    type = "application/msword";
                    break;
                case "pdf":
                    type = "application/pdf";
                    break;
                case "xls":
                    type = "application/vnd.ms-excel";
                    break;
                case "ppt":
                    type = "application/vnd.ms-powerpoint";
                    break;
                case "mp4":
                    type = "video/mp4";
                    break;
                case "jpeg":
                case "jpg":
                    type = "image/jpeg";
                    break;
                case "png":
                    type = "image/png";
                    break;
                case "mp3":
                    type = "audio/mpeg3";
                    break;
            }

            target.setDataAndType(Uri.fromFile(event.getEventFile()), type);

            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        activity = null;
    }
}