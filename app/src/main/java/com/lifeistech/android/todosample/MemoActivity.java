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

// MainActivityから，項目１つ分について飛ぶ
public class MemoActivity extends AppCompatActivity {

    public Realm realm;

    public TextView titleTextView;
    public TextView contentTextView;
    public CheckBox checkBox;

    RealmMemo memo;

    static int requestCode = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        //realmを開く
        realm = Realm.getDefaultInstance();

        titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);
        checkBox = findViewById(R.id.checkBox);


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

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                            memo.isChecked = checked;
                    }
                });
            }
        });

        showData();
    }

    public  void showData() {

        if(memo==null){
            memo = realm.where(RealmMemo.class).equalTo("updateDate",
                    getIntent().getStringExtra("updateDate")).findFirst();
            
        }



        // チェックボックスの更新
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
//                memo.isChecked = getIntent().getBooleanExtra("isChecked",false);
            }
        });

        titleTextView.setText(memo.title);
        contentTextView.setText(memo.content);
        checkBox.setChecked(memo.isChecked);

    }

    public void update(View v){

        Intent intent = new Intent(MemoActivity.this,DetailActivity.class);
        intent.putExtra("updateDate", memo.updateDate);
//        startActivity(intent);
        startActivityForResult(intent,requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 1001){
            if(resultCode == Activity.RESULT_OK){

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskdetail_menu, menu);
        return true;
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // realmを閉じる
        realm.close();
    }
}
