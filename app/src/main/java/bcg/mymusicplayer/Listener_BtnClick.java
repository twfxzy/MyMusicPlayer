package bcg.mymusicplayer;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/3.
 */
public class Listener_BtnClick implements View.OnClickListener {
    private Context c;
    //之所以带context参数，是因为可以调用Toast输出
    public Listener_BtnClick(Context context){
        this.c=context;
    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStop:{  // 停止 按键功能
                //设置线程标志位，退出线程
                MusicHandler.MusicThreadStatus=false;
                //快速将播放状态置为停止
                Activity_Main.playerStatus.setText(R.string.isStop);
                //快速将进度条置0
                Activity_Main.seekBar.setProgress(0);
                //快速将歌曲时间置0
                Activity_Main.mch.setSongTime(0,MusicHandler.musicService.player.getDuration());
                //停止播放，并关闭线程
                MusicHandler.musicService.stop();
                break;
            }
            case R.id.btnLast: {  // 上一首 按键功能
                if(MusicService.songNum==9000){
                    //  提示用户无歌曲
                    Toast.makeText(c,c.getString(R.string.noMusicToast),Toast.LENGTH_SHORT).show();
                }else{
                //如果线程停止了，则开启新线程
                    btnStartNewThread();
                    Activity_Main.playerStatus.setText(R.string.isPlay);
                    if(MusicService.PlayMode==0){//如果是随机播放状态点击上一首，则随机选择下一首
                        MusicHandler.musicService.nextSong(0);
                    }else{
                        MusicHandler.musicService.last();
                    }
                }
                break;
            }
            case R.id.btnStartOrPause: { // 播放/暂停 按键功能
                if(MusicService.songNum==9000){
                    //  提示用户无歌曲
                    Toast.makeText(c,c.getString(R.string.noMusicToast),Toast.LENGTH_SHORT).show();
                }else{
                    //设置播放状态信息
                    if (MusicHandler.musicService.player.isPlaying()){
                        Activity_Main.playerStatus.setText(R.string.isPause);
                    }else{
                        Activity_Main.playerStatus.setText(R.string.isPlay);
                    }
                    btnStartNewThread();//判断是否重开线程
                    MusicHandler.musicService.playOrpause();
                }
                break;
            }
            case R.id.btnNext: {  // 下一首 按键功能
                if(MusicService.songNum==9000){
                    //  提示用户无歌曲
                    Toast.makeText(c,c.getString(R.string.noMusicToast),Toast.LENGTH_SHORT).show();
                }else{//如果线程停止了，则开启新线程
                    btnStartNewThread();
                    Activity_Main.playerStatus.setText(R.string.isPlay);
                    //如果是顺序播放至最后一首，用户点击了下一首按键，则跳转至第一首
                    if((MusicService.PlayMode==1)||(MusicService.PlayMode==3)){
                        MusicHandler.musicService.nextSong(2); //
                    }else{
                        MusicHandler.musicService.nextSong(MusicService.PlayMode);
                    }
                }
                break;
            }
            case R.id.btnSettings:{//设置 按键的监听
                new Dialog_SettingsBtn(c).show();//对话框的位置在xml布局文件里已经定义了
            }
        }
    }


    //在停止状态下，点击播放、上首、下首按键，创建一个新线程
    private void btnStartNewThread(){
        if(MusicHandler.MusicThreadStatus==false){
            MusicHandler.MusicThreadStatus=true;
            Activity_Main.mch.startNewThread();
        }
    }

}
