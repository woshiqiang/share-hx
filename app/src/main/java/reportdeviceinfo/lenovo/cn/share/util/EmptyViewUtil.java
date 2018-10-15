package reportdeviceinfo.lenovo.cn.share.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * @Date 2018-10-15.
 */
public class EmptyViewUtil {
    /**
     * 设置ListView的EmptyView
     * setEmptyView
     *
     * @param listview
     * @param emptyView <p>网上对Api解释的非常清楚，将EmptyView添加到最外层的ViewGroup上。</p>
     * @author Administrator
     */
    public static void setEmptyView(ListView listview, View emptyView) {
        FrameLayout emptyLayout;
        if (listview.getEmptyView() == null) {
            emptyLayout = new FrameLayout(listview.getContext());
            emptyLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            emptyView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            emptyLayout.addView(emptyView);
            emptyView.setVisibility(View.VISIBLE);
            getParentView((ViewGroup) listview.getParent()).addView(emptyLayout);
            listview.setEmptyView(emptyLayout);
        } else {
            emptyLayout = (FrameLayout) listview.getEmptyView();
            emptyLayout.removeAllViews();
            emptyLayout.setVisibility(View.VISIBLE);
            emptyLayout.addView(emptyView);
        }
    }

    private static ViewGroup getParentView(ViewGroup parent) {
        ViewGroup tempVg = parent;
        if (parent.getParent() != null && parent.getParent() instanceof ViewGroup) {
            tempVg = (ViewGroup) parent.getParent();
            getParentView(tempVg);
        } else {
            return tempVg;
        }
        return tempVg;
    }
}
