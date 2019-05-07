package com.lifeistech.android.todosample;


// これも使っていない
public class Task {
    // 日付
    public String updateDate;
    // タイトル
    public String title;
    // 内容
    public String content;
    //チェックボックス(完了かどうか)
    public Boolean isChecked;


    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public Task(String updateDate, String title, String content, Boolean isChecked) {
        this.updateDate = updateDate;
        this.title = title;
        this.content = content;
        this.isChecked = isChecked;
    }
}
