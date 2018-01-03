package com.vrd.tech.yfd.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.Closeable;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tsang on 16/3/9.
 */
public class MyUtil {
    private static Toast toast;

    /**
     * MD5加密
     *
     * @param s 原始数据w
     * @return 加密后的md5值
     */
    public static String getM5DEndo(String s) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
        char[] charArray = s.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * 显示toast
     *
     * @param context 环境引用
     * @param info    消息内容
     */
    public static void showToast(Context context, String info) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    info,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(info);
        }
        toast.show();
    }

    /**
     * 验证是否是手机号码
     *
     * @param mobiles
     * @return true代表是手机号码
     */
    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(147)|(15[^4,\\D])|(17[0-1,6-8])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

//        return m.matches();
        /* 2016-08-05 修改全部返回True*/
        return true;
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 关闭IO流
     *
     * @param foo
     */
    public static void IOClose(Closeable foo) {
        if (foo != null) {
            try {
                foo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转换服务器时间（在原有的基础上加8小时）
     *
     * @param serverDate
     * @param format
     * @return 根据format返回不同形式
     * 0 返回：yyyy-MM-dd HH:mm:ss
     * 1 返回：yyyy-MM-dd
     * 2 返回：HH:mm:ss
     */
    public static String changeTime(String serverDate, int format) {
        String date = serverTimeHandler(serverDate);
        if (date.equals("")) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date originDate = simpleDateFormat.parse(date);
            calendar.setTime(originDate);
            calendar.add(Calendar.HOUR_OF_DAY, 8);
            date = simpleDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (0 == format) {
            return date;
        } else if (1 == format) {
            return date.substring(0, 10);
        } else if (2 == format) {
            return date.substring(11, 19);
        } else {
            return date;
        }
    }

    /**
     * 时间增加
     *
     * @param date 日期 格式（xxxx-xx-xx xx:xx:xx）
     * @param hour 增加小时数
     * @return 日期 格式（xxxx-xx-xx xx:xx:xx）
     */
    public static String addHourToDate(String date, int hour) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date originDate = simpleDateFormat.parse(date);
            calendar.setTime(originDate);
            calendar.add(Calendar.HOUR_OF_DAY, hour);
            return simpleDateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 处理服务器时间，服务器时间格式：2015-11-20T05:11:02.040Z 返回格式：2015-11-20 05:11:02
     *
     * @param serverDate
     * @return
     */
    public static String serverTimeHandler(String serverDate) {
        if (serverDate != null && serverDate.length() != 24) {
            return "";
        }
        String date = serverDate.substring(0, serverDate.length() - 5).replace("T", " ");
        return date;
    }

    /**
     * 获取屏幕高
     *
     * @param context
     * @return
     */
    public static int[] getScreenWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int[] wh = {dm.widthPixels, dm.heightPixels};
        return wh;
    }

    /**
     * 计算时间偏差,计算方式：d2 -d1
     */
    public static int calTimeOffset(String d1, String d2) {
        if (TextUtils.isEmpty(d1)) {
            return -1;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = sdf.parse(d1);
            Date end = sdf.parse(d2);
            long offset = end.getTime() - start.getTime();
            // Log.i("main", "offset=" + offset);
            return (int) (offset / 60000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void log(String msg) {
        Log.i("zeng", msg);
    }

    public static String getAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
