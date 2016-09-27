package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2016/8/12.
 */
public class Dialog_SettingsBtn_showAboutInfo extends AlertDialog implements View.OnClickListener {

    private TextView appInfoTitle,appInfoDetail;



    //构造方法
    public Dialog_SettingsBtn_showAboutInfo(Context context) {
        super(context);

    }

    //初始化组件
    public void initComponent(){
        appInfoTitle= (TextView) findViewById(R.id.appInfoTitle);
        appInfoDetail= (TextView) findViewById(R.id.appInfoDetail);
    }

    //对话框入口
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_show_about_info);
        //初始化组件
        initComponent();
        //设置监听
        appInfoTitle.setOnClickListener(this);
        appInfoDetail.setOnClickListener(this);

        //应用的详细信息
        String appInfoDetails=
                "+. 点击歌曲条目快捷播放"+'\n'+
                "+. 长按歌曲条目体验更多功能"+'\n'+
                "+. 选择目录时点击顶部即选定"+'\n'+
                "+. 选择目录时右下角显示音乐数量"+'\n'+
                "+. 更多信息请联系 872965579@qq.com"+'\n'+
                "+. 当前版本 1.0";
        //显示歌曲名称头
        appInfoTitle.setText("About  BCMusic");
        appInfoDetail.setText(appInfoDetails);
    }

    @Override//处理点击事件
    public void onClick(View view) {//点击对话框后消失
        switch (view.getId()){
            case R.id.appInfoTitle:
                break;
            case R.id.appInfoDetail:
                break;
        }
        dismiss();
    }
}
