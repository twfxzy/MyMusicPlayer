package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

/**
 * Created by Administrator on 2016/8/13.
 */
public class Dialog_LVItemLongClick_changePosition extends AlertDialog implements View.OnClickListener {
    private ImageButton changePositionConfirm,changePositionCancel;
    private TextView positionRange;
    private EditText setPosition;
    public int songItemPosition;//获取当前长按的item在列表中的位置，便于下一步处理
    private Context context;

    protected Dialog_LVItemLongClick_changePosition(Context context) {
        super(context);
        this.context=context;
    }

    public void initComponent(){
        changePositionConfirm= (ImageButton) findViewById(R.id.changePositionConfirm);
        changePositionCancel= (ImageButton) findViewById(R.id.changePositionCancel);
        positionRange= (TextView) findViewById(R.id.positionRange);
        setPosition= (EditText) findViewById(R.id.setPosition);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_change_song_position);
        //初始化组件
        initComponent();
        //设置监听
        changePositionConfirm.setOnClickListener(this);
        changePositionCancel.setOnClickListener(this);
        //给editText增加文本输入的监听,避免输入0开头的
        setPosition.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {//输入后检查若是0开头，则清除掉
                String text = s.toString();
                if (text.equals("0")  && text.length() == 1) { s.clear();}
            }
        });

        //将当前值显示出来
        setPosition.setText((songItemPosition+1)+"");
        //将光标移至文本末尾
        setPosition.setSelection(setPosition.getText().length());
        //设置取值范围
        positionRange.setText("范围[1-"+MusicService.musicList.size()+"]");

        //允许EditText调用输入法
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override//处理点击事件
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.changePositionConfirm:{//确认键
                String str=setPosition.getText().toString();//获取输入值
                if( str.length() != 0 ){ //输入不为空的时候
                    int num=Integer.parseInt(str);
                    if(num <= MusicService.musicList.size()){//筛选条件通过后，则更换位置
                        if((num-1)==songItemPosition){ //如果设置的位置与旧位置相同，则不操作
                        }//如果设置的位置或者长按的位置与正在播放的位置相等，则将播放器置为初始状态
                        else{
                            //互换位置
                            String itemPressed=MusicService.musicList.get(songItemPosition);
                            String itemChangeTo=MusicService.musicList.get(num-1);
                            MusicService.musicList.set(songItemPosition,itemChangeTo);
                            MusicService.musicList.set(num-1,itemPressed);

                            //将新列表存入文件
                            MusicListService.saveMusiclist(MusicService.musicList);

                            //-------以下3步非常重要-----重绘全部listview
                            Activity_Main.musicListMap.clear();//清除旧数据源
                            Activity_Main.musicListMap.addAll(MusicHandler.getMusicList());//更新数据源
                            Activity_Main.listView.setAdapter(Activity_Main.adapter);//重新设置适配器

                            //重绘选定item的底色
                            if(MusicService.songNum==(num-1) || MusicService.songNum==songItemPosition){
                                int temp= MusicService.songNum==(num-1)?songItemPosition:(num-1);
                                MusicHandler.musicService.reNewChangePosition(temp);
                            }
                        }
                    }else{
                        Toast.makeText(context,"您的输入有误..",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.changePositionCancel:{//取消键
                break;
            }
        }
        dismiss();
    }
}
