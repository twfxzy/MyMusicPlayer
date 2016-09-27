package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//AlertDialog可以无标题，Dialog默认显示标题，哪怕空着
public class Dialog_SettingsBtn_FileDirChoose extends AlertDialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private dirListAdapter adapter;
    private ListView listDir;
    private List<String> list=new ArrayList<>();
    private TextView currentDir,returnToFatherDir,currentDirSongNum;
    private Context context;
    static File musicDir=null;

    protected Dialog_SettingsBtn_FileDirChoose(Context cont) {
        super(cont);// 为啥super一定要在第一个呢？
        this.context=cont;
    }

    //初始化组件
    public void initComponent(){
        listDir= (ListView) findViewById(R.id.listDir);
        currentDir= (TextView) findViewById(R.id.currentDir);
        returnToFatherDir= (TextView) findViewById(R.id.returnToFatherDir);
        currentDirSongNum= (TextView) findViewById(R.id.currentDirSongNum);
    }

    @Override//入口程序
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_filedir_chooser);
        initComponent();//初始化组件
        //设置监听
        currentDir.setOnClickListener(this);
        returnToFatherDir.setOnClickListener(this);
        listDir.setOnItemClickListener(this);

        //给list填充数据
        musicDir=new File(Activity_Main.propertyService.loadProperties().get("CurrentSongDir").toString());

        try{
            GetCurrentDirList(musicDir);//内部已实现排序
        } catch(Exception e){
            musicDir=MusicListService.defaultSDCard;
            GetCurrentDirList(musicDir);
        }
        PropertyService.saveCurrentSongDir(musicDir.toString());
        //配置列表视图
        adapter=new dirListAdapter(getContext(),R.layout.b_filedir_item,list);
        listDir.setAdapter(adapter);
    }

    @Override//监听返回上一级和确定目录两个textview
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.currentDir: { //点击了当前目录，则确定播放该目录
                dismiss();//退出当前对话框

                //重新初始化音乐文件夹，并自动重绘了歌曲的listview
                MusicHandler.musicService.dirToMusicList(musicDir);
                //保存当前目录
                PropertyService.saveCurrentSongDir(musicDir.toString());
                Log.i("MusicService","位置8"+musicDir.toString());

                //清除列表的旧数据，重新生成列表，并加载到listview
                //-------以下3步非常重要------------------
                Activity_Main.musicListMap.clear();
                Activity_Main.musicListMap.addAll(MusicHandler.getMusicList());
                Activity_Main.listView.setAdapter(Activity_Main.adapter);

                //+2016.09.25 bug--修正新增列表无法初始化播放状态
                //初始化播放，播放/暂停键置为暂停态
                Activity_Main.setPlayIcon();
                MusicHandler.musicService.beforeCallisPlaying=false;
                if(Activity_Main.musicListMap.size()>0){//如果新列表不为空
                    MusicHandler.musicService.start();  //则预加载歌曲列表
                }

                //给用户做提示
                Toast.makeText(this.context, "您选择了:" + musicDir.getAbsolutePath()
                        +'\n'+"该目录有 "+MusicHandler.getMusicList().size()+" 首音乐", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.returnToFatherDir : {//点击返回上一级目录
                musicDir = ReturnToFatherDir(musicDir); //更新musicDir
                GetCurrentDirList(musicDir);//更新list
                listDir.invalidateViews();//重绘listDir
                break;
            }
        }
    }

    @Override  //点击列表中各项目的监听
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textViewTemp= (TextView) view;
        String fileName=textViewTemp.getText().toString();

        //将listview里面的item字符串格式化
        fileName=fileName.substring(4);  //减去 "目录: "这4个长度
        fileName=removeMusicNumberFromDir(fileName);//如果有括号，则去除

        //将格式化后的目录名传递给musicDir
        File fileTemp=new File(currentDir.getText()+"/"+fileName);
        musicDir=fileTemp;
        GetCurrentDirList(fileTemp);//该方法已内部实现排序
        listDir.invalidateViews();//重绘listDir
    }

    //获取目录下的所有--目录--
    public List<String> GetCurrentDirList(File dir){//dir是当前的根目录
        try{
            if(dir.listFiles().length>0){//如果目录下有目录或文件
                //下面3条语句放在dir.listFiles()后面，是为了在try正常时执行，如果try异常，这3条语句不执行
                currentDirSongNum.setText("-"+dir.listFiles(new MusicFilter()).length+"首-");//右下角显示歌曲数量
                list.clear();//清除list原有的内容
                returnToFatherDir.setText("返回上级目录..");

                //获取目录下的目录、文件的名称，音乐数量
                for(File dirTemp:dir.listFiles()){
                    if(dirTemp.isDirectory()){ //限定仅获取目录,同时显示该目录下有多少音乐
                        list.add("目录: "+getDirWithMusicNumber(dirTemp));
//                        list.add("目录: "+dirTemp.getName());
                    }
                }

                if(list.size()==0){  //如果目录下无子目录
                    returnToFatherDir.setText("返回上级目录(当前目录无子目录)");
                }
            }else{//如果目录下无目录和文件
                list.clear();//清除list原有的内容
                currentDirSongNum.setText("-0首-");//右下角显示歌曲数量
                returnToFatherDir.setText("返回上级目录(当前目录无内容)");
            }
        }catch(Exception e){
            Toast.makeText(context,"权限不足，不能进入该目录",Toast.LENGTH_SHORT).show();
            dir=ReturnToFatherDir(dir);//直接返回父目录
        }
        musicDir=dir;
        //修改顶部显示的绝对路径
        currentDir.setText(dir.getAbsolutePath());
        //忽略大小写进行排序
        Util_Tool.sortIgnoreCase(list);
        return list;
    }

    //计算目录列表里每个item目录下的音乐数量，并返回
    private String getDirWithMusicNumber(File dir){
        String dirName=dir.getName();//先获取目录名称
        try{//为啥用try呢？ 因为有的文件夹权限不够，无法进入遍历计算音乐数目。
            // 如果不用try，那么返回的dir就可能是null
            if(dir.listFiles(new MusicFilter()).length >0 ){//如果目录下有音乐文件
                dirName=dirName+"("+dir.listFiles(new MusicFilter()).length+")";
            }
        }catch (Exception e){
        }
        return dirName;
    }

    //去除如 music(12) 这样的字符串中括号 (12) 的部分
    private String removeMusicNumberFromDir(String fileName){//判断目录最后的括号是系统生成的还是用户自己添加的
        int lastIndexLeft=fileName.lastIndexOf("(");
        int lastIndexRight=fileName.lastIndexOf(")");

        //如果最后一个是右括号")",且存在左括号"("
        if(lastIndexRight == (fileName.length()-1) && (lastIndexLeft != -1)){
            if(lastIndexRight > (lastIndexLeft+1)){//如果最后一对括号中间有内容
                String temp=fileName.substring(lastIndexLeft+1,fileName.lastIndexOf(")"));
                try{
                    //转换成数字测试是否异常，若无异常，则表明是应用自身生成的括号；否则是用户的文件夹命名中有括号(11)
                    if(Integer.parseInt(temp)>0){}
                    fileName=fileName.substring(0,lastIndexLeft);
                }catch (Exception e){
                    Toast.makeText(context,"目录名不能以\"(数字)\"这样的括号结尾",Toast.LENGTH_SHORT).show();
                }
            }
        }
        return fileName;
    }

    //获取上级目录的方法-ok
    public File ReturnToFatherDir(File dir){
        //右下角显示歌曲数量
        if(dir.getAbsolutePath().length()==1){   //长度为1，表示等于 '/' 时表示已经在根目录了
            Toast.makeText(context,"'/'已是系统根目录",Toast.LENGTH_SHORT).show();
        }else{
            dir=new File(dir.getParent());
        }
        musicDir=dir;
        return dir;
    }

    //自定义视图配置器，做缓存优化
    class dirListAdapter extends ArrayAdapter<String>{
        public dirListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }
        @Override //缓存优化
        public View getView(int position, View convertView, ViewGroup parent) {
            DirViewHolder dirViewHolder=new DirViewHolder();

            if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.b_filedir_item,null);
            //打标签，绑定id
            dirViewHolder.dirtv= (TextView) convertView.findViewById(R.id.dirItem);
            convertView.setTag(dirViewHolder);
        }else{
                dirViewHolder= (DirViewHolder) convertView.getTag();
                dirViewHolder.dirtv.setText(list.get(position));
        }

            return super.getView(position, convertView, parent);
        }
    }

    //dir列表item类
    class DirViewHolder{
        TextView dirtv;
    }

}
