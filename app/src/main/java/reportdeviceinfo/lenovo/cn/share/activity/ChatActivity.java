package reportdeviceinfo.lenovo.cn.share.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

import reportdeviceinfo.lenovo.cn.share.R;

/**
 * Created by DaQiang on 2018/10/14.
 */

public class ChatActivity extends AppCompatActivity {
    EaseChatFragment chatFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatFragment = new EaseChatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, getIntent().getIntExtra("userType", 0));
        bundle.putString(EaseConstant.EXTRA_USER_ID, getIntent().getStringExtra("userName"));
//        Log.e("username", getIntent().getStringExtra("userName"));
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_chat, chatFragment).show(chatFragment).commit();
        initChat();
    }
    /**
     * 设置聊天界面属性
     */
    private void initChat() {

    }

}
