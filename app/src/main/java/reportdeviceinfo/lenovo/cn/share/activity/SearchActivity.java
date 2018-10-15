package reportdeviceinfo.lenovo.cn.share.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.adapter.ProductAdapter;
import reportdeviceinfo.lenovo.cn.share.util.DialogUtil;
import reportdeviceinfo.lenovo.cn.share.util.EmptyViewUtil;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageButton ib_back;
    private EditText et_search;
    private Button btn_search;
    private ListView listView;
    private List<AVObject> mList = new ArrayList<>();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initData();
    }


    private void initView() {
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        et_search = (EditText) findViewById(R.id.et_search);
        btn_search = (Button) findViewById(R.id.btn_search);
        listView = (ListView) findViewById(R.id.listView);

        ib_back.setOnClickListener(this);
        et_search.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

    private void initData() {
        adapter = new ProductAdapter(this, mList);
        View emptyView = View.inflate(this, R.layout.empty_view, null);
//        listView.setEmptyView(emptyView);
        EmptyViewUtil.setEmptyView(listView, emptyView);
        listView.setAdapter(adapter);
        //设置空页面

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_search:
                search();
                break;
        }
    }

    private void search() {
        String keyWord = et_search.getText().toString().trim();
        if (TextUtils.isEmpty(keyWord)) {
            Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        getData(keyWord);
    }

    /**
     * 搜索
     *
     * @param keyWord：关键词
     */
    private void getData(String keyWord) {
        final Dialog dialog = DialogUtil.createLoadingDialog(this, "搜索中...");
        AVQuery<AVObject> avQuery = new AVQuery<>("Product");
        avQuery.orderByDescending("createdAt");
        avQuery.whereContains("title", keyWord);
        avQuery.include("owner");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                DialogUtil.closeDialog(dialog);
                if (e == null) {
                    mList.clear();
                    mList.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AVObject item = mList.get(i);
        //点击量加1
        try {
            int scanNumber = item.getInt("scanNumber") + 1;
            item.put("scanNumber", scanNumber);
            item.save();
        } catch (AVException e) {
            e.printStackTrace();
        }
        AVUser owner = item.getAVUser("owner");
        //跳转到个人主页
        Intent intent = new Intent(SearchActivity.this, HomePageActivity.class);
        intent.putExtra("owner", owner);
        startActivity(intent);
    }
}
