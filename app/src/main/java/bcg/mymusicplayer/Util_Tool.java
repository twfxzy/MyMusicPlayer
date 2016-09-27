package bcg.mymusicplayer;

import android.graphics.Paint;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.zip.Inflater;

/**
 * 工具类，专门用于处理数据转换等
 */
public class Util_Tool {

    //获取不带后缀的文件名
    static String getFileNameWithNoSuffix(String fileName){
        fileName=fileName.substring(fileName.lastIndexOf("/")+1, fileName.lastIndexOf("."));//+1表示除去/
        return fileName;
    }

    //获取带后缀的文件名
    static String getFileNameWithSuffix(String fileName){
        fileName=fileName.substring(fileName.lastIndexOf("/")+1, fileName.length());//+1表示除去/
        return fileName;
    }

    //获取文件的后缀名
    static String getFileSuffix(String fileName){
        fileName=fileName.substring(fileName.lastIndexOf("."), fileName.length());
        return fileName;
    }

    //获取文件路径 如 /sdcard/
    static String getFilePath(String fileName){
        fileName=fileName.substring(0,fileName.lastIndexOf("/")+1);//+1表示包含/
        return fileName;
    }

    //获取文件路径 如 /sdcard
    static String getFilePathWithNoTail(String fileName){
        fileName=fileName.substring(0,fileName.lastIndexOf("/"));//不包含/
        return fileName;
    }


    //整数转时间格式化, 如136 输出02:16
    static String IntegerToTime(int time){
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = zeroAlignedFormat(minute) + ":" + zeroAlignedFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = zeroAlignedFormat(hour) + ":" + zeroAlignedFormat(minute)
                        + ":" + zeroAlignedFormat(second);
            }
        }
        return timeStr;
    }

    //数据补0格式化，返回如08,12,00等
    static String zeroAlignedFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    //将字符串形式的比特率转化为 KB/秒单位
    static String bitRate2String(String request){
        try{
            int temp=Integer.parseInt(request);
            temp=temp/1000;
            request=temp+" KB/秒";
        }catch(Exception e){
            request="";
        }
        return request;
    }

    //将字符串的毫秒时间长度转化为合理的时间长度 如 02:32
    static String duration2String(String request){
        try{
            int temp=Integer.parseInt(request);
            request=IntegerToTime(temp/1000);
        }catch(Exception e){
            request="";
        }
        return request;
    }

    //转换文件大小 字节->KB, MB
    static String Bytes2Mb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
            return (returnValue + " MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + " KB");
    }

    //忽略大小写对list进行排序
    static void sortIgnoreCase(List<String> list){
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
    }

    //根据给定的范围生成随机数，存入给定的list，并返回一个随机数
    static int getRealRandomNum(int numberRange , List<Integer> randomList){
        //生成一个随机数
        int randomTemp=new Random().nextInt(numberRange);
        //如果生成的随机数已出现过，则生成一个新随机数，直到出现不重复的随机数
        while(randomList.contains(randomTemp)){
            randomTemp=new Random().nextInt(numberRange);
        }
        //将生成的不重复随机数存入randomList
        randomList.add(randomTemp);
        //如果randomList已满，则清空前2/3位置，用于存储下次调用而生成的新随机数
        if(randomList.size()==numberRange){
            int temp=randomList.size() / 3;//为什么不是切半，而是选1/3？因为切半后随机性不够
            for (int j = 0; j <temp*2 ; j++) {//为什么要*2？ 为了加强随机性
                randomList.remove(0);//把前一半的数据全部删除点，后面的数据自动前移
            }
        }
        return randomTemp;
    }
}
