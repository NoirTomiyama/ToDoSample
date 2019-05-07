package com.lifeistech.android.todosample;

import android.app.Application;

import io.realm.Realm;

public class MemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Realmを使うための準備
        Realm.init(getApplicationContext());
    }

}
