package reportdeviceinfo.lenovo.cn.share.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

import java.text.SimpleDateFormat;
import java.util.List;

import reportdeviceinfo.lenovo.cn.share.R;
import reportdeviceinfo.lenovo.cn.share.util.ImageLoaderUtil;

public class ProductAdapter extends BaseAdapter {
    private Context mContext;
    private List<AVObject> mList;

    public ProductAdapter(Context mContext, List<AVObject> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        AVObject avObject = mList.get(i);
        AVFile image = avObject.getAVFile("image");
        if (image != null) {
            ImageLoaderUtil.display(mContext, image.getUrl(), holder.iv_album);
        } else {
            ImageLoaderUtil.display(mContext, R.mipmap.img_place_holder, holder.iv_album);
        }

        holder.tv_title.setText(avObject.getString("title"));
        holder.tv_description.setText(avObject.getString("description"));
        holder.tv_price.setText(avObject.getInt("price") + "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        holder.tv_date.setText(sdf.format(avObject.getCreatedAt()));

        return view;
    }

    static class ViewHolder {
        public ImageView iv_album;
        public TextView tv_title;
        public TextView tv_description;
        private TextView tv_price;
        private TextView tv_date;

        public ViewHolder(View view) {
            iv_album = view.findViewById(R.id.iv_album);
            tv_title = view.findViewById(R.id.tv_title);
            tv_description = view.findViewById(R.id.tv_description);
            tv_price = view.findViewById(R.id.tv_price);
            tv_date = view.findViewById(R.id.tv_date);
        }

    }
}