package shahar.shenkar.ourchat;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import shahar.shenkar.ourchat.Utils.AsyncHandler;
import shahar.shenkar.ourchat.objects.Model;

public class WidgetConfigureActivity extends Activity {
    Model model = Model.getInstance();
    ArrayList<String> courses;
    public ArrayAdapter<String> coursesAdapter;

    DatabaseReference reference;
    ListView widgetCoursesList;
    Button btadd ;
    String title;



    private static final String PREFS_NAME = "shahar.shenkar.ourchat.AppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    TextView mAppWidgetText;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Widget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public WidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public String loadTitlePref(int appWidgetId) {
        AsyncHandler.post(() -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            title = prefs.getString(PREF_PREFIX_KEY + appWidgetId, "example");
        });

        return title;
    }

    public void deleteTitlePref(int appWidgetId) {
        AsyncHandler.post(() -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(PREF_PREFIX_KEY + appWidgetId);
            editor.apply();
        });

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        courses = new ArrayList<String>();

        setResult(RESULT_CANCELED);
        reference = model.database.getReference().child("Users/"+model.userUid+"/courses");

        setContentView(R.layout.widget_configure);

        mAppWidgetText = (TextView) findViewById(R.id.appwidget_text);
        widgetCoursesList = (ListView)findViewById(R.id.listWidget);
        btadd = (Button)findViewById(R.id.add_button);

        btadd.setOnClickListener(view -> {
            final Context context = WidgetConfigureActivity.this;

            AsyncHandler.post(() -> {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREF_PREFIX_KEY + mAppWidgetId, mAppWidgetText.getText().toString());
                editor.apply();
            });

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Widget.updateAppWidget(context, appWidgetManager, mAppWidgetId);



            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        });


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot course : dataSnapshot.getChildren()){
                    courses.add(course.getKey());
                }
                update();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        widgetCoursesList.setOnItemClickListener((parent, view, position, id) -> mAppWidgetText.setText(coursesAdapter.getItem(position)));




        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(mAppWidgetId));
    }

    private void update(){
        coursesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, courses);
        widgetCoursesList.setAdapter(coursesAdapter);
    }

}
