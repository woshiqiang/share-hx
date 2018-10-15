package reportdeviceinfo.lenovo.cn.share.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.fragment.ConversationListFragment;
import reportdeviceinfo.lenovo.cn.share.fragment.HomeFragment;
import reportdeviceinfo.lenovo.cn.share.fragment.MeFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment[] fragments;
    private RadioGroup rg_main;
    private int index;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView() {
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        index = 0;
                        break;
                    case R.id.rb_msg:
                        index = 1;
                        break;
                    case R.id.rb_add:
                        startActivity(new Intent(MainActivity.this, PublishActivity.class));
                        if (currentIndex==0){
                            rg_main.check(R.id.rb_home);
                        }else {
                            rg_main.check(R.id.rb_me);
                        }
                        return;
                    case R.id.rb_me:
                        index = 2;
                        break;
                }

                showFragment(index);
            }
        });
        ConversationListFragment conversationListFragment = new ConversationListFragment();
        fragments = new Fragment[]{new HomeFragment(),conversationListFragment, new MeFragment()};
        getSupportFragmentManager().beginTransaction().add(R.id.fl_main, fragments[0]).add(R.id.fl_main, fragments[1]).hide(fragments[1]).show(fragments[0]).commit();

    }

    /**
     * 切换fragment
     *
     * @param index：0-2
     */
    public void showFragment(int index) {
        if (currentIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fl_main, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }

        currentIndex = index;
    }
}
