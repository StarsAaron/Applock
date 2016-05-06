package com.rujian.applock.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rujian.applock.R;
import com.rujian.applock.db.dao.UserDao;
import com.rujian.applock.util.Md5Util;


/**
 * Created by stars on 2015/7/26.
 */
public class EnterPwdActivity extends Activity implements View.OnClickListener {
    private EditText edt_enter_password;
    private Button btn_OK;
    private TextView tv_appname,tv_concel;
    private ImageView iv_appicon;
    private String packname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        registView();
        initView();
    }

    private void registView() {
        edt_enter_password = (EditText) findViewById(R.id.edt_enter_password);
        tv_appname = (TextView) findViewById(R.id.tv_appname);
        tv_concel = (TextView) findViewById(R.id.tv_concel);
        iv_appicon = (ImageView) findViewById(R.id.iv_appicon);
        btn_OK = (Button) findViewById(R.id.btn_OK);
    }

    private void initView() {
        Intent intent = getIntent();
        packname = intent.getStringExtra("packname");
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
            iv_appicon.setImageDrawable(applicationInfo.loadIcon(pm));
            tv_appname.setText(applicationInfo.loadLabel(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        btn_OK.setOnClickListener(this);
        tv_concel.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * 输入校验
     */
    public void enter(){
        String password = edt_enter_password.getText().toString().trim();
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        UserDao userDao = new UserDao(EnterPwdActivity.this);
        String passWord = userDao.getPassword();
        if(passWord.equals(Md5Util.md5Arithmetic(password))){
            //密码输入正确。
            //如果密码输入正确 告诉看门狗（后台的一个服务） 你不要在保护这个应用程序了 。 这个哥们密码输入正确。
            Intent intent = new Intent();
            intent.setAction("com.rujian.applock.stopprotect");
            intent.putExtra("packname", packname);
            sendBroadcast(intent);//发送一个自定义的广播
            Toast.makeText(this, "解锁成功", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_OK:
                enter();
                break;
            case R.id.tv_concel:
                close();
                break;
        }
    }

    /**
     * 关闭当前页面并且结束锁上的App进程
     */
    private void close(){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.MONKEY");
        intent.addCategory("android.intent.category.DEFAULT");
        startActivity(intent);
        //杀死packname对应的进程
        //所有的activity最小化 不会执行ondestory 只执行onstop方法
    }
}
