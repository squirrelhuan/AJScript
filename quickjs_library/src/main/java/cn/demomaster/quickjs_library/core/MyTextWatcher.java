package cn.demomaster.quickjs_library.core;

import android.text.TextWatcher;
import android.view.View;

public abstract class MyTextWatcher implements TextWatcher {

    public View mView;

    public MyTextWatcher(View mView) {
        this.mView = mView;
    }
}
