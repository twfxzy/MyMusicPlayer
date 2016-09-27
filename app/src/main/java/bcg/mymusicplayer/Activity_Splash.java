package bcg.mymusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class Activity_Splash extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a___splash_first_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Activity_Splash.this, Activity_Main.class);
                startActivity(i);
                finish();//启动主Activity后销毁自身
            }
        }, 1000);//显示1秒钟
    }
}
