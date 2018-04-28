package com.lifeistech.android.todosample;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {

    public Realm realm;

    public EditText titleText;
    public EditText contentText;

    RealmMemo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //realmを開く
        realm = Realm.getDefaultInstance();

        titleText = findViewById(R.id.titleEditText);
        contentText = findViewById(R.id.contentEditText);

        showData();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);

                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public  void showData() {

         memo = realm.where(RealmMemo.class).equalTo("updateDate",
                getIntent().getStringExtra("updateDate")).findFirst();

        titleText.setText(memo.title);
        contentText.setText(memo.content);

    }

    public void update(View view){

        final RealmMemo memo = realm.where(RealmMemo.class).equalTo("updateDate",
                getIntent().getStringExtra("updateDate")).findFirst();

        // 更新する
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                memo.title = titleText.getText().toString();
                memo.content = contentText.getText().toString();
            }
        });

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
