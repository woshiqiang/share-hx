package reportdeviceinfo.lenovo.cn.share.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.hyphenate.chat.EMClient;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.FileNotFoundException;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.activity.EditInfoActivity;
import reportdeviceinfo.lenovo.cn.share.activity.HomePageActivity;
import reportdeviceinfo.lenovo.cn.share.activity.LoginActivity;
import reportdeviceinfo.lenovo.cn.share.util.ImageLoaderUtil;

/**
 * @author
 * @time 2018-10-12 10:04
 * @类描述：我的
 * @变更记录:
 */

public class MeFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE = 100;

    private RoundedImageView riv_head;
    private TextView tv_nick;
    private TextView tv_introduction;
    private LinearLayout ll_my_product, ll_my_info;
    private LinearLayout ll_logout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, null);
        initView(view);

        getData();
        return view;
    }

    private void getData() {
        //拿到当前用户
        AVUser currentUser = AVUser.getCurrentUser();
        //用户名
        String username = currentUser.getUsername();
        String nick = currentUser.getString("nick");
        if (TextUtils.isEmpty(nick)) {
            tv_nick.setText(username);
        } else {
            tv_nick.setText(nick);
        }
        String sign = currentUser.getString("sign");
        if (!TextUtils.isEmpty(sign)) {
            tv_introduction.setText(sign);
        } else {
            tv_introduction.setText("点我设置签名");
        }


        AVFile headImg = currentUser.getAVFile("headImg");
        if (headImg != null)
            ImageLoaderUtil.display(getContext(), headImg.getUrl(), riv_head);
    }

    private void initView(View view) {
        riv_head = (RoundedImageView) view.findViewById(R.id.riv_head);
        tv_nick = (TextView) view.findViewById(R.id.tv_nick);
        tv_introduction = (TextView) view.findViewById(R.id.tv_introduction);
        tv_introduction.setOnClickListener(this);

        riv_head.setOnClickListener(this);
        ll_my_info = (LinearLayout) view.findViewById(R.id.ll_my_info);
        ll_my_info.setOnClickListener(this);
        ll_my_product = (LinearLayout) view.findViewById(R.id.ll_my_product);
        ll_my_product.setOnClickListener(this);
        ll_logout = (LinearLayout) view.findViewById(R.id.ll_logout);
        ll_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.riv_head://头像
                selectImage();
                break;
            case R.id.ll_my_info://昵称
                Intent intent1 = new Intent(getContext(), EditInfoActivity.class);
                intent1.putExtra("flag", "nick");
                startActivity(intent1);
                break;
            case R.id.tv_introduction://签名
                Intent intent2 = new Intent(getContext(), EditInfoActivity.class);
                intent2.putExtra("flag", "sign");
                startActivity(intent2);
                break;
            case R.id.ll_my_product://我的商品
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                intent.putExtra("owner", AVUser.getCurrentUser());
                startActivity(intent);
                break;
            case R.id.ll_logout://退出登录
                logout();
                break;
        }
    }

    /**
     * 退出登录
     */
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder
                .setTitle("退出登录")
                .setMessage("您是否要退出登录?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AVUser.getCurrentUser().logOut();
                        EMClient.getInstance().logout(false);
                        dialogInterface.dismiss();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    private void selectImage() {
        GalleryFinal.openGallerySingle(REQUEST_CODE_CHOOSE, mOnHanlderResultCallback);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                final String photoPath = resultList.get(0).getPhotoPath();

                try {
                    AVUser currentUser = AVUser.getCurrentUser();
                    AVFile file = AVFile.withAbsoluteLocalPath(System.currentTimeMillis() + ".png", photoPath);
                    currentUser.put("headImg", file);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            ImageLoaderUtil.display(getContext(), photoPath, riv_head);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                }


            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
