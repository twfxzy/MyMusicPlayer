package bcg.mymusicplayer;

import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Administrator on 2016/8/7.
 */
public class Listener_ListViewItemLongClick implements AdapterView.OnItemLongClickListener {
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        new Dialog_LVItemLongClick(parent.getContext()).show();
        Dialog_LVItemLongClick alertDialogTemp=new Dialog_LVItemLongClick(view.getContext());
        alertDialogTemp.songItemPosition=position; //将item的位置传递给Dialog_LVItemLongClick
        alertDialogTemp.show();
        return true;//返回true，则长按item条目后，仅响应长按事件
        //return false;//返回false，则长按item条目后，响应长按事件，松开item时还响应单击事件
    }
}
