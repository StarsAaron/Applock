package com.rujian.applock.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.rujian.applock.db.dao.AppLockDao;
import com.rujian.applock.ui.EnterPwdActivity;

/**
 * 不停的监视当前手机系统程序的运行状态
 */
public class AppLockService extends Service {
    public static final String TAG = "AppLockService";
    private ActivityManager am;
    private boolean flag; //循环标识
    private AppLockDao dao; //数据库相关Dao

    private String tempStopProtectpackname;//临时停止保护的包名
    private InnerReceiver receiver; //广播接收者
    private List<String> protectedPacknames;//被保护的包名集合
    private MyObserver observer; //内容观察者

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 内部广播接收者，用于接收暂停保护的应用包名
     */
    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectpackname = intent.getStringExtra("packname");
            Log.i(TAG, "临时的停止对某个应用程序的保护:" + tempStopProtectpackname);
        }
    }


    @Override
    public void onCreate() {
        //代码注册广播接收者
        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.rujian.applock.stopprotect");
        registerReceiver(receiver, filter);
        //获取全部受保护的包名
        dao = new AppLockDao(this);
        protectedPacknames = dao.findAll();
        //注册一个内容观察者
        Uri uri = Uri.parse("content://com.rujian.applock/applockdb");
        observer = new MyObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, observer);

        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        final Intent intent = new Intent(this, EnterPwdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //EnterPwdActivity对象共享
        new Thread() {
            public void run() {
                flag = true;
                while (flag) {
                    //巡逻 监视当前运行的应用程序  得到当前任务栈集合最前的任务栈信息  当前要开启的程序
                    RunningTaskInfo taskInfo = am.getRunningTasks(1).get(0);
                    String packname = taskInfo.topActivity.getPackageName();
                    if (protectedPacknames.contains(packname)) {//查询内存
                        //检查是否需要临时停止保护
                        if (!packname.equals(tempStopProtectpackname)) {
                            //不需要临时停止保护前应用程序需要保护， 关门放狗
                            intent.putExtra("packname", packname);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(100);//控制循环的频率
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        super.onCreate();
    }

    /**
     * 内容观察者
     */
    private class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.i(TAG, "啊啊啊啊，我发现了数据库的内容变化了。");
            protectedPacknames = dao.findAll();//数据变化，重新获取被保护的包名
        }

    }

    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        super.onDestroy();
    }

}
