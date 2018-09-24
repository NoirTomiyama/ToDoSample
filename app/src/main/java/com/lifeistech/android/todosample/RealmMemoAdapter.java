package com.lifeistech.android.todosample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;

public class RealmMemoAdapter extends ArrayAdapter<RealmMemo> {

//    private LayoutInflater layoutinflater;
    List<RealmMemo> mMemos;

    boolean isCheck = false;

    public static class ViewHolder{

        public LinearLayout linearLayout;
        public TextView titleTextView;
        public CheckBox checkBox;

        public ViewHolder(View view){
            linearLayout = view.findViewById(R.id.linearLayout);
            titleTextView = view.findViewById(R.id.titleText);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }

    public RealmMemoAdapter(@NonNull Context context, int resource, @NonNull List<RealmMemo> objects) {
        super(context, resource, objects);

//        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMemos = objects;
    }

    @Nullable
    @Override
    public RealmMemo getItem(int position) {
        return mMemos.get(position);
    }

    @Override
    public int getCount() {
        return mMemos.size();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_task_memo,null);
//            convertView = layoutinflater.inflate(R.layout.layout_task_memo, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final RealmMemo memo = getItem(position);

        if(memo != null) {
            viewHolder.titleTextView.setText(memo.title);
            viewHolder.checkBox.setChecked(memo.isChecked);

            if(memo.isChecked){
                viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#4D000000"));
            }else{
                viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#00000000"));
            }

            Log.d("memoTitle", memo.title);

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(getContext(),"onClick",Toast.LENGTH_SHORT).show();
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#4D000000"));
                    // Adapter内でのIntent処理
                    Intent intent = new Intent(view.getContext(),MemoActivity.class);

                    // チェックボックスの状態を渡す
                    intent.putExtra("isChecked",memo.isChecked);
                    intent.putExtra("updateDate",memo.updateDate);
                    view.getContext().startActivity(intent);
                }
            });

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean checked = ((CheckBox) view).isChecked();
                    if (checked) {
//                        Toast.makeText(getContext(),"checked:true",Toast.LENGTH_SHORT).show();
                        viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#4D000000"));
                        Snackbar.make(parent, "Task marked complete", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        isCheck = true;

                        // 保存できるか
                        Realm realm = Realm.getDefaultInstance();

                        final RealmMemo realmMemo = realm.where(RealmMemo.class).equalTo("updateDate",
                                memo.updateDate).findFirst();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmMemo.isChecked = true;
                            }
                        });

                        Log.d("checkOn","came");
                        realm.close();

                    } else {
//                        Toast.makeText(getContext(),"checked:false",Toast.LENGTH_SHORT).show();
                        viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#00000000"));
                        Snackbar.make(parent, "Task marked active", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();

                        isCheck = false;

                        Realm realm = Realm.getDefaultInstance();

                        final RealmMemo realmMemo = realm.where(RealmMemo.class).equalTo("updateDate",
                                memo.updateDate).findFirst();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmMemo.isChecked = false;
                            }
                        });

                        Log.d("checkOn","came");
                        realm.close();


                    }
                }
            });

        }


        return convertView;
    }


}
