package bcg.mymusicplayer;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class MusicService {
    static List<String> musicList;// 存放找到的所有音乐的绝对路径。
    public MediaPlayer player; // 定义多媒体对象
    static int songNum; // 当前播放的歌曲在List中的下标
    static String songNamep; // 当前播放的歌曲名
    static List<Integer> randomList=new ArrayList<>();
    public boolean beforeCallisPlaying; //true表示来电之前正在播放
    static int PlayMode;//0 为随机播放;1 为单曲循环;2 为全部循环;3(默认) 为按序播放,至最后一首结束

    //初始化MusicService参数
    public void initService() {
        musicList = new ArrayList<String>();
        //初始化player,增加监听:自动播放下一首
        player = new MediaPlayer();
        //如果当前歌曲播放完毕,自动播放下一首,有的ogg 有android_loop标志，不能下一首
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer arg0) {
                nextSong(PlayMode);
            }
        });

        //从列表文件读取歌曲列表
        musicList.addAll(MusicListService.loadMusicList());
        //初次打开应用，自动加载sd卡的音乐，然后保存到文件
        MusicListService.saveMusiclist(musicList);
        //初次加载 在Activity_main里完成
        beforeCallisPlaying=false;  //初次加载时，应置为false
        //初始化定时器的任务
//        initSleepTimerTask();
//        initSleepTimerHandler();
    }

    static String playModeToString(int playMode){
        String temp=null;
        switch (playMode){
            case 0:{
                temp="-随机-";
                break;
            }
            case 1: {
                temp ="-单曲-";
                break;
            }
            case 2:{
                temp="-全部-";
                break;
            }
            case 3:{
                temp="-顺序-";
                break;
            }
        }
        return temp;
    }

    //切换目录后，初次遍历文件夹
    public void dirToMusicList(File musicDir){
        musicList.clear();//必须先清空旧列表
        //遍历文件夹 导入歌曲。如果找到了歌曲，则如下
        if (musicDir.listFiles(new MusicFilter()).length > 0) {
            for (File file : musicDir.listFiles(new MusicFilter())) {
                musicList.add(file.getAbsolutePath());//此时的musiclist是文件的绝对路径，而不是文件名
            }
            //初次遍历需要排序。忽略大小写对list进行排序
            Util_Tool.sortIgnoreCase(musicList);

            songNum=0;//初次加载将歌曲位置初始化为0
            start();//预加载歌曲资源
            //软件初次加载，初始化底部状态栏信息
            Activity_Main.playerStatus.setText(R.string.isReady);
            Activity_Main.musicName.setText(songNamep);
            Activity_Main.playMode.setText(playModeToString(PlayMode));
            Activity_Main.mch.setSongTime(0,player.getDuration());
        }else{//如果未找到歌曲，则如下
            Activity_Main.listView.setAdapter(null);//将歌曲列表清空
            //歌曲列表无内容时，清除切换列表前播放的歌曲及状态
            Activity_Main.btnStop.callOnClick();
            //底部状态栏信息
            Activity_Main.playerStatus.setText(R.string.noMusic);
            Activity_Main.musicName.setText(R.string.defaultMusicName);
            Activity_Main.playMode.setText("");
            Activity_Main.songTime.setText(R.string.defaultMusicTime);
            songNum=9000;  //9000这种状态定义为无歌曲
        }
        //将新列表存入文件
        MusicListService.saveMusiclist(MusicService.musicList);
        PropertyService.saveProperties(9999, 9999, songNum);
    }

    public void reNewChangePosition(int songNumAfter){
        songNum=songNumAfter;
        songNamep=(songNum+1)+"."+getSongName(musicList.get(songNum));
        //软件初次加载，初始化底部状态栏信息
        Activity_Main.musicName.setText(songNamep);
        Activity_Main.reDrawListView(songNum);
        PropertyService.saveProperties(9999,9999,songNum);
    }

    public void reNewChangeName(){
        //如果长按改名、位置、删除等操作的item位置是正在播放的位置,则播放器置为初始位置
        if(songNum==Dialog_LVItemLongClick.songItemPosition){
            songNum=0;//初次加载将歌曲位置初始化为0
            //保存songNum到文件
            PropertyService.saveProperties(9999,9999,songNum);

            start();//预加载歌曲资源
            //软件初次加载，初始化底部状态栏信息
            Activity_Main.playerStatus.setText(R.string.isReady);
            Activity_Main.musicName.setText(songNamep);
            Activity_Main.playMode.setText(playModeToString(PlayMode));
            Activity_Main.mch.setSongTime(0,player.getDuration());
        }
    }

    public void reNewDeleteFromFile(){
        if(musicList.size()>0) {
            if (songNum > Dialog_LVItemLongClick.songItemPosition) {
                songNum--;
            }else if (songNum == Dialog_LVItemLongClick.songItemPosition) {
                if(songNum == musicList.size()){//此时的musicList已经删除了一位，故不用musicList.size()-1
                    songNum--;
                }
                if(player.isPlaying()){
                    if( PlayMode ==0 || PlayMode ==1 ){
                        nextSong(PlayMode);//删除完毕后自动播放下一首
                    }else{//当为全部循环或按序播放时，不能再将songNum+1，而应直接播放
                        start();
                        player.start();
                        beforeCallisPlaying=true;  //只要一播放，则将其置为true
                    }
                }
            }

            //修改底部状态栏
            songNamep = (songNum + 1) + "." + getSongName(musicList.get(songNum));
            Activity_Main.musicName.setText(songNamep);
            Activity_Main.reDrawListView(songNum);
        }
        else{//如果全删除完了，则置为无歌曲的状态
            Activity_Main.listView.setAdapter(null);//将歌曲列表清空
            //歌曲列表无内容时，清除切换列表前播放的歌曲及状态
            Activity_Main.btnStop.callOnClick();
            //底部状态栏信息
            Activity_Main.playerStatus.setText(R.string.noMusic);
            Activity_Main.musicName.setText(R.string.defaultMusicName);
            Activity_Main.playMode.setText("");
            Activity_Main.songTime.setText(R.string.defaultMusicTime);
            songNum=9000;  //9000这种状态定义为无歌曲
        }
        //保存songNum到文件
        PropertyService.saveProperties(9999,9999,songNum);
    }

    public void reNewDeleteFromList(){
        reNewDeleteFromFile();
    }



    //给player初始化歌曲资源
    public void start() {//给player初始化歌曲资源
        try {
            player.reset(); //重置多媒体
            String dataSource = musicList.get(songNum);//得到当前播放音乐的路径
            songNamep=(songNum+1)+"."+getSongName(dataSource);//截取歌名,显示在底部
            player.setDataSource(dataSource);//为多媒体对象设置播放路径
            player.prepare();//准备播放
            //通知主线程重绘listview
            Activity_Main.reDrawListView(songNum);
        } catch (Exception e) {//不需要输出错误信息，用户也看不到
        }
    }

    //获取音乐名称
    private String getSongName(String dataSource) {
        File file = new File(dataSource);//假设为D:\\mm.mp3
        String songName = file.getName();//name=mm.mp3
        return songName;
    }

    //给listviewlistener定义的方法，用于用户随机点击播放歌曲列表listview里的条目
    public void listviewClickPlay(int position){
        songNum=position;
        start();
        player.start();
        //设置底部播放按键的图标为播放状态
        Activity_Main.setPauseIcon();
        PropertyService.saveProperties(9999, 9999, songNum);
    }

    //指定播放模式的入口
    public void nextSong(int mode){
        //必须要放在switch前面，因为nextAllToEnd里面有setPlayIcon的代码
        Activity_Main.setPauseIcon();
        switch (mode){
            case 0://定义为随机播放
                nextRandom();
                break;
            case 1://定义为单曲循环
                nextSingleLoop();
                break;
            case 2://定义为全部循环
                nextAllLoop();
                break;
            case 3://定义为按顺序播放至最后一首结束
                nextAllToEnd();
                break;
        }
    }
    //下一首全部循环-ok
    public void nextAllLoop() {
        songNum = (songNum==musicList.size()-1) ? 0 : songNum + 1;
        start();
        player.start();
        beforeCallisPlaying=true;  //只要一播放，则将其置为true
        PropertyService.saveProperties(9999, 9999, songNum);
    }
    //下一首按顺序播放至最后一首停止----ok
    public void nextAllToEnd() {
       if(songNum<(musicList.size()-1)){ //如果未播放至最后一首，则一直往下一首播放
           songNum = songNum + 1;
           start();
           player.start();
           beforeCallisPlaying=true;  //只要一播放，则将其置为true
       }else{//否则，播放停止在最后一首
           songNum=(musicList.size()-1);
           //设置线程标志位，退出线程
           MusicHandler.MusicThreadStatus=false;
           //快速将播放状态置为停止
           Activity_Main.playerStatus.setText(R.string.isStop);
           //快速将进度条置0
           Activity_Main.seekBar.setProgress(0);
           //快速将歌曲时间置0
           Activity_Main.mch.setSongTime(0,player.getDuration());
           //停止播放，并关闭线程
           stop();
       }
        PropertyService.saveProperties(9999, 9999, songNum);
    }
    //下一首随机---ok
    public void nextRandom(){
        //获取随机数
        songNum=Util_Tool.getRealRandomNum(musicList.size(),randomList);
        start();
        player.start();
        beforeCallisPlaying=true;  //只要一播放，则将其置为true
        PropertyService.saveProperties(9999, 9999, songNum);
    }
    //下一首单曲循环--ok
    public void nextSingleLoop(){
        start();
        player.start();
        beforeCallisPlaying=true;  //只要一播放，则将其置为true
    }
    //上一首
    public void last() {
        songNum = songNum==0 ? (musicList.size()-1) : songNum - 1;
        start();
        player.start();
        beforeCallisPlaying=true;  //只要一播放，则将其置为true
        PropertyService.saveProperties(9999, 9999, songNum);
    }
    //播放/暂停
    public void playOrpause() {
        if (player.isPlaying()) {
            //设置底部播放按键的图标为暂停状态
            Activity_Main.setPlayIcon();
            player.pause();
            beforeCallisPlaying=false;  //如果未处于播放状态，则将其置为false
        }else{
            //设置底部播放按键的图标为播放状态
            Activity_Main.setPauseIcon();
            player.start();
            beforeCallisPlaying=true;  //只要一播放，则将其置为true
        }
    }
    //停止播放后再加载资源
    public void stop() {
        player.stop();
        start();//点击停止后系统释放了歌曲资源，再点击 播放/暂停 按键时需要重新加载资源才能播放
        beforeCallisPlaying=false;  //在停止状态，一定记得将其置为false
        //设置底部播放按键的图标为暂停状态
        Activity_Main.setPlayIcon();
    }
}
