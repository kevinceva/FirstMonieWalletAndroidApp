package com.ceva.ubmobile.models;

import android.view.View;

/**
 * Created by brian on 30/06/2017.
 */

public class ShowCaseModel {
    private View view;
    private String content;
    private String title;

    public ShowCaseModel(View view, String content) {
        this.view = view;
        this.content = content;
    }

    public ShowCaseModel(View view, String content, String title) {
        this.view = view;
        this.content = content;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
