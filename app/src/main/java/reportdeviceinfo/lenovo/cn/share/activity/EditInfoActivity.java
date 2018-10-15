package reportdeviceinfo.lenovo.cn.share.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import reportdeviceinfo.lenovo.cn.share.R;

public class EditInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton ib_back;
    private TextView tv_title;
    private Button btn_submit;
    private AppCompatEditText et_content;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        initView();

        flag = getIntent().getStringExtra("flag");
        if (flag.equals("nick")) {
            tv_title.setText("编辑昵称");
        } else if (flag.equals("sign")) {
            tv_title.setText("编辑签名");
        }
    }

    private void initView() {
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_content = (AppCompatEditText) findViewById(R.id.et_content);

        ib_back.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    private void submit() {
        String content = et_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        AVUser user = AVUser.getCurrentUser();
        if (flag.equals("nick")) {
            user.put("nick", content);
        } else if (flag.equals("sign")) {
            user.put("sign", content);
        }

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(EditInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
