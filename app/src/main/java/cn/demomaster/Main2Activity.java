package cn.demomaster;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import cn.demomaster.huan.quickdeveloplibrary.base.activity.QDActivity;
import cn.demomaster.huan.quickdeveloplibrary.base.tool.actionbar.ACTIONBAR_TYPE;
import cn.demomaster.qdlogger_library.QDLogger;
import cn.demomaster.quickjs_library.fragment.AppletsFragment;

public class Main2Activity extends QDActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_main2);
        findViewById(R.id.container_content).setTag("content");
        String scriptStr = QDFileUtil.getFromAssets(mContext, "page/main.js");
        AjsEngine.getInstance().executeJ2V8(mContext, scriptStr);
*/

        View view = new FrameLayout(this);
        view.setId(View.generateViewId());
        view.setTag("content");
        setContentView(view);
        getActionBarTool().setActionBarType(ACTIONBAR_TYPE.NO_ACTION_BAR_NO_STATUS);
        QDLogger.i("AppletsFragmentActivity onCreate id="+view.getId()+",FragmentHelper="+getFragmentHelper().hashCode());
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