package bcg.mymusicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 读写 播放器 配置 参数.
 */
public class PropertyService {
    static Context context;
    public PropertyService(Context context){
        this.context=context;
    }

    //保存配置参数
    static void saveProperties(int backGroundId,int playMode,int itemPosition ) {
        // 获取SharedPreferences对象, 路径在 /data/data/shared_pref/bcmusic_properties.xml, 文件模式为MODE_PRIVATE
        SharedPreferences preferences = context.getSharedPreferences("BCGroup_music_properties", Context.MODE_PRIVATE);
        // 获取编辑器
        SharedPreferences.Editor editor = preferences.edit();
        // 通过editor进行设置, 9999表示要屏蔽的操作
        if(backGroundId != 9999){//背景图片编号
            editor.putInt("backGroundId", backGroundId);
        }
        if(playMode != 9999) {//播放模式
            editor.putInt("playMode", playMode);
        }
        if(itemPosition != 9999) {//播放列表中条目的位置
            editor.putInt("itemPosition", itemPosition);
        }
        // 提交修改, 将数据写入文件
        editor.commit();
    }

    //保存当前歌曲路径
    static void saveCurrentSongDir(String CurrentSongDir ) {
        // 获取SharedPreferences对象, 路径在 /data/data/shared_pref/bcmusic_properties.xml, 文件模式为MODE_PRIVATE
        SharedPreferences preferences = context.getSharedPreferences("BCGroup_music_properties", Context.MODE_PRIVATE);
        // 获取编辑器
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CurrentSongDir", CurrentSongDir);
        // 提交修改, 将数据写入文件
        editor.commit();
    }


    //读取配置参数
    static Map<String, Object> loadProperties() {
        SharedPreferences preferences = context.getSharedPreferences("BCGroup_music_properties", Context.MODE_PRIVATE);
        //读取配置文件之前
        int backGroundId = preferences.getInt("backGroundId", 0);//背景图片编号
        int playMode = preferences.getInt("playMode", 3);//播放模式
        int itemPosition = preferences.getInt("itemPosition", 0);//播放列表中条目的位置
        String CurrentSongDir=preferences.getString("CurrentSongDir",MusicListService.defaultSDCard.toString());

        Map<String, Object> map = new HashMap<>();
        map.put("backGroundId", backGroundId);
        map.put("playMode", playMode);
        map.put("itemPosition", itemPosition);
        map.put("CurrentSongDir",CurrentSongDir);

        return map;
    }
}
