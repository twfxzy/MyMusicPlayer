package bcg.mymusicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读写 播放器 配置 参数.
 */
public class MusicListService {
    //获取外部SD卡
    static File defaultSDCard= Environment.getExternalStorageDirectory();
    //获取默认文件夹下的musiclist
    static String fullFilePath=defaultSDCard+"/Android/data/com.BCGroup.music/bcmusic.list";

    //将musiclist保存至文件
    static void saveMusiclist(List<String> musicList) {
        String item=null;
        try {
            File stringToFile = new File(fullFilePath);
            //如果上级目录不存在，则创建
            if(!stringToFile.getParentFile().exists()){
                stringToFile.getParentFile().mkdirs();
            }

            //如果文件不存在，则新建。模式为覆盖，不追加
            FileWriter fw=new FileWriter(stringToFile,false);
            BufferedWriter bfw=new BufferedWriter(fw);

            //将list数组写入文件
            for (int i = 0; i < musicList.size() ; i++) {
                item=musicList.get(i)+'\n';  //加入换行标志
                bfw.write(item);
            }
            //刷新、关闭流
            bfw.flush();
            fw.flush();
            bfw.close();
            fw.close();

        } catch (Exception e) { }
    }

    //从文件中读取musiclist，如果文件不存在
    static List<String> loadMusicList() {
        List<String> musicList = new ArrayList<>();
        File musicListFile=new File(fullFilePath);
        if (!musicListFile.exists()) {//如果文件不存在,则默认读取外部sd卡的数据
            try {//读取外部SD卡数据
                if (defaultSDCard.listFiles(new MusicFilter()).length > 0) {
                    for (File file : defaultSDCard.listFiles(new MusicFilter())) {
                        musicList.add(file.getAbsolutePath());//此时的musiclist是文件的绝对路径，而不仅是文件名
                    }
                }
            }catch (Exception e) {//如果sd卡都不存在呢？
            }
        }else{//如果文件存在,则读取
            String temp=null;
            try {
                FileReader fr=new FileReader(fullFilePath);
                BufferedReader bfr=new BufferedReader(fr);
                while((temp=bfr.readLine()) != null){
                    musicList.add(temp);
                }
                bfr.close();
                fr.close();
            } catch (Exception e) {//如果文件存在，但读取出错，则仍旧读取外部SD卡数据
                //读取外部SD卡数据
                if (defaultSDCard.listFiles(new MusicFilter()).length > 0) {
                    for (File file : defaultSDCard.listFiles(new MusicFilter())) {
                        musicList.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return musicList;
    }
}