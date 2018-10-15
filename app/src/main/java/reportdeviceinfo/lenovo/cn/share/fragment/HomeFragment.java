package reportdeviceinfo.lenovo.cn.share.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.activity.SearchActivity;
import reportdeviceinfo.lenovo.cn.share.adapter.HomeQuestionPagerAdapter;


/**
 * @author
 * @time 2018-10-12 10:04
 * @类描述：首页
 * @变更记录:
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private TabLayout homeTabLayout;
    private ViewPager homeViewPager;
    private EditText et_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        homeTabLayout = (TabLayout) view.findViewById(R.id.homeTabLayout);
        homeViewPager = (ViewPager) view.findViewById(R.id.homeViewPager);

        homeViewPager.setAdapter(new HomeQuestionPagerAdapter(getActivity().getSupportFragmentManager(), getActivity()));
        homeTabLayout.setupWithViewPager(homeViewPager);

        et_search = view.findViewById(R.id.et_search);
        et_search.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_search:
                startActivity(new Intent(getContext(),SearchActivity.class));
                break;
        }
    }
}
