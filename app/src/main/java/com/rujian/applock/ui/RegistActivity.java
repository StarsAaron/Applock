package com.rujian.applock.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rujian.applock.R;
import com.rujian.applock.db.dao.UserDao;
import com.rujian.applock.bean.UserBean;

/**
 * Created by stars on 2015/7/26.
 */
public class RegistActivity extends Activity {
    private EditText edt_username, edt_password, edt_password2, edt_question, edt_answer;
    private TextView tv_back;
    private Button btn_regist;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        userBean = new UserBean();
        registView();
        initView();
    }

    private void registView() {
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_password2 = (EditText) findViewById(R.id.edt_password2);
        edt_question = (EditText) findViewById(R.id.edt_question);
        edt_answer = (EditText) findViewById(R.id.edt_answer);
        btn_regist = (Button)findViewById(R.id.btn_regist);
        tv_back = (TextView)findViewById(R.id.tv_back);
    }

    private void initView() {
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInput()){
                    UserDao userDao = new UserDao(RegistActivity.this);
                    if(userDao.delete()){
                        System.out.println("------------------删除成功");
                        if(userDao.addUser(userBean)!= 0){
                            Toast.makeText(RegistActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(RegistActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegistActivity.this,"保存出错",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean checkInput(){
        userBean.username = edt_username.getText().toString();
        userBean.password = edt_password.getText().toString().trim();
        userBean.password2 = edt_password2.getText().toString().trim();
        userBean.question = edt_question.getText().toString();
        userBean.answer = edt_answer.getText().toString().trim();

        if(TextUtils.isEmpty(userBean.username)){
            Toast.makeText(RegistActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(userBean.password)){
            Toast.makeText(RegistActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(userBean.password2)){
            Toast.makeText(RegistActivity.this,"请再次输入密码",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(userBean.question)){
            Toast.makeText(RegistActivity.this,"密保问题不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(userBean.answer)){
            Toast.makeText(RegistActivity.this,"密保答案不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!userBean.password.equals(userBean.password2)){
            Toast.makeText(RegistActivity.this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
}
