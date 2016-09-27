package bcg.mymusicplayer;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Administrator on 2016/8/3.
 */
public class MusicFilter implements FilenameFilter {
    private String[] musicSuffix={"mp3","wav","acc","ogg","mid","flac"};
    public boolean accept(File dir, String name) {
        int tempfirst=name.lastIndexOf(".");
//        Log.i("MusicService","tempfirst="+tempfirst);
        int templast=name.length();
        String fileSuffix=name.substring(tempfirst+1,templast);//加1是因为要去除"."
//        Log.i("MusicService","fileSuffix="+fileSuffix);
        for (int i = 0; i <musicSuffix.length ; i++) {
            //tempfirst!=-1 是因为 文件夹会让tempfirst=-1，如果不隔离，那么会返回以mp3、wav等命名的文件夹
            //tempfirst!=0 是因为 .mp3文件夹会让tempfirst=0，如果不隔离，那么会返回以.mp3、.wav等命名的文件夹
            if((tempfirst > 0) && musicSuffix[i].equals(fileSuffix.toLowerCase())){
                return true;
            }
        }
        return false;
    }
}
