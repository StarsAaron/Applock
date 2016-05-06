package com.rujian.applock.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.rujian.applock.bean.QuestionBean;
import com.rujian.applock.bean.UserBean;
import com.rujian.applock.db.UserDBOpenHelper;
import com.rujian.applock.util.Md5Util;

/**
 * Created by stars on 2015/7/26.
 */
public class UserDao  {
    private Context context;
    private UserDBOpenHelper userDBOpenHelper = null;

    public UserDao(Context context) {
        this.context = context;
        userDBOpenHelper = new UserDBOpenHelper(context,"user.db",null,1);
    }

    public int addUser(UserBean userBean){
        SQLiteDatabase db = userDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username",userBean.username);
        values.put("password", Md5Util.md5Arithmetic(userBean.password));
        values.put("question",userBean.question);
        values.put("answer", Md5Util.md5Arithmetic(userBean.answer));
        long i = db.insert("user", null, values);
        db.close();
        if(i!=-1){
            Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
            return 1;
        }else{
            Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    public boolean checkUser(String name,String password){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", null, "username=? AND password=?", new String[]{name, password}, null, null, null);
        if(cursor.moveToNext()){
            cursor.close();
            db.close();
            return true;
        }else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public boolean anyoneExit(){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        if(cursor.moveToNext()){
            cursor.close();
            db.close();
            return true;
        }else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public String getPassword(){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"password"}, null, null, null, null, null);
        if(cursor.moveToNext()){
            String password = cursor.getString(0);
            cursor.close();
            db.close();
            return password;
        }else{
            cursor.close();
            db.close();
            return null;
        }
    }

    public String getUserName(){
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"username"}, null, null, null, null, null);
        if(cursor.moveToNext()){
            String username = cursor.getString(0);
            cursor.close();
            db.close();
            return username;
        }else{
            cursor.close();
            db.close();
            return null;
        }
    }

    /**
     * 删除所有记录
     */
    public boolean delete(){
        SQLiteDatabase db = userDBOpenHelper.getWritableDatabase();
        if(db.delete("user", "1", null)!=-1){
            db.close();
            return true;
        }else{
            db.close();
            return false;
        }

    }

    public QuestionBean getQuestion(){
        QuestionBean questionBean = new QuestionBean();
        SQLiteDatabase db = userDBOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("user", new String[]{"question", "answer"}, null, null, null, null, null);
        if(cursor.moveToNext()){
            questionBean.question = cursor.getString(0);
            questionBean.answer = cursor.getString(1);
            cursor.close();
            db.close();
            return questionBean;
        }else{
            cursor.close();
            db.close();
            return null;
        }
    }

    /**
     * 修改密码
     * @return
     */
    public boolean chgUserPass(String password){
        SQLiteDatabase db = userDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", Md5Util.md5Arithmetic(password));
        int i = db.update("user", values, null, null);
        if(i!=-1){
            Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 修改密保
     * @return
     */
    public boolean chgUserQues(String question,String answer){
        SQLiteDatabase db = userDBOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question);
        values.put("answer", Md5Util.md5Arithmetic(answer));
        int i = db.update("user", values, null, null);
        if(i!=-1){
            Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
