package bcg.mymusicplayer;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Listener_PhoneState extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:{//等待接电话
                if(MusicHandler.musicService.player.isPlaying()){//如果播放器正在播放，则暂停
                    MusicHandler.musicService.player.pause();//暂停播放器
                }
                break;
            }
            case TelephonyManager.CALL_STATE_IDLE:{//电话空闲状态，比如挂断电话之后
                if(MusicHandler.musicService.beforeCallisPlaying==true){//如果来电之前播放器正在播放，则电话结束后依旧播放
                    MusicHandler.musicService.player.start();//开始播放
                }
                break;
            }
            case TelephonyManager.CALL_STATE_OFFHOOK:{//通话中
                if(MusicHandler.musicService.player.isPlaying()){//如果播放器正在播放，则暂停
                    MusicHandler.musicService.player.pause();//暂停播放器
                }
                break;
            }
        }
        super.onCallStateChanged(state, incomingNumber);
    }
}
