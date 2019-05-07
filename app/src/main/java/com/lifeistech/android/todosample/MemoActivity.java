package com.lifeistech.android.todosample;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

// MainActivityから，項目ひとつぶんに飛ぶ
public class MemoActivity extends AppCompatActivity {

    // Realmの用意
    public Realm realm;

    // 項目ひとつ分なので、
    // 以下3つを用意
    public TextView titleTextView;
    public TextView contentTextView;
    public CheckBox checkBox;

    // ひとつ分のRealmMemo型のmemo変数を用意
    RealmMemo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        //realmを開く
        realm = Realm.getDefaultInstance();

        // 関連付け
        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        checkBox = findViewById(R.id.checkBox);

        memo = realm
                .where(RealmMemo.class)
                .equalTo("updateDate", getIntent().getStringExtra("updateDate"))
                .findFirst();

        // チェックボックスクリック処理
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean checked = ((CheckBox) view).isChecked();

                if(checked){
                    Snackbar.make(view, "Task marked complete", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Task marked active", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

                // チェックボックスの状態を更新
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                            memo.isChecked = checked;
                    }
                });

                showData();
            }
        });

        // 一覧として表示
        showData();
    }

    // メモの表示メソッド
    public void showData() {

        titleTextView.setText(memo.title);
        contentTextView.setText(memo.content);
        checkBox.setChecked(memo.isChecked);

    }

    // メモ更新画面に飛ぶ
    public void update(View v) {
        Intent intent = new Intent(MemoActivity.this, DetailActivity.class);
        intent.putExtra("updateDate", memo.updateDate);
        startActivityForResult(intent, 1); // DetailActivityから戻ってきた際に，一覧に飛ぶためのテクニック①(覚えなくていい)
    }

    // レイアウトの右上にゴミ箱アイコンを設置
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskdetail_menu, menu);
        return true;
    }

    // ゴミ箱アイコンをクリックした際にメモを削除できるように
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_delete:
                Snackbar.make(findViewById(R.id.relativeLayout), "menu_delete", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

                // 削除する
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        memo.deleteFromRealm();
                        finish();
                    }
                });

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // DetailActivityから戻ってきた際に，一覧に飛ぶためのテクニック②(覚えなくていい)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult","came");


        if(resultCode == RESULT_OK){
            finish();
            Log.d("test","test");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // realmを閉じる
        realm.close();
    }
}
