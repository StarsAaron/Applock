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
 * 修改密保问题
 */
public class ChangeQuestionActivity extends Activity{
    private Button btn_chg_ok;
    private EditText edt_chg_question,edt_chg_answer;
    private TextView tv_chg_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chg_quet);

        registView();
        initView();
    }

    private void registView() {
        edt_chg_question = (EditText)findViewById(R.id.edt_chg_question);
        edt_chg_answer = (EditText)findViewById(R.id.edt_chg_answer);
        btn_chg_ok = (Button)findViewById(R.id.btn_chg_ok);
        tv_chg_back = (TextView)findViewById(R.id.tv_chg_back);
    }

    private void initView() {
        btn_chg_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = edt_chg_question.getText().toString().trim();
                String answer = edt_chg_answer.getText().toString().trim();
                if(checkInput(question,answer)){
                    UserDao userDao = new UserDao(getApplicationContext());
                    if(userDao.chgUserQues(question,answer)){
                        finish();
                    }
                }
            }
        });

        tv_chg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean checkInput(String password,String password2){
        if(TextUtils.isEmpty(password)){
            edt_chg_question.setError("问题不能为空");
            return false;
        }
        if(TextUtils.isEmpty(password2)){
            edt_chg_answer.setError("答案不能为空");
            return false;
        }
        return true;
    }
}
