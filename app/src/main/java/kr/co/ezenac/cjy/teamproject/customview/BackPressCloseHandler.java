package kr.co.ezenac.cjy.teamproject.customview;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by Administrator on 2018-02-28.
 */

public class BackPressCloseHandler {
    private long backKeypressedTime = 0;
    private Toast toast;

    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    public void onBackPressed(){
        if (System.currentTimeMillis() > backKeypressedTime + 2000){
            backKeypressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeypressedTime + 2000){
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide(){
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
