package com.lifeistech.android.todosample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import io.realm.Realm;


// メモの更新画面のActivity
public class DetailActivity extends AppCompatActivity {

    // realmの用意
    public Realm realm;

    // EditTextの用意
    public EditText titleText;
    public EditText contentText;

    // RealmMemo(Realmに保存するクラス名)
    // 更新画面なので，メモ一つに対してアクションを行う
    RealmMemo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //realmを開く
        realm = Realm.getDefaultInstance();

        // 関連付け
        titleText = findViewById(R.id.titleEditText);
        contentText = findViewById(R.id.contentEditText);

        // 更新画面にメモを表示する
        showData();

    }

    public  void showData() {

        // 画面遷移したあとに，画面にメモ情報を表示する
        // Realm型のmemo変数に代入する
        // 保存されている，RealmMemoクラスのプロパティ"updateDate"と遷移前にIntentに詰めた"updateDate"と比較
        // 1つ目をmemo変数に代入
        memo = realm.where(RealmMemo.class).equalTo("updateDate",
                getIntent().getStringExtra("updateDate")).findFirst();

        // 取り出すだけでは表示されないので
        // 各EditTextに表示
        titleText.setText(memo.title);
        contentText.setText(memo.content);
    }

    // メモ更新メソッド
    public void update(View view){

        // 保存されている，RealmMemoクラスのプロパティ"updateDate"と遷移前にIntentに詰めた"updateDate"と比較
        // 1つ目をmemo変数に代入
        final RealmMemo memo = realm.where(RealmMemo.class).equalTo("updateDate",
                getIntent().getStringExtra("updateDate")).findFirst();

        // 更新する
        // executeTransactionメソッド内で更新
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memo.title = titleText.getText().toString();
                memo.content = contentText.getText().toString();
            }
        });

        // 一つ戻った際に一覧まで戻るテクニック
        // 今は理解しなくて良い
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        // 画面を閉じる
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // realmを閉じる
        realm.close();
    }

}
