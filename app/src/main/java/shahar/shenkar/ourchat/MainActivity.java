package shahar.shenkar.ourchat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import shahar.shenkar.ourchat.events.changeTitle;
import shahar.shenkar.ourchat.events.ReplaceMainFragmentEvent;
import shahar.shenkar.ourchat.fragments.AddChatFragment;
import shahar.shenkar.ourchat.fragments.AddEventsFragment;
import shahar.shenkar.ourchat.fragments.CalenderFragment;
import shahar.shenkar.ourchat.fragments.ChatsListFragment;
import shahar.shenkar.ourchat.fragments.ChatFragment;
import shahar.shenkar.ourchat.fragments.MenuFragment;
import shahar.shenkar.ourchat.fragments.StorageFragment;
import shahar.shenkar.ourchat.fragments.LoginFragment;
import shahar.shenkar.ourchat.objects.Model;

public class MainActivity extends AppCompatActivity {
    Model model = Model.getInstance();
    Button mChat;
    Button mCalendar;
    Button mFolder;
    Button mMenu;
    Button mBack;
    Toolbar tb1;
    Toolbar tb2;
    TextView title;
    FragmentManager manager;
    LoginFragment loginFragment;
    ChatsListFragment coursesFragment;
    ChatFragment conversationFragment;
    CalenderFragment calenderFragment;
    StorageFragment storageFragment;
    MenuFragment menuFragment;
    AddChatFragment addChatsFragment;
    AddEventsFragment addEventsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
        }
        LeakCanary.install(getApplication());

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        title = (TextView)findViewById(R.id.titleText) ;
        mChat = (Button)findViewById(R.id.bchats);
        mCalendar = (Button)findViewById(R.id.bclander);
        mFolder = (Button)findViewById(R.id.bfolder);
        mMenu = (Button)findViewById(R.id.bmenu);
        mBack = (Button)findViewById(R.id.bback);
        tb1 = (Toolbar)findViewById(R.id.toolbar2);
        tb2 = (Toolbar)findViewById(R.id.toolbar3);

        ToggleMenu(false);

        loginFragment = new LoginFragment();
        coursesFragment = new ChatsListFragment();
        conversationFragment = new ChatFragment();
        calenderFragment= new CalenderFragment();
        storageFragment = new StorageFragment();
        menuFragment = new MenuFragment();
        addChatsFragment = new AddChatFragment();
        addEventsFragment = new AddEventsFragment();

        manager = getFragmentManager();

        manager.beginTransaction()
                .replace(R.id.mainLayout,loginFragment,loginFragment.getTag())
                .commit();

        setListenrs();



    }
    public void setListenrs(){
        mChat.setOnClickListener(v -> EventBus.getDefault().post(new ReplaceMainFragmentEvent("Chats")));
        mCalendar.setOnClickListener(v -> EventBus.getDefault().post(new ReplaceMainFragmentEvent("Calendar")));
        mFolder.setOnClickListener(v -> EventBus.getDefault().post(new ReplaceMainFragmentEvent("Storage")));
        mMenu.setOnClickListener(v -> EventBus.getDefault().post(new ReplaceMainFragmentEvent("Options")));
        mBack.setOnClickListener(v->{
            model.isChatting=false;
            EventBus.getDefault().post(new ReplaceMainFragmentEvent("Chats"));
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        manager = null;
    }

    @Subscribe
    public void changeFragement(ReplaceMainFragmentEvent event) {
        Fragment frag ;
        initTabColor();
        switch (event.getMessage()){
            case "Chat":
                mBack.setVisibility(View.VISIBLE);
                frag = conversationFragment;
                title.setText(model.currentCourse);
                mChat.setBackgroundResource(R.drawable.chat2);

                break;
            case "Calendar":
                frag = calenderFragment;
                title.setText("Calendar");
                mCalendar.setBackgroundResource(R.drawable.calendar2);

                break;

            case "Storage":
                frag = storageFragment;
                title.setText(model.currentCourse);
                mFolder.setBackgroundResource(R.drawable.folder2);
                break;

            case "Options":
                frag = menuFragment;
                title.setText("Options");
                mMenu.setBackgroundResource(R.drawable.menu2);
                break;
            case "Chats":

                ToggleMenu(true);
                frag = coursesFragment;
                title.setText("Chat List");
                mChat.setBackgroundResource(R.drawable.chat2);
                break;

            case "AddChats":
                frag = addChatsFragment;
                title.setText("adding chat");
                break;


            case "AddEvents":
                frag = addEventsFragment;
                title.setText("adding calendar events");
                break;


            default:
                ToggleMenu(false);
                frag = loginFragment;
        }
        manager.beginTransaction()
                .replace(R.id.mainLayout,frag,frag.getTag())
                .commit();
    }

    @Subscribe
    public void openChat(changeTitle event) {
        model.currentCourse = event.getMessage();
        title.setText(event.getMessage());
    }
    public void initTabColor(){
        mChat.setBackgroundResource(R.drawable.chat1);
        mCalendar.setBackgroundResource(R.drawable.calendar1);
        mFolder.setBackgroundResource(R.drawable.folder1);
        mMenu.setBackgroundResource(R.drawable.menu1);
        mBack.setVisibility(View.GONE);
    }
    public void ToggleMenu(boolean set){
        if(!set) {
            mCalendar.setVisibility(View.GONE);
            mChat.setVisibility(View.GONE);
            mFolder.setVisibility(View.GONE);
            mMenu.setVisibility(View.GONE);
            tb1.setVisibility(View.GONE);
            tb2.setVisibility(View.GONE);
        } else {
            mCalendar.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            mFolder.setVisibility(View.VISIBLE);
            mMenu.setVisibility(View.VISIBLE);
            tb1.setVisibility(View.VISIBLE);
            tb2.setVisibility(View.VISIBLE);
        }
    }

}
