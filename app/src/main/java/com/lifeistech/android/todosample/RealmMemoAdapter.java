package com.lifeistech.android.todosample;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import io.realm.Realm;

public class RealmMemoAdapter extends ArrayAdapter<RealmMemo> {

    private List<RealmMemo> mMemos;

    // interfaceとしてOnCheckListenerを用意
    // ここは理解できなくてもいいです。金曜日に説明します

    // ここからは一旦コピー

    private OnCheckClickListener onCheckClickListener = null;

    interface OnCheckClickListener {
        void onCheckClick();
    }

    void setOnCheckClickListener(OnCheckClickListener onCheckClickListener) {
        this.onCheckClickListener = onCheckClickListener;
    }

    // ここまで

    // ViewHolderパターン
    public static class ViewHolder{

        LinearLayout linearLayout;
        TextView titleTextView;
        CheckBox checkBox;

        ViewHolder(View view){
            linearLayout = view.findViewById(R.id.linearLayout);
            titleTextView = view.findViewById(R.id.titleText);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }

    // Adapterのコンストラクタ．
    // MainActivityで宣言時に第3引数にメモの一覧をセットしている
    RealmMemoAdapter(@NonNull Context context, int resource, @NonNull List<RealmMemo> objects) {
        super(context, resource, objects);

        mMemos = objects;
    }


    // 教科書どおり
    @Nullable
    @Override
    public RealmMemo getItem(int position) {
        return mMemos.get(position);
    }

    // 教科書どおり
    @Override
    public int getCount() {
        return mMemos.size();
    }

    // 教科書どおり
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_task_memo,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // メモ一つ分に対して、処理を記述する
        final RealmMemo memo = getItem(position);

        if(memo != null) {
            viewHolder.titleTextView.setText(memo.title);
            viewHolder.checkBox.setChecked(memo.isChecked);

            // メモの確認
            Log.d("memoTitle", memo.title);

            // メモひとつ分のLinearLayoutをクリックしたときの記述
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Adapter内でのIntent処理
                    Intent intent = new Intent(view.getContext(),MemoActivity.class);
                    // intentに"updateDate"を鍵として，MemoActivityに遷移する
                    intent.putExtra("updateDate",memo.updateDate);
                    view.getContext().startActivity(intent);
                }
            });

            // Checkboxをクリックした時の処理
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // チェックボックスの状態の取得
                    final boolean checked = ((CheckBox) view).isChecked();

                    if (checked == true) {
                        Snackbar.make(parent, "Task marked complete", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();

                        // realmの用意
                        Realm realm = Realm.getDefaultInstance();

                        // memo変数は，要素ひとつ分のメモであり，Realm管理下にないので、
                        // realmのクエリを使って管理下にあるrealmMemo変数を用意する
                        // 詳しくはこちら(https://qiita.com/rissy/items/ce5a5f9504fae4b74c70)
                        final RealmMemo realmMemo = realm
                                .where(RealmMemo.class)
                                .equalTo("updateDate", memo.updateDate)
                                .findFirst();

                        // チェックボックスの更新
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if(realmMemo!=null) realmMemo.isChecked = true;
                            }
                        });

                        Log.d("checkbox","true");

                        // チェックボックスをクリックした際に
                        // MainActivity内のsetMemo()を呼び出すための記述
                        onCheckClickListener.onCheckClick();

                        // realmを閉じる
                        realm.close();

                    } else {
                        // チェックボックスがfalseなら
                        Snackbar.make(parent, "Task marked active", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();

                        // realmの用意
                        Realm realm = Realm.getDefaultInstance();

                        // memo変数は，要素ひとつ分のメモであり，Realm管理下にないので、
                        // realmのクエリを使って管理下にあるrealmMemo変数を用意する
                        final RealmMemo realmMemo = realm
                                .where(RealmMemo.class)
                                .equalTo("updateDate", memo.updateDate).findFirst();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if(realmMemo!=null) realmMemo.isChecked = false;
                            }
                        });

                        Log.d("checkbox","false");

                        // チェックボックスをクリックした際に
                        // MainActivity内のsetMemo()を呼び出すための記述
                        onCheckClickListener.onCheckClick();

                        // realmを閉じる
                        realm.close();

                    }
                }
            });

        }

        return convertView;
    }


}
