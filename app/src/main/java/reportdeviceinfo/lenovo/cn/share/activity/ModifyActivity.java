package reportdeviceinfo.lenovo.cn.share.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import java.io.FileNotFoundException;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.util.DialogUtil;
import reportdeviceinfo.lenovo.cn.share.util.ImageLoaderUtil;

/**
 * @author
 * @Date 2018-10-15.
 * 修改商品
 */
public class ModifyActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE = 0x100;
    private ImageButton ib_back;//返回
    private Button btn_submit;//提交
    private EditText et_title;//标题
    private EditText et_description, et_price;//描述，价格
    private ImageView iv_album;//封面
    private String imagePath;//图片路径
    private AVObject ob;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        initView();
        initData();
    }

    private void initData() {
        ob = getIntent().getParcelableExtra("ob");
        if (ob != null) {
            et_title.setText(ob.getString("title"));
            et_description.setText(ob.getString("description"));
            et_price.setText("" + ob.getInt("price"));
            try {
                String url = ob.getAVFile("image").getUrl();
                ImageLoaderUtil.display(this, url, iv_album);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 绑定控件
     */
    private void initView() {
        ib_back = findViewById(R.id.ib_back);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_title = (EditText) findViewById(R.id.et_title);
        et_description = (EditText) findViewById(R.id.et_description);
        et_price = (EditText) findViewById(R.id.et_price);
        iv_album = (ImageView) findViewById(R.id.iv_album);

        iv_album.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        ib_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back://返回
                finish();
                break;
            case R.id.iv_album://选择封面
                selectAlbum();
                break;
            case R.id.btn_submit://提交
                submit();
                break;
        }
    }

    /**
     * 打开图库，选择图片
     */
    private void selectAlbum() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            //打开系统相册
            openAlbum();
        }
    }

    //打开系统相册
    private void openAlbum() {
        GalleryFinal.openGallerySingle(REQUEST_CODE_CHOOSE, mOnHanlderResultCallback);
    }


    /**
     * 提交
     */
    private void submit() {
        //商品标题
        String title = et_title.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "商品标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //价格
        Integer price = 0;
        try {
            //避免价格为空，转换异常
            price = Integer.valueOf(et_price.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "价格输入有误", Toast.LENGTH_SHORT).show();
            return;
        }

        //商品描述
        String description = et_description.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "商品描述不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        //进度框
        final Dialog loadingDialog = DialogUtil.createLoadingDialog(this, "提交中...");
        //封装商品信息
        ob.put("title", title);
        ob.put("description", description);
        ob.put("price", price);
        ob.put("owner", AVUser.getCurrentUser());
        //图片
        try {
            if (imagePath != null) {
                AVFile file = AVFile.withAbsoluteLocalPath(System.currentTimeMillis() + ".png", imagePath);
                ob.put("image", file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ob.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                //关闭等待框
                DialogUtil.closeDialog(loadingDialog);
                if (e == null) {
                    Toast.makeText(ModifyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ModifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                imagePath = resultList.get(0).getPhotoPath();
                ImageLoaderUtil.display(ModifyActivity.this, imagePath, iv_album);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(ModifyActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };


}
