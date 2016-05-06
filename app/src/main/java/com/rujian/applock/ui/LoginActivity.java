package com.rujian.applock.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rujian.applock.R;
import com.rujian.applock.db.dao.UserDao;
import com.rujian.applock.util.Md5Util;


public class LoginActivity extends Activity implements View.OnClickListener {
    private Button btn_login;
    private EditText edt_login_password;
    private TextView tv_login_username,tv_login_forget;
    private UserDao userDao = null;
    private String name;//用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//不随屏幕旋转

        registView();
        initView();
    }

    private void registView() {
        edt_login_password = (EditText) findViewById(R.id.edt_login_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_login_forget = (TextView) findViewById(R.id.tv_login_forget);
        tv_login_username = (TextView) findViewById(R.id.tv_login_username);
    }

    private void initView() {
        tv_login_forget.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        userDao = new UserDao(LoginActivity.this);
        if (userDao.anyoneExit()) {//检查是否已经存在账户，如果存在直接显示用户名
            name = userDao.getUserName();
            tv_login_username.setText("用户名：" + name);
        }else{
            Intent intent2 = new Intent(LoginActivity.this, RegistActivity.class);
            startActivity(intent2);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String pass = edt_login_password.getText().toString().trim();
                if (checkInput(pass)) {
                    if (userDao.checkUser(name, Md5Util.md5Arithmetic(pass))) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, AppLockActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.tv_login_forget:
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private boolean checkInput(String password) {
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
