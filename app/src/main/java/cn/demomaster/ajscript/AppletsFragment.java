package cn.demomaster.ajscript;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.demomaster.ajscript.engine.j2v8.AjsEngine;
import cn.demomaster.huan.quickdeveloplibrary.base.fragment.QDFragment;
import cn.demomaster.huan.quickdeveloplibrary.helper.toast.QdToast;
import cn.demomaster.huan.quickdeveloplibrary.util.QDFileUtil;
import cn.demomaster.huan.quickdeveloplibrary.widget.ImageTextView;
import cn.demomaster.huan.quickdeveloplibrary.widget.dialog.QDDialog;
import cn.demomaster.qdlogger_library.QDLogger;


/**
 * Squirrel桓
 * 2018/8/25
 */
public class AppletsFragment extends QDFragment {

    @Override
    public int getHeadlayoutResID() {
        return R.layout.qd_applet_actionbar_common;
    }

    @Nullable
    @Override
    public View onGenerateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_layout_applets, null);
       /* View view = new FrameLayout(getContext());
        AjsLayoutInflater.parseXmlAssetsForLayout(getContext(), "config/layout_test.xml", view);*/
        return view;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);

        ImageTextView imageTextView = ((ImageTextView) findViewById(R.id.it_actionbar_title));
        if (imageTextView != null) {
            imageTextView.setText(getTitle());
        }
    }
    AjsEngine ajsEngine;
    @Override
    public void initCreatView(View mView) {
        super.initCreatView(mView);

        Bundle bundle = getIntent().getExtras();
        String controller = bundle.getString("controller");
        String title = bundle.getString("title");
        setTitle(title);
        String scriptStr = QDFileUtil.getFromAssets(mContext, controller);
        ajsEngine = new AjsEngine(mContext);
        //QDLogger.e("文本内容========"+scriptStr);
        ajsEngine.startControll( this,mView.findViewById(R.id.container_content),scriptStr);

        ImageTextView btn_title = (ImageTextView) findViewById(R.id.it_actionbar_title);
        if (btn_title != null) {
            btn_title.setText(getTitle());
        }
        ImageTextView btn_back = (ImageTextView) findViewById(R.id.it_actionbar_back);
        if (btn_back != null) {
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickBack();
                }
            });
        }
        ImageTextView btn_close = (ImageTextView) findViewById(R.id.it_actionbar_close);
        if (btn_close != null) {
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
        ViewGroup ll_menu_right = (ViewGroup) findViewById(R.id.ll_menu_right);
        View view_splitor = (View) findViewById(R.id.view_splitor);
        if (isRootFragment) {
            if (btn_back != null) {
                btn_back.setVisibility(View.GONE);
            }
            if (btn_close != null) {
                btn_close.setVisibility(View.VISIBLE);
            }
            if (ll_menu_right != null) {
                ll_menu_right.setBackgroundResource(R.drawable.applet_menu_bg);
            }
            if (view_splitor != null) {
                view_splitor.setVisibility(View.VISIBLE);
            }
        } else {
            if (btn_close != null) {
                btn_close.setVisibility(View.GONE);
            }
            if (btn_back != null) {
                btn_back.setVisibility(View.VISIBLE);
            }
            if (ll_menu_right != null) {
                ll_menu_right.setBackgroundDrawable(null);
            }
            if (view_splitor != null) {
                view_splitor.setVisibility(View.GONE);
            }
        }
    }

    public void initView(View rootView) {
        int i = (int) (Math.random() * 10 % 4);
        setTitle(titles[i] + "sss");
        //getActionBarTool().setHeaderBackgroundColor(colors[i]);

        QDLogger.d("initView =" + getTitle() + "," + i + ",FragmentHelper=" + getFragmentHelper().hashCode());
    }

    private String[] titles = {"1", "2", "3", "4"};
    private int[] colors = {Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE};

    public void startFragment(Bundle bundle) {
        //getFragmentHelper().startFragment(new AppletsFragment(),bundle);
        getFragmentHelper().build(getContext(), AppletsFragment.class.getName()).putExtras(bundle)
                .putExtra("password", 666666).navigation();
    }

    //记录用户首次点击返回键的时间
    private long firstClickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isRootFragment) {
            if (System.currentTimeMillis() - firstClickTime > 2000) {
                QdToast.show(mContext, "再点击退出 activity");
                firstClickTime = System.currentTimeMillis();
            } else {
                getActivity().finish();
            }
            return true;
        }
        // QdToast.show(mContext, "onKeyDown isRootFragment="+isRootFragment());
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFragmentResume() {
        ajsEngine.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ajsEngine.onPaused();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ajsEngine.onDestory();
    }
}