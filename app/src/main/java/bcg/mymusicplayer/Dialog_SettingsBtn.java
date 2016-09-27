package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/7.
 */
public class Dialog_SettingsBtn extends AlertDialog implements View.OnClickListener {
    private Context c;  //！！将MainActivity传递到当前类，作为context参数使用
    private TextView btnSettingsChooseDir, btnSettingsSetBackground,btnSetSleepTime,btnSettingsAbout,exitBCMusic;
    private RadioButton btnSettingsRandomPlay, btnSettingsSingleLoop, btnSettingsAllLoop, btnSettingsOrderPlay;

    public Dialog_SettingsBtn(Context context) {
        super(context);
        this.c = context;
    }


    @Override//入口方法
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b__btn_settings);
        //初始化组件
        initComponent();
        //设置监听
        btnSettingsChooseDir.setOnClickListener(this);
        btnSettingsSetBackground.setOnClickListener(this);
        btnSettingsRandomPlay.setOnClickListener(this);
        btnSettingsSingleLoop.setOnClickListener(this);
        btnSettingsAllLoop.setOnClickListener(this);
        btnSettingsOrderPlay.setOnClickListener(this);
        btnSetSleepTime.setOnClickListener(this);
        btnSettingsAbout.setOnClickListener(this);
        exitBCMusic.setOnClickListener(this);
        //获取播放状态 并将对应radiobutton选中
        setRadionButton();
    }

    //初始化组件
    public void initComponent() {
        btnSettingsChooseDir = (TextView) findViewById(R.id.btnSettingsChooseDir);
        btnSettingsSetBackground = (TextView) findViewById(R.id.btnSettingsSetBackground);
        btnSettingsRandomPlay = (RadioButton) findViewById(R.id.btnSettingsRandomPlay);
        btnSettingsSingleLoop = (RadioButton) findViewById(R.id.btnSettingsSingleLoop);
        btnSettingsAllLoop = (RadioButton) findViewById(R.id.btnSettingsAllLoop);
        btnSettingsOrderPlay = (RadioButton) findViewById(R.id.btnSettingsOrderPlay);
        btnSetSleepTime = (TextView) findViewById(R.id.btnSetSleepTime);
        btnSettingsAbout = (TextView) findViewById(R.id.btnSettingsAbout);
        exitBCMusic = (TextView) findViewById(R.id.exitBCMusic);
    }

    @Override//监听实体菜单按键，退出当前对话框
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_MENU){
            dismiss();//退出对话框
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override//监听方法
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSettingsChooseDir: {//1.选 择 目 录
                new Dialog_SettingsBtn_FileDirChoose(c).show();
                break;
            }
            case R.id.btnSettingsSetBackground: {//2.设 置 背 景-ok
                Activity_Main.setBackGroundPic();
                break;
            }
            case R.id.btnSettingsRandomPlay: {//3.>>随机播放
                MusicService.PlayMode = 0;
                Activity_Main.playMode.setText(R.string.playMode0);
                MusicService.randomList.clear();//将随机列表置0
                PropertyService.saveProperties(9999, MusicService.PlayMode, 9999);
                break;
            }
            case R.id.btnSettingsSingleLoop: {//4.>>单曲循环
                MusicService.PlayMode = 1;
                Activity_Main.playMode.setText(R.string.playMode1);
                PropertyService.saveProperties(9999, MusicService.PlayMode, 9999);
                break;
            }
            case R.id.btnSettingsAllLoop: {//5.>>全部循环
                MusicService.PlayMode = 2;
                Activity_Main.playMode.setText(R.string.playMode2);
                PropertyService.saveProperties(9999, MusicService.PlayMode, 9999);
                break;
            }
            case R.id.btnSettingsOrderPlay: {//6.>>顺序播放
                MusicService.PlayMode = 3;
                Activity_Main.playMode.setText(R.string.playMode3);
                PropertyService.saveProperties(9999, MusicService.PlayMode, 9999);
                break;
            }
            case R.id.btnSetSleepTime: {//7.设置睡眠时间
                new Dialog_SettingsBtn_setSleepTime(c).show();
                break;
            }
            case R.id.btnSettingsAbout: {//8.关 于 应 用
                new Dialog_SettingsBtn_showAboutInfo(c).show();
                break;
            }
            case R.id.exitBCMusic: {//9.退 出 应 用
                //保存配置文件
                PropertyService.saveProperties(Activity_Main.backGroundPicPosition,
                        MusicHandler.musicService.PlayMode,MusicHandler.musicService.songNum);
                //保存musiclist文件
                MusicListService.saveMusiclist(MusicHandler.musicService.musicList);
                //退出应用，释放资源
                ExitSysApplication.getInstance().exit();
                break;
            }
        }
        dismiss();//点击item后让对话框消失
    }

    //获取播放状态 并将对应radiobutton选中
    public void setRadionButton() {
        if (MusicService.PlayMode == 0) {
            btnSettingsRandomPlay.setChecked(true);
        } else if (MusicService.PlayMode == 1) {
            btnSettingsSingleLoop.setChecked(true);
        } else if (MusicService.PlayMode == 2) {
            btnSettingsAllLoop.setChecked(true);
        } else if (MusicService.PlayMode == 3) {
            btnSettingsOrderPlay.setChecked(true);
        }
    }
}