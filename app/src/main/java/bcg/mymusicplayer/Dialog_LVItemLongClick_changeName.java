package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2016/8/13.
 */
public class Dialog_LVItemLongClick_changeName extends AlertDialog implements View.OnClickListener {

    private ImageButton changeNameConfirm,changeNameCancel;
    private TextView fileSuffix;
    private EditText setFileName;
    public int songItemPosition;//获取当前长按的item在列表中的位置，便于下一步处理
    private Context context;
    protected Dialog_LVItemLongClick_changeName(Context context) {
        super(context);
        this.context=context;
    }

    public void initComponent(){
        changeNameConfirm= (ImageButton) findViewById(R.id.changeNameConfirm);
        changeNameCancel= (ImageButton) findViewById(R.id.changeNameCancel);
        fileSuffix= (TextView) findViewById(R.id.fileSuffix);
        setFileName= (EditText) findViewById(R.id.setFileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_change_song_name);
        initComponent();//初始化组件
        //设置监听
        changeNameConfirm.setOnClickListener(this);
        changeNameCancel.setOnClickListener(this);
        // 输出后缀名
        fileSuffix.setText(Util_Tool.getFileSuffix(MusicService.musicList.get(songItemPosition)));
        // 输出无后缀的文件名
        setFileName.setText(Util_Tool.getFileNameWithNoSuffix(MusicService.musicList.get(songItemPosition)));
        //将光标移至文本末尾
        setFileName.setSelection(setFileName.getText().length());

        //允许EditText调用输入法
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.changeNameConfirm:{//确认键
                String str = setFileName.getText().toString();
                if( str.length() != 0 ){ //输入不为空的时候,才进一步判断操作
                    String illegalChar[]={"/","\\",":","*","?","\"","<",">","|"};
                    boolean isLegal=true; //判断是否包含非法字符的标志位

                    //判断是否包含非法字符
                    for (int i = 0; i < illegalChar.length ; i++) {
                        if(str.contains(illegalChar[i])){//如果包含有非法字符，则显示警告
                            Toast.makeText(context,"文件名不能有 \\ / : * ? \" < > | 字符",Toast.LENGTH_SHORT).show();
                            isLegal=false;
                            break;//一旦检测到有非法字符，则显示警告并退出循环
                        }
                    }

                    //如果输入合法，则进一步操作
                    if(isLegal==true){//如果输入合法，则进一步操作
                        try{
                            //获取新文件名的字符串
                            String fileNameTemp=Util_Tool.getFilePath(MusicService.musicList.get(songItemPosition))
                                    +str+fileSuffix.getText();//此时不包含路径

                            //获取源文件
                            File oldFileName=new File(MusicService.musicList.get(songItemPosition));
                            //根据新文件名的字符串生成新文件名
                            File newFileName=new File(fileNameTemp);
                            //将源文件改为新的文件名
                            if(!newFileName.exists()){//如果文件不存在，则重命名
                                oldFileName.renameTo(newFileName);

                                //判断是否停止一切播放并就绪
                                MusicHandler.musicService.reNewChangeName();
                                //更新列表的文件名
                                MusicService.musicList.set(songItemPosition,newFileName+"");
                                //将新列表存入文件
                                MusicListService.saveMusiclist(MusicService.musicList);
//                                -------以下3步非常重要-----重绘listview
                                Activity_Main.musicListMap.clear();//清除旧数据源
                                Activity_Main.musicListMap.addAll(MusicHandler.getMusicList());//更新数据源
                                Activity_Main.listView.setAdapter(Activity_Main.adapter);//重新设置适配器

                            }else{//如果文件存在(就是没改名或者改的名已被其他文件占用)，提示警告
                                Toast.makeText(context,"文件已存在，请重新命名",Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            Toast.makeText(context,"修改名称失败，请检查后重试",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            }
            case R.id.changeNameCancel:{//取消键
                break;
            }
        }
        dismiss();
    }
}
