package shahar.shenkar.ourchat;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import shahar.shenkar.ourchat.Utils.AsyncHandler;
import shahar.shenkar.ourchat.Utils.UiHandler;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Widget extends AppWidgetProvider {
    private static final String PREF_PREFIX_KEY = "appwidget_";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        final String[] title = {""};
        AsyncHandler.post(() -> {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            title[0] = prefs.getString(PREF_PREFIX_KEY + appWidgetId, "example");
            if (!title[0].isEmpty()) {
                UiHandler.post(() -> {
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                    views.setTextViewText(R.id.appwidget_text, title[0]);
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                });
            }
        });
    }

    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("widget-onUpdate");
        // There may be multiple widgets active, so update all of them
        for (final int appWidgetId : appWidgetIds) {
//          updateAppWidget(context, appWidgetManager, appWidgetId);
            final RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);

            final String[] widgetText = new String[1];
            AsyncHandler.post(() -> {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                widgetText[0] = prefs.getString(PREF_PREFIX_KEY + appWidgetId, "example");
            });
            FirebaseDatabase.getInstance().getReference().child("Institution/Shenkar/Java/groups/Main/messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String temp = "";
                    for (DataSnapshot message: dataSnapshot.getChildren()){
                        temp = message.getValue().toString();
                    }
                    views.setTextViewText(R.id.appwidget_text, widgetText[0] +": "+temp);
                    appWidgetManager.updateAppWidget(appWidgetId, views);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            AsyncHandler.post(() -> {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(PREF_PREFIX_KEY + appWidgetId);
                editor.apply();
            });
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }
}

