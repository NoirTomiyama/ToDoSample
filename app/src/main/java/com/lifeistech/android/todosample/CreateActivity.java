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


// メモ作成のActivity
public class CreateActivity extends AppCompatActivity {

    // Realmの準備
    public Realm realm;

    // EditTextの準備
    // アクセス修飾子は任意
    public EditText titleEditText;
    public EditText contentEditText;

    // Overrideアノテーション
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // 関連付け
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        // realmインスタンスの生成
        realm = Realm.getDefaultInstance();

    }

    // データをRealmに保存する
    public void save(final String title,final String updateDate,final String content){

        //メモを保存する
        // executeTransactionメソッド内で記述することにより保存可能
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

    // EditText に入力されたデータを元にメモを作成する
    public void create(View v){

        // タイトルを取得する
        String title = titleEditText.getText().toString();

        // 日付を取得
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPANESE);
        String updateDate = simpleDateFormat.format(date);

        //内容を取得
        String content = contentEditText.getText().toString();

        // 保存する
        save(title,updateDate,content);

        // 画面を終了する
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // realmを閉じる
        realm.close();
    }
}
