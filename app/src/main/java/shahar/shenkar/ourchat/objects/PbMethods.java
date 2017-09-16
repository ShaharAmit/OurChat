package shahar.shenkar.ourchat.objects;


import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class PbMethods {
    private static PbMethods instance = null;

    public static PbMethods getInstance() {
        if(instance == null) {
            instance = new PbMethods();
        }
        return instance;
    }
    public void CloseKeyBoard(Activity activity) {
        if (activity!=null && activity.getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
            activity = null;
        }
    }
}
