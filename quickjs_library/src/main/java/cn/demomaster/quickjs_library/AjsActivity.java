package cn.demomaster.quickjs_library;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import cn.demomaster.huan.quickdeveloplibrary.base.activity.QDActivity;
import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.quickjs_library.fragment.AppletsFragment;

import static cn.demomaster.qdrouter_library.actionbar.ACTIONBAR_TYPE.NO_ACTION_BAR_NO_STATUS;

public class AjsActivity extends QDActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new FrameLayout(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setId(View.generateViewId());
        }
        view.setTag("content");
        setContentView(view);
        getActionBarTool().setActionBarType(NO_ACTION_BAR_NO_STATUS);
        QDLogger.i("id="+view.getId()+",FragmentHelper="+getFragmentHelper().hashCode());
        /*AppletsFragment f1 = new AppletsFragment();
        startFragment(this,f1,getContentViewId());*/
        // startFragment(new AppletsFragment(),getContentViewId());

        Bundle bundle = new Bundle();
        String controller = "page/index.js";
        String title = "主页";
        bundle.putString("controller",controller);
        bundle.putString("title",title);
        getFragmentHelper().build(this, AppletsFragment.class.getName()).setContainerViewId(view.getId()).putExtras(bundle)
                .putExtra("password", 123).navigation();
    }
}