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
import com.rujian.applock.bean.QuestionBean;
import com.rujian.applock.db.dao.UserDao;
import com.rujian.applock.util.Md5Util;

/**
 * Created by stars on 2015/7/26.
 */
public class ForgetActivity extends Activity implements View.OnClickListener {
    private TextView tv_question, tv_forget_back;
    private EditText edt_answer;
    private Button btn_forget_Ok;
    private QuestionBean questionBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        registView();
        initView();
    }

    private void registView() {
        tv_question = (TextView) findViewById(R.id.tv_question);
        tv_forget_back = (TextView) findViewById(R.id.tv_forget_back);
        edt_answer = (EditText) findViewById(R.id.edt_answer);
        btn_forget_Ok = (Button)findViewById(R.id.btn_forget_Ok);
    }

    private void initView() {
        btn_forget_Ok.setOnClickListener(this);
        tv_forget_back.setOnClickListener(this);
        UserDao userDao = new UserDao(ForgetActivity.this);
        questionBean = userDao.getQuestion();
        if(questionBean != null){
            tv_question.setText(questionBean.question);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_forget_Ok:
                check();
                break;
            case R.id.tv_forget_back:
                Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void check() {
        String answer = edt_answer.getText().toString().trim();
        if(TextUtils.isEmpty(answer)){
            Toast.makeText(ForgetActivity.this,"请输入答案",Toast.LENGTH_SHORT).show();
        }else{
            String md5Answer = Md5Util.md5Arithmetic(answer);
            if(md5Answer.equals(questionBean.answer)){
                Toast.makeText(ForgetActivity.this,"申请修改密码成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgetActivity.this,RegistActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(ForgetActivity.this,"答案错误",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
