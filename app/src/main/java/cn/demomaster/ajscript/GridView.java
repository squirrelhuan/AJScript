package cn.demomaster.ajscript;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.demomaster.qdlogger_library.QDLogger;

public class GridView extends FrameLayout {
    int row = 3;
    int colunms = 3;

    public void setRow(int row) {
        this.row = row;
    }

    public void setColunms(int colunms) {
        this.colunms = colunms;
    }

    public GridView(@NonNull Context context) {
        super(context);
        init();
    }

    public GridView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GridView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    private void init() {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        final Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);

                int child_h = viewPosionConfigMap.get(child).h * height / colunms;
                int child_w = viewPosionConfigMap.get(child).w * width / row;

                final int childWidthMeasureSpec;
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        child_w, MeasureSpec.EXACTLY);

                final int childHeightMeasureSpec;
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        child_h, MeasureSpec.EXACTLY);
                //QDLogger.i(i+",width="+width+",height="+height+",h="+child_h+",w="+child_w);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // super.onLayout(changed, left, top, right, bottom);
        layoutChildren(left, top, right, bottom, false /* no force left gravity */);
    }

    void layoutChildren(int left, int top, int right, int bottom, boolean forceLeftGravity) {
        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft = 0;
                int childTop = 0;

                PosionConfig posionConfig = viewPosionConfigMap.get(child);
                if (posionConfig != null) {
                    childLeft = posionConfig.x * getWidth() / row;
                    childTop = posionConfig.y * getHeight() / colunms;
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
        }
    }

    Map<View, PosionConfig> viewPosionConfigMap = new LinkedHashMap<>();

    public void addView(View child, PosionConfig posionConfig) {
        viewPosionConfigMap.put(child, posionConfig);
        super.addView(child);
    }

    public static class PosionConfig {

        public PosionConfig() {

        }

        int x;
        int y;
        int w;
        int h;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }
    }
}
