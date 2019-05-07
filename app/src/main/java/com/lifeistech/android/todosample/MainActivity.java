package com.lifeistech.android.todosample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

import io.realm.Realm;
import io.realm.RealmResults;

// 一覧を取得する
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RealmMemoAdapter.OnCheckClickListener{

    // implements以下の NavigationView.OnNavigationItemSelectedListenerは
    // ドロワーを使用するために使う

    // RealmMemoAdapter.OnCheckClickListenerは
    // onCheckClick()メソッド内のsetMemoList()をAdapter内で実行するために，こちらで実装している

    // ドロワーの用意
    DrawerLayout drawer;


    public Realm realm;
    public ListView listView;

    FrameLayout frameLayout; // Memoが全く無いときに画像だすためのLayout 無視してOK
    TextView explainText;
    TextView statusTextView;

    private int mode = 0; // フィルター機能の際に使う変数

    // Adapter内でインタフェースとして用意していたメソッド
    @Override
    public void onCheckClick() {
        setMemoList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // [Start]ドロワーなどの用意
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // [END]

        // realmを開く
        realm = Realm.getDefaultInstance();

        // 関連付け
        listView = findViewById(R.id.listView);
        frameLayout = findViewById(R.id.frameLaytout);
        explainText = findViewById(R.id.explainText);

        statusTextView = findViewById(R.id.statusTextView);

        // frameLayoutやexplainTextは一旦見えなくしている
        frameLayout.setVisibility(View.INVISIBLE);
        explainText.setVisibility(View.INVISIBLE);

        // メモすべてをセットする
        setMemoList();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setMemoList();
    }

    public void setMemoList(){

        RealmResults<RealmMemo> results = null;

        // フィルター機能
        // mode変数によってresultsの中のメモ数が変化する
        switch (mode){
            case 0:
                statusTextView.setText("All TO-DOs");
                results = realm.where(RealmMemo.class).findAll();
                break;
            case 1:
                statusTextView.setText("Completed TO-DOs");
                results = realm.where(RealmMemo.class)
                        .equalTo("isChecked",true) // isChecked変数がtrueのもののみ取得
                        .findAll();
                break;
            case 2:
                statusTextView.setText("Active TO-DOs");
                results = realm.where(RealmMemo.class)
                        .equalTo("isChecked",false) // isChecked変数がfalseのもののみ取得
                        .findAll();
                break;
            default:
                break;
        }

        // realmから読み取る
        // 取得したresultsをListとして扱う
        List<RealmMemo> items = realm.copyFromRealm(results);

        // itemが空かどうかで
        // frameLayoutに画像を出すのかどうか判定
        changeLayout(items.isEmpty());

        // Adapterの用意
        // 第2引数でレイアウト
        // 第3引数でメモすべてをAdapterに渡す
        RealmMemoAdapter adapter = new RealmMemoAdapter(this,R.layout.layout_task_memo,items);

        // adapterで実装している
        // checkboxをクリックした際にsetMemoList()を呼び出すために必要な処理
        adapter.setOnCheckClickListener(this);

        // listViewにadapterをセット
        listView.setAdapter(adapter);

    }

    // item数によりレイアウトを変化させる
    public void changeLayout(boolean isEmpty){
        if(isEmpty){
            frameLayout.setVisibility(View.VISIBLE);
            explainText.setVisibility(View.VISIBLE);
            explainText.setText("You have no TO-DOs!");
            statusTextView.setVisibility(View.INVISIBLE);
        }else{
            frameLayout.setVisibility(View.INVISIBLE);
            explainText.setVisibility(View.INVISIBLE);
            statusTextView.setVisibility(View.VISIBLE);
        }
    }

    // FAB(Floating Action Buttonをクリックした際にメモを作成)
    public void create(View v){
        Intent intent = new Intent(this,CreateActivity.class);
        startActivity(intent);
    }

    // ドロワー関連で必要な処理
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 右上のメニューにフィルター機能を設定する
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_tasks, menu);
        return true;
    }

    // クリックしたフィルターによってmode変数を変更させる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.active:
                // TODO 未達成タスクの表示
                Toast.makeText(getApplicationContext(),"active",Toast.LENGTH_SHORT).show();
                // TextViewの変更
                mode = 2;
                setMemoList();
                break;
            case R.id.completed:
                // TODO 達成済タスクの表示
                Toast.makeText(getApplicationContext(),"completed",Toast.LENGTH_SHORT).show();
                mode = 1;
                setMemoList();
                break;
            case R.id.all:
                // TODO すべてのタスクの表示
                Toast.makeText(getApplicationContext(),"all",Toast.LENGTH_SHORT).show();
                mode = 0;
                setMemoList();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ドロワー関連の処理
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.list_navigation_menu_item:
                Toast.makeText(this,"list_navigation_menu_item",Toast.LENGTH_SHORT).show();
                break;
            case R.id.statistics_navigation_menu_item:
                Toast.makeText(this,"statistics_navigation_menu_item",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // realmを閉じる
        realm.close();
    }


}
