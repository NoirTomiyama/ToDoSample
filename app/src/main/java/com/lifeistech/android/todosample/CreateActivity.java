package com.lifeistech.android.todosample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class CreateActivity extends AppCompatActivity {

    public Realm realm;

    public EditText titleEditText;
    public EditText contentEditText;

//    SharedPreferences pref;
//    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        realm = Realm.getDefaultInstance();

//        pref = getSharedPreferences("tasks",MODE_PRIVATE);

    }

//    データをRealmに保存する
    public void save(final String title,final String updateDate,final String content){

        //メモを保存する
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmMemo memo = realm.createObject(RealmMemo.class);
                memo.title = title;
                memo.updateDate = updateDate;
                memo.content = content;
                memo.isChecked = false;
            }
        });
    }

    //データをSharedPreferenceに保存する
//    public void save(final String title,final String updateDate,final String content){
//
//        // メモを保存する
//
//        dateSet.add(updateDate);
//
//        editor = pref.edit();
//
//        editor.putString(updateDate,title);
//        editor.putString(updateDate + "_" + title,content);
//        editor.putBoolean(updateDate + "_" + content,false);
//        editor.commit();
//
//    }


    // EditText に入力されたデータを元にMemoを作る
    public void create(View v){

        // タイトルを取得する
        String title = titleEditText.getText().toString();

        // 日付を取得
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE);
        String updateDate = simpleDateFormat.format(date);

        //内容を取得
        String content = contentEditText.getText().toString();

        //出力してみる
//        check(title,updateDate,content);

        // 保存する
        save(title,updateDate,content);

        // 画面を終了する
        finish();

    }

    private void check(String title, String updateDate, String content) {

        //Memoクラスのインスタンスを生成する
        RealmMemo memo = new RealmMemo();

        //それぞれの要素を代入する
        memo.title = title;
        memo.updateDate = updateDate;
        memo.content = content;

        //ログに出してみる
        Log.d("memo.title",memo.title);
        Log.d("memo.updateDate",memo.updateDate);
        Log.d("memo.content",memo.content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }
}
