package reportdeviceinfo.lenovo.cn.share.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import java.io.FileNotFoundException;

import reportdeviceinfo.lenovo.cn.share.MyLeanCloudApp;
import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.util.DialogUtil;

/**
 * @author
 * @Date 2018-10-13.
 * 发布商品
 */
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_SYSTEM_PIC = 0x11;
    private ImageButton ib_back;//返回
    private Button btn_submit;//提交
    private EditText et_title;//标题
    private EditText et_description, et_price;//描述，价格
    private ImageView iv_album;//封面
    private String imagePath;//图片路径

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initView();
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
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SYSTEM_PIC);//打开系统相册
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

        if (TextUtils.isEmpty(imagePath)) {
            Toast.makeText(this, "请选择封面", Toast.LENGTH_SHORT).show();
            return;
        }

        //进度框
        final Dialog loadingDialog = DialogUtil.createLoadingDialog(this, "提交中...");
        //封装商品信息
        AVObject product = new AVObject("Product");
        product.put("title", title);
        product.put("description", description);
        product.put("price", price);
        product.put("scanNumber", 0);
        product.put("owner", AVUser.getCurrentUser());
        //添加经纬度
        AVGeoPoint point = new AVGeoPoint(MyLeanCloudApp.latitude,MyLeanCloudApp.longitude);
        product.put("whereCreated",point);


        //图片
        try {
            AVFile file = AVFile.withAbsoluteLocalPath(System.currentTimeMillis() + ".png", imagePath);
            product.put("image", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        product.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                //关闭等待框
                DialogUtil.closeDialog(loadingDialog);
                if (e == null) {
                    Toast.makeText(PublishActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PublishActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SYSTEM_PIC && resultCode == Activity.RESULT_OK && null != data) {
            if (Build.VERSION.SDK_INT >= 19) {
                handleImageOnKitkat(data);
            } else {
                handleImageBeforeKitkat(data);
            }
        }
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是File类型的uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片

    }

    private void handleImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            iv_album.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
}
