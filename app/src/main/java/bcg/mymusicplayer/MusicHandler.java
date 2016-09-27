package bcg.mymusicplayer;


import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicHandler {
    public static MusicService musicService;
    private MusicThread musicThread;// 自动改变进度条的线程
    private Myhandler myhandler;
    private Thread mtd;
    static boolean MusicThreadStatus=false;//刚进入界面时，不开线程


    //实例化对象时初始化
    public MusicHandler(){
        musicService=new MusicService();//播放音乐报错时提示Toast
        musicService.initService();
        myhandler=new Myhandler();
    }

    //如果线程标志位为真就新开一个线程
    public void startNewThread(){
        if(MusicThreadStatus==true){
            musicThread=new MusicThread();
            mtd= new Thread(musicThread);
            mtd.start();
        }
    }

    //获取歌曲列表
    static List<Map<String, Object>> getMusicList(){
        List<Map<String, Object>> date = new ArrayList<Map<String, Object>>();
        for (int i = 0; i <MusicService.musicList.size() ; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            File file = new File(MusicService.musicList.get(i));
            map.put("fileName", (i+1)+". "+file.getName());
            date.add(map);
        }
        return date;
    }

    //设置歌曲播放进度信息
    static void setSongTime(int position,int mMax){
        Activity_Main.songTime.setText(Util_Tool.IntegerToTime(position/1000)+"/"+
                Util_Tool.IntegerToTime(mMax/1000));
    }

    //处理多线程消息
    class Myhandler extends Handler{
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //获取进度条长度，歌曲时长等基本参数
            int position = musicService.player.getCurrentPosition();//得到当前歌曲播放进度(秒)
            int mMax = musicService.player.getDuration();//最大秒数
            int sMax = Activity_Main.seekBar.getMax();//seekBar最大值，算百分比
            //设置歌曲名
            Activity_Main.musicName.setText(musicService.songNamep);
            //设置进度条
            Activity_Main.seekBar.setProgress(position * sMax / mMax);
            //设置歌曲播放进度
            setSongTime(position,mMax);
        }
    }

    //创建多线程对象
    class MusicThread implements Runnable {
        @Override
        public void run() {
            while (MusicThreadStatus)
                try {
                    myhandler.sendMessage(new Message());
                    // 每间隔1秒发送一次更新消息
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

}

