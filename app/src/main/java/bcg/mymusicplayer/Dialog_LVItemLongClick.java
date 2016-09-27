package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/12.
 */
public class Dialog_LVItemLongClick extends AlertDialog implements View.OnClickListener {
    private Context c;
    private TextView deleteFromSource,deleteFromList,changeFileName,setOrder,songInfo;
    protected Dialog_LVItemLongClick(Context context) {
        super(context);
        this.c=context;
    }
    static int songItemPosition;//获取当前长按的item在列表中的位置，便于下一步处理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c__listview_longclick);
        initComponent();//初始化组件
        //设置监听
        deleteFromSource.setOnClickListener(this);
        deleteFromList.setOnClickListener(this);
        changeFileName.setOnClickListener(this);
        setOrder.setOnClickListener(this);
        songInfo.setOnClickListener(this);
    }
    //初始化组件
    public void initComponent(){
        deleteFromSource= (TextView) findViewById(R.id.deleteFromSource);
        deleteFromList= (TextView) findViewById(R.id.deleteFromList);
        changeFileName= (TextView) findViewById(R.id.changeFileName);
        setOrder= (TextView) findViewById(R.id.setOrder);
        songInfo= (TextView) findViewById(R.id.songInfo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.deleteFromSource:{//删除源文件
                Dialog_LVItemLongClick_deleteFromFile deleteFromFile=new Dialog_LVItemLongClick_deleteFromFile(c);
                deleteFromFile.songItemPosition=songItemPosition;
                deleteFromFile.show();
                break;
            }
            case R.id.deleteFromList:{//从列表删除
                Dialog_LVItemLongClick_deleteFromList deleteFromListDialog= new Dialog_LVItemLongClick_deleteFromList(c);
                deleteFromListDialog.songItemPosition=songItemPosition;
                deleteFromListDialog.show();
                break;
            }
            case R.id.changeFileName:{//修 改 名 称
                Dialog_LVItemLongClick_changeName changeNameDialog=new Dialog_LVItemLongClick_changeName(c);
                changeNameDialog.songItemPosition=songItemPosition;
                changeNameDialog.show();
                break;
            }
            case R.id.setOrder:{//设 置 排 序
                Dialog_LVItemLongClick_changePosition changePositionDialog= new Dialog_LVItemLongClick_changePosition(c);
                changePositionDialog.songItemPosition=songItemPosition;
                changePositionDialog.show();
                break;
            }
            case R.id.songInfo:{//详 细 信 息
                Dialog_LVItemLongClick_showMediaInfo mediaInfo=new Dialog_LVItemLongClick_showMediaInfo(c);
                mediaInfo.songItemPosition=songItemPosition;
                mediaInfo.show();
                break;
            }
        }
        dismiss();
    }
}
