package bcg.mymusicplayer;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 * 点击退出应用的时候 执行该类
 */
public class ExitSysApplication extends Application {
    private List<Activity> mList = new LinkedList<>();
    private static ExitSysApplication instance;

    private ExitSysApplication() {}

    public synchronized static ExitSysApplication getInstance() {
        if (null == instance) {
            instance = new ExitSysApplication();
        }
        return instance;
    }
    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
