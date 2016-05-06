package com.rujian.applock.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rujian.applock.R;
import com.rujian.applock.bean.AppInfoBean;
import com.rujian.applock.db.dao.AppLockDao;
import com.rujian.applock.engine.AppInfoProvider;
import com.rujian.applock.service.AppLockService;
import com.rujian.applock.util.DensityUtil;
import com.rujian.applock.util.ServiceStatusUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stars on 2015/7/26.
 */
public class AppLockActivity extends Activity implements View.OnClickListener {
    private TextView tv_unlock;
    private TextView tv_locked;
    private LinearLayout ll_unlock;
    private LinearLayout ll_locked;

    private ListView lv_unlock;
    private ListView lv_locked;

    private List<AppInfoBean> appInfos;

    private TextView tv_unlock_count;
    private TextView tv_locked_count;

    private AppLockDao dao;

    private List<AppInfoBean> unlockAppInfos;
    private List<AppInfoBean> lockedAppInfos;

    private AppLockAdapter unlockadapter;
    private AppLockAdapter lockedadapter;

    private Button bnt_lock;
    private ImageButton imgb_setting;
    //标题设置按钮弹窗
    private PopupWindow titlePopupWindow;
    //是否显示popupwindow
    private boolean isShowPopu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);

        registView();
        initView();
    }

    private void registView() {
        dao = new AppLockDao(this);
        tv_locked = (TextView) findViewById(R.id.tv_locked);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_locked = (ListView) findViewById(R.id.lv_locked);
        tv_unlock_count = (TextView) findViewById(R.id.tv_unlock_count);
        tv_locked_count = (TextView) findViewById(R.id.tv_locked_count);
        bnt_lock = (Button) findViewById(R.id.bnt_lock);
        imgb_setting = (ImageButton)findViewById(R.id.imgb_setting);
        imgb_setting.setOnClickListener(this);
        bnt_lock.setOnClickListener(this);
        tv_locked.setOnClickListener(this);
        tv_unlock.setOnClickListener(this);
    }

    private void initView() {
        isShowPopu = false;
        // 获取所有的应用程序信息的集合。//下面的逻辑最好放在子线程。
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
                for (AppInfoBean appinfo : appInfos) {
                    if (dao.find(appinfo.packname)) {
                        lockedAppInfos.add(appinfo);
                    } else {
                        unlockAppInfos.add(appinfo);
                    }
                }
                unlockadapter.notifyDataSetChanged();
                lockedadapter.notifyDataSetChanged();
            }
        },2000);

        // 过滤所有应用程序的集合 把未加锁的和已加锁的appinfo给区分出来。
        unlockAppInfos = new ArrayList<>();
        lockedAppInfos = new ArrayList<>();

        unlockadapter = new AppLockAdapter(true);
        lv_unlock.setAdapter(unlockadapter);
        lockedadapter = new AppLockAdapter(false);
        lv_locked.setAdapter(lockedadapter);
        //开关显示初始化
        if (ServiceStatusUtils.isServiceRunning(this, "com.rujian.applock.service.AppLockService")) {
            bnt_lock.setBackgroundResource(R.mipmap.ic_toggle_on);
        } else {
            bnt_lock.setBackgroundResource(R.mipmap.ic_toggle_off);
        }
    }

    /**
     * 锁适配器
     */
    private class AppLockAdapter extends BaseAdapter {
        private boolean unlockflag = true;//标识是否是加锁适配器

        public AppLockAdapter(boolean unlockflag) {
            this.unlockflag = unlockflag;
        }

        @Override
        public int getCount() {// 返回当前界面有多少个条目
            if (unlockflag) {
                tv_unlock_count.setText("未加锁软件:" + unlockAppInfos.size() + "个");
                return unlockAppInfos.size();
            } else {
                tv_locked_count.setText("已加锁软件:" + lockedAppInfos.size() + "个");
                return lockedAppInfos.size();
            }
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(),
                        R.layout.list_applock_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.iv_status = (ImageView) view
                        .findViewById(R.id.iv_status);
                view.setTag(holder);
            }
            final AppInfoBean appinfo;
            if (unlockflag) {
                holder.iv_status.setBackgroundResource(R.mipmap.ic_unlock);
                appinfo = unlockAppInfos.get(position);
            } else {
                holder.iv_status.setBackgroundResource(R.mipmap.ic_lock);
                appinfo = lockedAppInfos.get(position);
            }
            holder.iv_icon.setImageDrawable(appinfo.icon);
            holder.tv_name.setText(appinfo.name);
            holder.iv_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 从当前界面移除item
                    // 把这个条目的包名给存到数据库
                    if (unlockflag) {// 未加锁列表
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(500);
                        view.startAnimation(ta);
                        //让主线程在这等待500毫秒
                        //若干秒时间后 在主线程执行逻辑
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //会在主线程里面执行。
                                unlockAppInfos.remove(position);
                                dao.add(appinfo.packname);
                                lockedAppInfos.add(appinfo);
                                unlockadapter.notifyDataSetChanged();
                                lockedadapter.notifyDataSetChanged();
                            }
                        }, 500);


                    } else {
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, -1.0f,
                                Animation.RELATIVE_TO_SELF, 0,
                                Animation.RELATIVE_TO_SELF, 0);
                        ta.setDuration(500);
                        view.startAnimation(ta);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                lockedAppInfos.remove(position);
                                dao.delete(appinfo.packname);
                                unlockAppInfos.add(appinfo);
                                unlockadapter.notifyDataSetChanged();
                                lockedadapter.notifyDataSetChanged();
                            }
                        }, 500);

                    }
                }
            });
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    /**
     * 缓存用
     */
    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_status;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_locked: //加锁应用
                tv_locked.setBackgroundResource(R.mipmap.ic_tab_right_pressed);
                tv_unlock.setBackgroundResource(R.mipmap.ic_tab_left_default);
                ll_unlock.setVisibility(View.GONE);
                break;
            case R.id.tv_unlock://无加锁应用
                tv_locked.setBackgroundResource(R.mipmap.ic_tab_right_default);
                tv_unlock.setBackgroundResource(R.mipmap.ic_tab_left_pressed);
                ll_unlock.setVisibility(View.VISIBLE);
                break;
            case R.id.bnt_lock://锁服务总开关
                Intent appLockIntent = new Intent(this, AppLockService.class);
                if (ServiceStatusUtils.isServiceRunning(this, "com.rujian.applock.service.AppLockService")) {
                    bnt_lock.setBackgroundResource(R.mipmap.ic_toggle_off);
                    stopService(appLockIntent);//停止服务
                } else {
                    bnt_lock.setBackgroundResource(R.mipmap.ic_toggle_on);
                    startService(appLockIntent);//开启服务
                }
                break;
            case R.id.imgb_setting://弹窗
                if(titlePopupWindow != null && !titlePopupWindow.isShowing()){
                    //解决自动隐藏点击两次弹出窗口问题
                    isShowPopu = false;
                }
                if (!isShowPopu) {
                    isShowPopu = true;
                    //设置弹窗
                    View view = View.inflate(getApplicationContext(), R.layout.popwindow, null);
                    TextView tv_chg_pwd = (TextView)view.findViewById(R.id.tv_chg_pwd);
                    TextView tv_chg_question = (TextView)view.findViewById(R.id.tv_chg_question);
                    TextView tv_about = (TextView)view.findViewById(R.id.tv_about);
                    tv_chg_pwd.setOnClickListener(this);
                    tv_chg_question.setOnClickListener(this);
                    tv_about.setOnClickListener(this);
                    //重要：创建PopupWindow
                    titlePopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    //重要注意：必须要设置背景
                    titlePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //根据手机手机的分辨率 把60dip 转化成 不同的值 px
                    int px = DensityUtil.dip2px(getApplicationContext(), 72);
                    //获取点击的按钮位置
                    int[] location = new int[2];
                    v.getLocationInWindow(location);
                    //设置弹出窗口位置
                    titlePopupWindow.showAtLocation(v, Gravity.TOP + Gravity.LEFT, location[0], px);
                    //设置弹窗动画
                    AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                    aa.setDuration(200);

                    ScaleAnimation sa = new ScaleAnimation(1.0f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0);
                    sa.setDuration(200);
                    AnimationSet set = new AnimationSet(false);
                    set.addAnimation(sa);
                    set.addAnimation(aa);
                    view.startAnimation(set);
                } else {
                    titlePopupWindow.dismiss();
                    isShowPopu = false;
                }
                break;
            case R.id.tv_chg_pwd:
                if(isShowPopu){
                    titlePopupWindow.dismiss();
                    isShowPopu = false;
                }
                Intent intent = new Intent(AppLockActivity.this,ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_chg_question:
                if(isShowPopu){
                    titlePopupWindow.dismiss();
                    isShowPopu = false;
                }
                Intent intent2 = new Intent(AppLockActivity.this,ChangeQuestionActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_about:
                if(isShowPopu){
                    titlePopupWindow.dismiss();
                    isShowPopu = false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AppLockActivity.this);
                builder.setMessage("作者：Aaron \n版本：V1.0 \n日期：2015.7").setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                        .setTitle("关于")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
        }
    }

    int pressBackNum = 0;

    @Override
    public void onBackPressed() {
        pressBackNum++;
        if (1 == pressBackNum) {
            Toast.makeText(AppLockActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //两秒后归零
                    pressBackNum = 0;
                }
            }, 2000);
        } else if (2 == pressBackNum) {
            finish();
        }

    }
}
