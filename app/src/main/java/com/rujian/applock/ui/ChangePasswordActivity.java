package com.rujian.applock.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rujian.applock.R;
import com.rujian.applock.db.dao.UserDao;

/**
 * Created by stars on 2015/7/27.
 * 修改密码
 */
public class ChangePasswordActivity extends Activity{
    private Button btn_chgpass_ok;
    private EditText edt_chgpass_password,edt_chgpass_password2;
    private TextView tv_chgpass_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chg_pwd);

        registView();
        initView();
    }

    private void registView() {
        edt_chgpass_password = (EditText)findViewById(R.id.edt_chgpass_password);
        edt_chgpass_password2 = (EditText)findViewById(R.id.edt_chgpass_password2);
        btn_chgpass_ok = (Button)findViewById(R.id.btn_chgpass_ok);
        tv_chgpass_back = (TextView)findViewById(R.id.tv_chgpass_back);
    }

    private void initView() {
        btn_chgpass_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edt_chgpass_password.getText().toString().trim();
                String password2 = edt_chgpass_password2.getText().toString().trim();
                if(checkInput(password,password2)){
                    UserDao userDao = new UserDao(getApplicationContext());
                    if(userDao.chgUserPass(password)){
                        finish();
                    }
                }
            }
        });

        tv_chgpass_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean checkInput(String password,String password2){
        if(TextUtils.isEmpty(password)){
            edt_chgpass_password.setError("密码不能为空");
            return false;
        }
        if(TextUtils.isEmpty(password2)){
            edt_chgpass_password2.setError("请再输入一次密码");
            return false;
        }
        if(!password.equals(password2)){
            edt_chgpass_password2.setError("两次密码不一致");
            return false;
        }
        return true;
    }
}
