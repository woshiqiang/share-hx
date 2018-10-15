package reportdeviceinfo.lenovo.cn.share.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import reportdeviceinfo.lenovo.cn.share.fragment.NewestFragment;


public class HomeQuestionPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"最新", "最热", "最近"};
    private String[] orders = new String[]{"createdAt","scanNumber","updatedAt"};
    private Context context;

    public HomeQuestionPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return  NewestFragment.newInstance( orders[position]);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}