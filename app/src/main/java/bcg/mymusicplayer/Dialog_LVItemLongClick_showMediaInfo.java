package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;

import android.database.Cursor;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.math.BigDecimal;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dialog_LVItemLongClick_showMediaInfo extends AlertDialog implements View.OnClickListener {

    private TextView mediaInfoTitle,mediaInfoDetail;
    private Context context;
    private String fileNameWithSuffix,filePathWithNoTail;
    public int songItemPosition;//获取当前长按的item在列表中的位置，便于下一步处理

    //构造方法
    public Dialog_LVItemLongClick_showMediaInfo(Context context) {
        super(context);
        this.context=context;
    }

    //初始化组件
    public void initComponent(){
        mediaInfoTitle= (TextView) findViewById(R.id.mediaInfoTitle);
        mediaInfoDetail= (TextView) findViewById(R.id.mediaInfoDetail);
    }

    //对话框入口
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_show_media_info);
        //初始化组件
        initComponent();
        //设置监听
        mediaInfoTitle.setOnClickListener(this);
        mediaInfoDetail.setOnClickListener(this);

        //获取带后缀的文件名
        fileNameWithSuffix=Util_Tool.getFileNameWithSuffix(MusicService.musicList.get(songItemPosition));
        //获取文件路径  ，如 /sdcard/
        filePathWithNoTail=Util_Tool.getFilePathWithNoTail(MusicService.musicList.get(songItemPosition));
        //显示歌曲名称头
        mediaInfoTitle.setText("About "+fileNameWithSuffix);
        mediaInfoDetail.setText(getMediaInfo()); //此处不能删除
    }

    //通过mediametadataretriver获取歌曲详细信息,可以动态获取文件信息，但信息不全
    public String getMediaInfo(){
        //要返回的歌曲信息字符串
        String stringToSongInfo="";
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String fullFilePathName=filePathWithNoTail+"/"+fileNameWithSuffix;
        try{
            mmr.setDataSource(fullFilePathName);
            //获取媒体内容输出项
            String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            String mimeType = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            //释放内存
            mmr.release();

            //获取媒体文件大小
            File getFileSize=new File(fullFilePathName);
            Long size=getFileSize.length();

            //整合所有的内容输出到字符串
            stringToSongInfo=
                             "歌手: "+artist+'\n'
                            +"标题: "+title+'\n'
                            +"专辑: "+album+'\n'
                            +"发行: "+year+'\n'
                            +"大小: "+Util_Tool.Bytes2Mb(size)+'\n'
                            +"时长: "+Util_Tool.duration2String(duration)+'\n'
                            +"码率: "+Util_Tool.bitRate2String(bitrate)+'\n'
                            +"类型: "+mimeType+'\n'
                            +"路径: "+filePathWithNoTail;
        } catch(Exception e){
            Toast.makeText(context, "查询媒体信息失败，请检查后重试", Toast.LENGTH_SHORT).show();
        }
        return stringToSongInfo;
    }

    //通过media数据库获取歌曲详细信息，这种方式不能获取数据库里没有的信息
//    public String getMediaInfo(){
//        //要返回的歌曲信息字符串
//        String stringToSongInfo="";
//        //查询指定音乐文件信息的筛选条件
//        String querySelection=MediaStore.Audio.Media.DISPLAY_NAME+"= '"+fileNameWithSuffix+"'";
//       try{
//           //获取内容解释器
//           ContentResolver mResolver = context.getContentResolver();
//           //新建查询游标
//           Cursor cursor =mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, querySelection , null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);;
//           //对查询结果进行整合输出
//           if (cursor.moveToFirst()) {
//               //在数据库里的ID
//               int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
//               String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
//               String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
//               String TRACK = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
//               String year = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR));
//               long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
//               String MIME_TYPE = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
//
//               //整合内容输出字符串
//               stringToSongInfo=
//                       "    ID: "+id+'\n'
//                       +"歌手: "+artist+'\n'
//                       +"专辑: "+album+'\n'
//                       +"音轨: "+TRACK+'\n'
//                       +"发行: "+year+'\n'
//                       +"大小: "+Util_Tool.Bytes2Mb(size)+'\n'
//                       +"类型: "+MIME_TYPE+'\n'
//                       +"目录: "+filePathWithNoTail;
//           }else{
//               stringToSongInfo="未找到相关信息，请检查后重试";
//           }
//           cursor.close();//关闭连接
//       }catch(Exception e){
//           e.printStackTrace();
//           Toast.makeText(context, "查询数据库失败，请检查后重试", Toast.LENGTH_SHORT).show();
//       }
//        return stringToSongInfo;
//    }

    @Override//处理点击事件
    public void onClick(View view) {//点击对话框后消失
        switch (view.getId()){
            case R.id.mediaInfoTitle:
                break;
            case R.id.mediaInfoDetail:
                break;
        }
        dismiss();
    }
}
