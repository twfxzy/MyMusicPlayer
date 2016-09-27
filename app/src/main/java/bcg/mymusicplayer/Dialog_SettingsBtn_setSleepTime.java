package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Dialog_SettingsBtn_setSleepTime extends AlertDialog implements View.OnClickListener {
    private Context context;
    private TextView set10min,set20min,cancelSleep;
    static TextView sleepTimeLeft;
    private EditText setSleepTime;
    private ImageButton setSleepTimeConfirm, setSleepTimeCancel;
    static int sleepTime=-1;//静态的睡眠时间
    static Timer timer ; //静态定时器
    static Handler sleepTimerHandler;//接收器
//    static TimerTask task;//timer任务

    //构造方法
    public Dialog_SettingsBtn_setSleepTime(Context context) {
        super(context);
        this.context=context;
    }

    //对话框入口
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_set_sleep_time);
        //初始化组件
        initComponent();
        //初始化监听
        setListener();
        //还得有显示睡眠时间的处理方法
        initTimerTask();
        //允许EditText调用输入法
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    //初始化组件
    public void initComponent(){
        sleepTimeLeft= (TextView) findViewById(R.id.sleepTimeLeft);
        set10min= (TextView) findViewById(R.id.set10min);
        set20min= (TextView) findViewById(R.id.set20min);
        cancelSleep= (TextView) findViewById(R.id.cancelSleep);
        setSleepTime= (EditText) findViewById(R.id.setSleepTime);
        setSleepTimeConfirm= (ImageButton) findViewById(R.id.setSleepTimeConfirm);
        setSleepTimeCancel= (ImageButton) findViewById(R.id.setSleepTimeCancel);
    }

    //设置监听
    private void setListener(){
        set10min.setOnClickListener(this);
        set20min.setOnClickListener(this);
        cancelSleep.setOnClickListener(this);

        //给editText增加文本输入的监听,避免输入0开头的
        setSleepTime.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {//输入后检查若是0开头，则清除掉
                String text = s.toString();
                if (text.equals("0")  && text.length() == 1) { s.clear();}
            }
        });

        setSleepTimeConfirm.setOnClickListener(this);
        setSleepTimeCancel.setOnClickListener(this);
    }

    //初始化定时器任务
    private void initTimerTask(){
        if(sleepTime == -1){//-1表示当前无timertask
            //timer一旦cancel后必须new一个，才能使用
            timer = new Timer();
            //初始化Handler
            sleepTimerHandler = new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    //按秒更新睡眠时间
                    sleepTimeLeft.setText("  "+Util_Tool.IntegerToTime(sleepTime)+" 睡眠");
                    //定时器结束，退出程序
                    if(sleepTime==0){
                        //退出应用，释放资源
                        ExitSysApplication.getInstance().exit();
                    }
                }
            };
        }
    }

    //开始定时器
    private void startSleepTimer(int time){
        timer.purge();//移除定时器的任务
        sleepTime=time;
        timer.schedule(
                new TimerTask() {// 每次purge后，TimerTask就被丢弃，所以，比需重新new一个TimerTask
                    public void run() {
                        sleepTime--;  //在非显示的状态，要继续减sleepTime
                        sleepTimerHandler.sendMessage(new Message());
                    }
                },0,1000); //按1秒的间隔刷新
    }

    //完全取消、结束定时器
    private void stopSleepTimer(){
        timer.cancel();
    }

    @Override//处理点击事件
    public void onClick(View view) {//点击对话框后消失
        switch (view.getId()){
            case R.id.set10min:{//设置为10分钟
                startSleepTimer(600);//启动定时器
                Toast.makeText(context,"10分钟后睡眠..",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.set20min:{//设置为20分钟
                startSleepTimer(1200);//启动定时器
                Toast.makeText(context,"20分钟后睡眠..",Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.cancelSleep:{//取消睡眠
                sleepTime=-1;
                stopSleepTimer();
                Toast.makeText(context,"已取消睡眠..",Toast.LENGTH_SHORT).show();
                sleepTimeLeft.setText("未设置睡眠");
                break;
            }

            case R.id.setSleepTimeConfirm:{//确认键
                String str=setSleepTime.getText().toString();//获取输入值
                if( str.length() != 0 ){ //输入不为空的时候
                    int sleepTimeTemp=Integer.parseInt(str); //转换字符
                    if(sleepTimeTemp<=60){//输入数值小于60时
                        startSleepTimer(sleepTimeTemp*60);//启动定时器
                        Toast.makeText(context,sleepTimeTemp+"分钟后睡眠..",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"您的输入有误..",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.setSleepTimeCancel:{//取消键
                break;
            }
        }
        dismiss();
    }
}
