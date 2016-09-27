package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/13.
 */
public class Dialog_LVItemLongClick_deleteFromList extends AlertDialog implements View.OnClickListener {

    private ImageButton deleteFileConfirm,deleteFileCancel;
    private TextView deleteFileInfo;
    public int songItemPosition;//获取当前长按的item在列表中的位置，便于下一步处理
    private Context context;
    protected Dialog_LVItemLongClick_deleteFromList(Context context) {
        super(context);
        this.context=context;
    }


    public void initComponent(){
        deleteFileConfirm= (ImageButton) findViewById(R.id.deleteFileConfirm);
        deleteFileCancel= (ImageButton) findViewById(R.id.deleteFileCancel);
        deleteFileInfo= (TextView) findViewById(R.id.deleteFileInfo);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_delete_file_confirm);
        //初始化组件
        initComponent();
        //设置监听
        deleteFileConfirm.setOnClickListener(this);
        deleteFileCancel.setOnClickListener(this);

        //设置提示标语
        deleteFileInfo.setText("确定从-歌曲列表-中删除 "+'\n' +(songItemPosition+1)+". "+
                Util_Tool.getFileNameWithSuffix(MusicService.musicList.get(songItemPosition))+" 吗?");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.deleteFileConfirm:{//确认键
                try{
                    //清除选中项
                    MusicService.musicList.remove(songItemPosition);
                    //将新列表存入文件
                    MusicListService.saveMusiclist(MusicService.musicList);

                    //更新状态
                    MusicHandler.musicService.reNewDeleteFromList();

                    //清除列表的旧数据，重新生成列表，并加载到listview
                    //-------以下3步非常重要-----重绘listview
                    Activity_Main.musicListMap.clear();//清除旧数据源
                    Activity_Main.musicListMap.addAll(MusicHandler.getMusicList());//更新数据源
                    Activity_Main.listView.setAdapter(Activity_Main.adapter);//重新设置适配器

                }catch (Exception e){
                    Toast.makeText(context,"从列表中删除歌曲失败，请检查后重试",Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case R.id.deleteFileCancel:{//取消键
                break;
            }
        }
        dismiss();
    }
}
