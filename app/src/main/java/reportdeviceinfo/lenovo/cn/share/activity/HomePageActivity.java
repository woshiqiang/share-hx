package reportdeviceinfo.lenovo.cn.share.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.hyphenate.easeui.EaseConstant;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.adapter.ProductAdapter;
import reportdeviceinfo.lenovo.cn.share.util.DialogUtil;
import reportdeviceinfo.lenovo.cn.share.util.EmptyViewUtil;
import reportdeviceinfo.lenovo.cn.share.util.ImageLoaderUtil;

/**
 * @author
 * @time 2018-10-13 15:44
 * @类描述：他人主页
 * @变更记录:
 */
public class HomePageActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageButton ib_back;
    private RoundedImageView riv_head;
    private Button btn_caht;//聊天
    private TextView tv_nick;
    private TextView tv_sign;
    private ListView listView;
    private List<AVObject> mList = new ArrayList<>();
    private ProductAdapter adapter;
    private AVUser owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initView();
        initData();
        getData();
    }

    private void getData() {
        AVQuery<AVObject> query = new AVQuery<>("Product");
        query.orderByDescending("createdAt");//按发布时间倒叙
        query.whereEqualTo("owner", owner);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mList.clear();
                    mList.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
        //判断是否是自己
        if (AVUser.getCurrentUser().getUsername().equals(owner.getUsername())) {
            //自己 隐藏聊天按钮
            btn_caht.setVisibility(View.GONE);
            //自己的主页，自己的商品可以进行修改和删除
            listView.setOnItemClickListener(this);
        } else {
            btn_caht.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(null);
        }
    }

    private void initData() {
        owner = getIntent().getParcelableExtra("owner");
        //昵称
        String nick = owner.getString("nick");
        if (TextUtils.isEmpty(nick)) {
            tv_nick.setText(owner.getUsername());
        } else {
            tv_nick.setText(nick);
        }


        AVFile headImg = owner.getAVFile("headImg");
        if (headImg != null) {
            ImageLoaderUtil.display(this, headImg.getUrl(), riv_head);
        } else {
            String string = owner.getString("iconurl");
            Log.d("HomePageActivity", "头像---"+string);
            ImageLoaderUtil.display(this, string, riv_head);
        }

        String sign = owner.getString("sign");
        if (!TextUtils.isEmpty(sign)) {
            tv_sign.setText(sign);
        }

        adapter = new ProductAdapter(this, mList);
        listView.setAdapter(adapter);

    }

    private void initView() {
        btn_caht = findViewById(R.id.btn_chat);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        riv_head = (RoundedImageView) findViewById(R.id.riv_head);
        tv_nick = (TextView) findViewById(R.id.tv_nick);
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        listView = (ListView) findViewById(R.id.listView);
        EmptyViewUtil.setEmptyView(listView, View.inflate(this, R.layout.empty_view, null));

        ib_back.setOnClickListener(this);
        btn_caht.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_chat:
                toChat();
                break;
        }
    }

    /**
     * 跳转到聊天界面
     */
    private void toChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        // 单聊
        intent.putExtra("userType", EaseConstant.CHATTYPE_SINGLE);
        intent.putExtra("userName", owner.getUsername());
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        // 创建数据
        final String[] items = new String[]{"修改", "删除"};
        // 创建对话框构建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setTitle("操作")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AVObject ob = mList.get(i);
                        if (which == 0) {
                            //修改
                            modify(ob);
                        } else {
                            delete(ob);
                        }
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 修改
     *
     * @param ob
     */
    private void modify(AVObject ob) {
        Intent intent = new Intent(this, ModifyActivity.class);
        intent.putExtra("ob", ob);
        startActivity(intent);
    }

    /**
     * 删除
     *
     * @param ob
     */
    private void delete(AVObject ob) {
        //删除
        final Dialog deleteDialog = DialogUtil.createLoadingDialog(this, "正在删除...");
        ob.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                DialogUtil.closeDialog(deleteDialog);
                if (e == null) {
                    Toast.makeText(HomePageActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    getData();
                } else {
                    Toast.makeText(HomePageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
