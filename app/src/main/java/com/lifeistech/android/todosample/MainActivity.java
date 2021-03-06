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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RealmMemoAdapter.OnCheckClickListener{

    DrawerLayout drawer;
    public Realm realm;
    public ListView listView;

    FrameLayout frameLayout;
    TextView explainText;
    TextView statusTextView;

    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // realmを開く
        realm = Realm.getDefaultInstance();

        listView = findViewById(R.id.listView);
        frameLayout = findViewById(R.id.frameLaytout);
        explainText = findViewById(R.id.explainText);

        statusTextView = findViewById(R.id.statusTextView);

        frameLayout.setVisibility(View.INVISIBLE);
        explainText.setVisibility(View.INVISIBLE);


        setMemoList();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setMemoList();

    }

    public void setMemoList(){

        RealmResults<RealmMemo> results = null;

        switch (mode){
            case 0:
                statusTextView.setText("All TO-DOs");
                results = realm.where(RealmMemo.class).findAll();
                break;
            case 1:
                statusTextView.setText("Completed TO-DOs");
                results = realm.where(RealmMemo.class)
                        .equalTo("isChecked",true)
                        .findAll();
                break;
            case 2:
                statusTextView.setText("Active TO-DOs");
                results = realm.where(RealmMemo.class)
                        .equalTo("isChecked",false)
                        .findAll();
                break;
            default:
                break;
        }

        //realmから読み取る

        List<RealmMemo> items = realm.copyFromRealm(results);

        if(items.isEmpty()){
            frameLayout.setVisibility(View.VISIBLE);
            explainText.setVisibility(View.VISIBLE);
            explainText.setText("You have no TO-DOs!");
            statusTextView.setVisibility(View.INVISIBLE);

        }else{
            frameLayout.setVisibility(View.INVISIBLE);
            explainText.setVisibility(View.INVISIBLE);
            statusTextView.setVisibility(View.VISIBLE);
        }



        RealmMemoAdapter adapter = new RealmMemoAdapter(this,R.layout.layout_task_memo,items);

        adapter.setOnCheckClickListener(this);

        listView.setAdapter(adapter);

    }


    public void create(View v){
        Intent intent = new Intent(this,CreateActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_tasks, menu);
        return true;
    }

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

//            case R.id.menu_clear:
//                Toast.makeText(this,"menu_clear",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu_refresh:
//                Toast.makeText(this,"menu_refresh",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu_filter:
//                Toast.makeText(this,"menu_filter",Toast.LENGTH_SHORT).show();
//                setPopup();
//                break;
//            default:
//                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void setPopup(){

        PopupMenu popup = new PopupMenu(this,findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
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
                return true;
            }
        });
    }

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

    @Override
    public void onCheckClick() {
        setMemoList();
    }
}
