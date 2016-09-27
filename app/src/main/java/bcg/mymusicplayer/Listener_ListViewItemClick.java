package bcg.mymusicplayer;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Administrator on 2016/8/4.
 */
public class Listener_ListViewItemClick implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long idd) {
        //如果刚进入软件就点击item，则新开一个线程
        if(MusicHandler.MusicThreadStatus==false){
            MusicHandler.MusicThreadStatus=true;
            Activity_Main.mch.startNewThread();
        }
        //通知主线程重绘listview
        Activity_Main.reDrawListView(position);
        //播放当前选中的歌曲
        Activity_Main.playerStatus.setText(R.string.isPlay);
        MusicHandler.musicService.listviewClickPlay(position);

    }
}
