package bcg.mymusicplayer;

import android.widget.SeekBar;

public class Listener_SeekBar implements SeekBar.OnSeekBarChangeListener {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progess, boolean fromUser) {}

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int mMax = MusicHandler.musicService.player.getDuration();
        int position =(mMax/100)* Activity_Main.seekBar.getProgress();
        //快速更新时间信息
        MusicHandler.setSongTime(position,mMax);
        //将player按进度条的比例进行定位
        MusicHandler.musicService.player.seekTo(position);
    }
}
