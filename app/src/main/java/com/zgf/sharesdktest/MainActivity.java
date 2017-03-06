package com.zgf.sharesdktest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class MainActivity extends AppCompatActivity {
    private String title = "title";
    private String imageUrl = "https://www.baidu.com/img/bd_logo1.png";
    private String content = "content";
    private String shareUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOne();
            }
        });
    }

    private void shareOne() {
        ShareSDK.initSDK(this);

        showShareDialog();
    }

    //===============各平台分享============

    /**
     * 微博客户端分享图片不能大于2M
     * 仅支持JPEG、GIF、PNG格式
     * 新浪微博使用客户端分享不会正确回调
     * 注：微博分享链接是将链接写到setText内：eg：setText(“分享文本 http://mob.com”);
     * imagePath和imageUrl同时存在，imageUrl将被忽略。
     */
    private void shareSina() {
        share(SinaWeibo.NAME);

//        shareOnlyImage(SinaWeibo.NAME);
    }

    /**
     * QQ分享支持图文
     * title：最多30个字符
     * text：最多40个字符
     * QQ分享图文和音乐，在PC版本的QQ上可能只看到一条连接，
     * 因为PC版本的QQ只会对其白名单的连接作截图，
     * 如果不在此名单中，则只是显示连接而已.
     * 如果只分享图片在PC端看不到图片的，只会显示null，在手机端会显示图片和null字段。
     */
    private void shareQQ() {
        share(QQ.NAME);

//        shareOnlyImage(QQ.NAME);
    }

    /**
     * QQ空间支持分享文字和图文 参数说明 title：最多200个字符 text：最多600个字符
     * QQ空间分享时一定要携带title、titleUrl、site、siteUrl，
     * QQ空间本身不支持分享本地图片，
     * 因此如果想分享本地图片，图片会先上传到ShareSDK的文件服务器，得到连接以后才分享此链接。
     * 由于本地图片更耗流量，因此imageUrl优先级高于imagePath。
     * site是分享此内容的网站名称，仅在QQ空间使用；siteUrl是分享此内容的网站地址，仅在QQ空间使用；
     */
    private void shareQQzone() {
        share(QZone.NAME);

//        shareOnlyImage(QZone.NAME);
    }

    /**
     * 微信（好友、朋友圈、收藏）
     * 说明 title：512Bytes以内
     * text：1KB以内 imageData：10M以内
     * imagePath：10M以内
     * imageUrl：10KB以内
     * 微信分享分绕过审核和不绕过审核，不绕过审核必须保证微信后台配置的签名与您的app的签名一致，否则无法分享；
     * 微信并无实际的分享网络图片和分享bitmap的功能，如果设置了网络图片，
     * 此图片会先下载会本地，之后再当作本地图片分享，因此延迟较大。
     */
    private void shareWechat() {
        share(Wechat.NAME);

//        shareOnlyImage(Wechat.NAME);
    }

    private void shareWechatConment() {
        share(WechatMoments.NAME);

//        shareOnlyImage(WechatMoments.NAME);
    }

    /**
     * 分享内容为：标题，内容，图片，网站名称，网址
     * @param platformStr
     */
    private void share(String platformStr) {
        Platform.ShareParams params = new Platform.ShareParams();
        params.setTitle(title);
        params.setText(content);
        params.setImageUrl(imageUrl);
        params.setSite("发布分享的网站名称");
        params.setSiteUrl("发布的网站链接");

        Platform platform = ShareSDK.getPlatform(platformStr);
        platform.setPlatformActionListener(paListener);

        platform.share(params);
    }

    /**
     * 分享的内容：标题和图片
     * @param platformStr
     */
    private void shareOnlyImage(String platformStr) {
        Platform.ShareParams params = new Platform.ShareParams();
        params.setTitle(title);
        params.setImageUrl(imageUrl);

        Platform platform = ShareSDK.getPlatform(platformStr);
        platform.setPlatformActionListener(paListener);

        platform.share(params);
    }

    public void showShareDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
        window.setContentView(R.layout.dialog_share);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RecyclerView view = (RecyclerView) window.findViewById(R.id.rv_share);
        initShareView(view);

    }

    private void initShareView(RecyclerView view) {
        List<ShareModel> list = new ArrayList<>();
        ShareModel shareModel = new ShareModel();

        shareModel.setType(0);
        shareModel.setImage(R.drawable.ssdk_oks_classic_sinaweibo);
        shareModel.setPlatform("微博");
        list.add(shareModel);

        shareModel = new ShareModel();
        shareModel.setType(1);
        shareModel.setImage(R.drawable.ssdk_oks_classic_wechat);
        shareModel.setPlatform("微信");
        list.add(shareModel);

        shareModel = new ShareModel();
        shareModel.setType(2);
        shareModel.setImage(R.drawable.ssdk_oks_classic_wechatmoments);
        shareModel.setPlatform("朋友圈");
        list.add(shareModel);

        shareModel = new ShareModel();
        shareModel.setType(3);
        shareModel.setImage(R.drawable.ssdk_oks_classic_qq);
        shareModel.setPlatform("qq");
        list.add(shareModel);

        shareModel = new ShareModel();
        shareModel.setType(4);
        shareModel.setImage(R.drawable.ssdk_oks_classic_qzone);
        shareModel.setPlatform("qq空间");
        list.add(shareModel);

        shareModel = new ShareModel();
        shareModel.setType(5);
        shareModel.setImage(R.drawable.ssdk_oks_classic_line);
        shareModel.setPlatform("复制链接");
        list.add(shareModel);

        view.setLayoutManager(new GridLayoutManager(this, 3));
        ShareViewAdapter adapter = new ShareViewAdapter(this, list);
        view.setAdapter(adapter);
        adapter.setOnitemClick(new ShareViewAdapter.OnItemCLick() {
            @Override
            public void onClick(int type) {
                switch (type){
                    case 0:
                        shareSina();
                        break;
                    case 1:
                        shareWechat();
                        break;
                    case 2:
                        shareWechatConment();
                        break;
                    case 3:
                        shareQQ();
                        break;
                    case 4:
                        shareQQzone();
                        break;
                    case 5:
                        Toast.makeText(MainActivity.this, "复制链接", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    PlatformActionListener paListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.e("====platform====" + platform.getName(), "===分享成功===");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e("====platform====" + platform.getName(), "===分享失败===");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Log.e("====platform====" + platform.getName(), "===取消分享===");
        }
    };
}

class ShareViewAdapter extends RecyclerView.Adapter<ShareViewAdapter.ShareViewHolder> {
    private List<ShareModel> list;
    private Context context;

    public ShareViewAdapter(Context context, List<ShareModel> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_share_view, null);
        return new ShareViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, final int position) {
        holder.imageView.setImageResource(list.get(position).getImage());
        holder.textView.setText(list.get(position).getPlatform());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemCLick.onClick(list.get(position).getType());
            }
        });
    }

    class ShareViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ShareViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item_view);
            textView = (TextView) itemView.findViewById(R.id.tv_share_platform);
        }

    }

    public interface OnItemCLick {
        void onClick(int type);
    }

    private OnItemCLick onItemCLick;
    public void setOnitemClick(OnItemCLick onItemCLick) {
        this.onItemCLick = onItemCLick;
    }

}
