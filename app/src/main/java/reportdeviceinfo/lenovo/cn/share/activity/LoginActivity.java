package reportdeviceinfo.lenovo.cn.share.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.util.DialogUtil;


/**
 * 登录界面
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_username;
    private EditText et_password;
    private Button btn_login;
    private Button btn_register;
    private TextView title;
    private Dialog dialog;
    private ImageButton ib_qq, ib_wechat;
    private UMShareAPI mShareAPI;
    private SHARE_MEDIA platform = null;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mShareAPI = UMShareAPI.get(this);
        initView();
        initData();
    }

    private void initData() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        title.setText("登录");
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        ib_qq = findViewById(R.id.ib_qq);
        ib_wechat = findViewById(R.id.ib_wechat);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        ib_wechat.setOnClickListener(this);
        ib_qq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                submit();
                break;
            case R.id.btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.ib_qq://QQ登录
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                break;
            case R.id.ib_wechat://微信登录
                platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(LoginActivity.this, platform, umAuthListener);
                break;
        }
    }

    private void submit() {
        String username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        login(username, password);

    }

    private void login(final String username, final String password) {
        dialog = DialogUtil.createLoadingDialog(this, "登录中...");
        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    loginHX(username, "123456");
                } else {
                    DialogUtil.closeDialog(dialog);
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginHX(final String currentUsername, final String currentPassword) {
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                DialogUtil.closeDialog(dialog);
                Log.d("LoginActivity", "login: onSuccess");


                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();


                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                DialogUtil.closeDialog(dialog);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            Log.d("MainActivity", "share_media:" + share_media);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Log.d("MainActivity", "data:" + data);

            getInfo(platform);
            if (platform == SHARE_MEDIA.WEIXIN) {
                //unionid:（6.2以前用unionid）uid
                uid = data.get("unionid");
            } else {
                uid = data.get("uid");
            }
            if (!TextUtils.isEmpty(uid)) {
                //如果uid不为空即回调授权成功，则可以调接口告诉后台此时的第三方uid，后台判断此唯一标识值是否存在即判断用户是否用
                //第三方登录过，如果登录过直接进入主界面， 没有登录过则后台存储该值并进入注册界面进行手机号绑定注册
//                judgeUMLogin();，如果登录
            } else {
                Toast.makeText(getApplicationContext(), "暂无法使用该登录方式",
                        Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), "授权成功",
                    Toast.LENGTH_SHORT).show();
            Log.d("user info", "user info:" + data.toString());
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            Toast.makeText(getApplicationContext(), "Authorize fail",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            Toast.makeText(getApplicationContext(), "Authorize cancel",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private void getInfo(SHARE_MEDIA platform) {
        mShareAPI.getPlatformInfo(this, platform, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

                loginThird(map);

            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {

            }
        });
    }

    /**
     * 三方登录
     *
     * @param map
     */
    private void loginThird(Map<String, String> map) {
        Log.d("MainActivity", "map:" + map);
        final String iconurl = map.get("iconurl");//头像
        String screen_name = map.get("screen_name");//QQ昵称
        final String name = map.get("name");//昵称
        String accessToken = map.get("accessToken");
        String gender = map.get("gender");
        String openid = map.get("openid");
        String uid = map.get("uid");
        String expires_in = map.get("expires_in");
        dialog = DialogUtil.createLoadingDialog(this, "登录中...");
        // 使用其他第三方登录 SDK 的获取方法，请自行查询对应 SDK 提供商的文档

        Map<String, Object> map2 = new HashMap<>();
        map2.put("access_token", accessToken);
        map2.put("expires_in", expires_in);
        map2.put("openid", openid);
        map2.put("name", name);
        map2.put("gender", gender);
        map2.put("iconurl", iconurl);


        String platf = platform == SHARE_MEDIA.QQ ? "qq" : "weixin";
        AVUser.loginWithAuthData(map2, platf, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    avUser.put("nick", name);
                    avUser.put("iconurl", iconurl);
                    try {
                        avUser.save();
                    } catch (AVException e1) {
                        e1.printStackTrace();
                    }
                    registerHX(avUser.getUsername(), "123456");
                } else {
                    DialogUtil.closeDialog(dialog);
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerHX(final String username, final String pwd) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // call method in SDK
                    EMClient.getInstance().createAccount(username, pwd);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            loginHX(username, "123456");
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                loginHX(username, "123456");
//                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "环信注册失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
