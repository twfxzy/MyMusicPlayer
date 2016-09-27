package bcg.mymusicplayer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Main extends AppCompatActivity {
    private ImageButton btnLast,btnNext,btnSettings;
    static ImageButton btnStop,btnStartOrPause;
    static TextView playerStatus,musicName,playMode,songTime,songName;
    static ListView listView;
    static LinearLayout mainActivityAll;
    static SeekBar seekBar;
    private Listener_BtnClick listenerBtnClick;
    private Listener_SeekBar listenerSeekBar;
    private Listener_ListViewItemClick listenerListViewItemClick;
    private Listener_ListViewItemLongClick listenerListViewItemLongClick;
    static MusicHandler mch;
    static MyAdapter adapter;
    static int ptemp=0;//p是点击listview歌曲列表的item位置
    static List<Map<String, Object>> musicListMap;
    static List<Integer> backGroundPic=new ArrayList<>();
    static int backGroundPicPosition;
    static PropertyService propertyService; //播放器参数配置类
    private Map<String, Object> map=new HashMap<>();

    public void initComponent(){//初始化组件绑定
        mainActivityAll= (LinearLayout) findViewById(R.id.mainActivityAll);
        btnStop= (ImageButton) findViewById(R.id.btnStop);
        btnLast= (ImageButton) findViewById(R.id.btnLast);
        btnStartOrPause= (ImageButton) findViewById(R.id.btnStartOrPause);
        btnNext= (ImageButton) findViewById(R.id.btnNext);
        btnSettings= (ImageButton) findViewById(R.id.btnSettings);

        playerStatus= (TextView) findViewById(R.id.playerStatus);
        musicName= (TextView) findViewById(R.id.musicName);//在底部textview显示的音乐名称
        playMode= (TextView) findViewById(R.id.playMode);
        songTime= (TextView) findViewById(R.id.songTime);
        songName= (TextView) findViewById(R.id.songName);//在上部listview显示的音乐名称
        listView= (ListView) findViewById(R.id.listView);
        seekBar= (SeekBar) findViewById(R.id.seekBar);

        //初始化背景图片数组
        backGroundPic.add(R.drawable.background00);
        backGroundPic.add(R.drawable.background01);
        backGroundPic.add(R.drawable.background02);
        backGroundPic.add(R.drawable.background03);
        backGroundPic.add(R.drawable.background04);
        backGroundPic.add(R.drawable.background05);
        backGroundPic.add(R.drawable.background06);
    }
    //入口主方法
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a__activity_main);

        //记录该Activity，方便按序退出程序
        ExitSysApplication.getInstance().addActivity(this);

        //要最先初始化所有组件
        initComponent();

        //实例化音乐处理对象
        mch=new MusicHandler();

        //加载配置参数
        initProperties();

        //配置所有的监听器
        setAllListener();

        //填充视图
        //1.获取数据源
        musicListMap=mch.getMusicList();
        //2.构建适配器
        adapter = new MyAdapter(Activity_Main.this, musicListMap ,R.layout.a_song_item, new String[] { "fileName" }, new int[] { R.id.songName });
        //3.设置视图适配器
        listView.setAdapter(adapter);
    }

    //加载配置参数
    private void initProperties(){
        //启动应用时，初始化参数配置器，从文件加载配置参数，如果文件不存在，则加载默认参数
        propertyService=new PropertyService(this);
        map=propertyService.loadProperties();//加载播放器的4个参数，文件不存在，则加载默认值
        //背景图值赋值
        backGroundPicPosition=Integer.parseInt(map.get("backGroundId").toString());
        //播放模式赋值
        MusicService.PlayMode=Integer.parseInt(map.get("playMode").toString());
        //列表中条目的位置 赋值
        MusicService.songNum=Integer.parseInt(map.get("itemPosition").toString());

        //获取到背景图值后，直接完成设置背景图
        setBackGroundPic(backGroundPicPosition);

        //因为musicsevice在此之前已经初始化了，所以需要对播放器底部状态栏重新设置
        if(MusicService.musicList.size()==0 || MusicService.songNum == 9000){
            //如果musicList突然意外无歌曲，或者检测到 songNum=9000 这个定义为无歌曲的状态
            listView.setAdapter(null);//将歌曲列表清空
            //歌曲列表无内容时，清除切换列表前播放的歌曲及状态
            btnStop.callOnClick();
            //底部状态栏信息
            playerStatus.setText(R.string.noMusic);
            musicName.setText(R.string.defaultMusicName);
            playMode.setText("");
            songTime.setText(R.string.defaultMusicTime);

            MusicService.songNum = 9000;// 只要无音乐，则置为9000的状态
            propertyService.saveProperties(9999,9999,MusicService.songNum);

            Toast.makeText(Activity_Main.this,"未找到音乐，请检查后重试",Toast.LENGTH_SHORT).show();
        }else{//如果一切正常，且有歌曲
            mch.musicService.start();//预加载歌曲资源，start自动重绘listview
            //软件初次加载，初始化底部状态栏信息
            playerStatus.setText(R.string.isReady);
            musicName.setText(MusicService.songNamep);
            playMode.setText(MusicService.playModeToString(MusicService.PlayMode));
            mch.setSongTime(0,mch.musicService.player.getDuration());
        }
    }

    //配置所有的监听器
    private void setAllListener(){
        //给停止、上首、播放/暂停、下首、设置 按键设置监听
        listenerBtnClick=new Listener_BtnClick(this);
        btnStop.setOnClickListener(listenerBtnClick);
        btnLast.setOnClickListener(listenerBtnClick);
        btnStartOrPause.setOnClickListener(listenerBtnClick);
        btnNext.setOnClickListener(listenerBtnClick);
        btnSettings.setOnClickListener(listenerBtnClick);

        //给seekbar设置监听
        listenerSeekBar =new Listener_SeekBar();
        seekBar.setOnSeekBarChangeListener(listenerSeekBar);

        //给ListView滑动列表item设置监听
        listenerListViewItemClick =new Listener_ListViewItemClick();
        listView.setOnItemClickListener(listenerListViewItemClick);
        listenerListViewItemLongClick =new Listener_ListViewItemLongClick();
        listView.setOnItemLongClickListener(listenerListViewItemLongClick);

        //设置来电状态监听
        TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new Listener_PhoneState(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    //重绘歌曲列表listview
    static void reDrawListView(int position){
        ptemp=position;
        listView.invalidateViews();
        //只要里面的3个参数发生变化，则调用保存方法
        propertyService.saveProperties(9999, 9999, ptemp);
    }

    //按顺序切换背景图片
    static void setBackGroundPic(){
        if(backGroundPicPosition < backGroundPic.size()-1){
            backGroundPicPosition++;
        }else{
            backGroundPicPosition=0;
        }
        mainActivityAll.setBackgroundResource(backGroundPic.get(backGroundPicPosition));
        //只要里面的3个参数发生变化，则调用保存方法
        propertyService.saveProperties(backGroundPicPosition, 9999, 9999);
    }

    //按指定位置设置背景图片
    static void setBackGroundPic(int itemPosition){
        if(itemPosition > backGroundPic.size()-1 || itemPosition<0){//如果给定的参数越界，则归0
            itemPosition=0;
            //只要里面的3个参数发生变化，则调用保存方法
            propertyService.saveProperties(itemPosition, 9999, 9999);
        }
        backGroundPicPosition=itemPosition;
        mainActivityAll.setBackgroundResource(backGroundPic.get(backGroundPicPosition));
    }

    @Override//监听实体菜单键，呼出右下角设置菜单
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //在主屏幕中，按下实体菜单键，则弹出右下角菜单
        if(keyCode==KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP ){
            btnSettings.callOnClick();//呼叫监听方法
            return true;
        }
        //在主屏幕中，按下实体返回键，则回到桌面，不退出应用
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    //切换播放键的图标
    static void setPlayIcon(){
        btnStartOrPause.setImageResource(R.drawable.button_icon_play);
    }
    //切换播放键的图标
    static void setPauseIcon(){
        btnStartOrPause.setImageResource(R.drawable.button_icon_pause);
    }

    //给listview做缓存优化
    class MyAdapter extends SimpleAdapter{
        public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView= LayoutInflater.from(Activity_Main.this).inflate(R.layout.a_song_item,null);
                viewHolder=new ViewHolder();
                //打标签，绑定id
                viewHolder.tv= (TextView) convertView.findViewById(R.id.songName);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
                if(mch.getMusicList().size()>0){//避免重设文件夹后，该文件夹下无歌曲，此时会空指针异常
                    Map<String,Object> map=mch.getMusicList().get(position);
                    viewHolder.tv.setText(map.get("fileName").toString());
                }
            }

            //绘制listview的底色
            if(position==ptemp) {//绘制被点击的item,这里加00是设置透明色
                convertView.setBackgroundColor(Color.parseColor(getString(R.string.colorListViewClicked)));
            }
            else {//绘制未被点击的item,这里设置的是透明色
                convertView.setBackgroundColor(Color.parseColor(getString(R.string.colorListViewUnClicked)));
            }

            return super.getView(position, convertView, parent);
        }
    }

    class ViewHolder{//音乐内容单行显示歌名
        TextView tv;
    }

}
