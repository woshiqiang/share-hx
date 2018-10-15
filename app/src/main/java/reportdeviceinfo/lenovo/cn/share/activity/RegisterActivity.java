package reportdeviceinfo.lenovo.cn.share.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.util.DialogUtil;


/**
 * 注册
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CODE_FLAG = 0x10;
    private EditText et_username;
    private EditText et_password;
    private Button btn_register;
    private TextView title;
    private Dialog dialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();


    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        title.setText("注册");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_register = (Button) findViewById(R.id.btn_register);

        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                submit();
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

        register(username, password);
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     */
    private void register(final String username, final String password) {
        dialog = DialogUtil.createLoadingDialog(this, "注册中...");
        AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(password);// 设置密码
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                DialogUtil.closeDialog(dialog);
                if (e == null) {
                    // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
//                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
//                    RegisterActivity.this.finish();
                    registerHX(username,"123456");
                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "环信注册失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }


}
