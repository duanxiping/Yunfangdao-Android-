package com.vrd.tech.yfd.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.vrd.tech.yfd.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tsang on 16/7/1.
 */
public class APIUtil {
    private static String MY_KEY;
    private static String MY_SECRET;

    public static void initKey(Context context) {
        MY_KEY = context.getString(R.string.my_key);
        MY_SECRET = context.getString(R.string.my_secret);
    }

    public final static String MD5(String input) {
        byte[] source;
        try {
            // Get byte according by specified coding.
            source = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            source = input.getBytes();
        }
        String result = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            // The result should be one 128 integer
            byte temp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = temp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            result = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加密签名说明：空格符，以%20加密
     * ,以原始形式加密
     * 中文以utf-8形式加密
     *
     * @param options
     * @return
     */
    public static String getSignString(HashMap<String, String> options) {
        HashMap<String, String> signMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            if (entry.getValue().contains(" ") || entry.getValue().contains(",")) {
                signMap.put(entry.getKey(), entry.getValue());
            } else {
                signMap.put(entry.getKey(), encodeUTF(entry.getValue()));

            }
        }
        String s = raw(signMap);
        String sign = MD5(MY_SECRET + s.replaceAll(" ", "%20") + MY_SECRET).toUpperCase();
        return sign;
    }

    public static String getCurrentTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(date);
        return str;
    }

    public static Map<String, String> createOptions(String method, HashMap<String, String> params) {
        return createOptions(method, params, "");
    }

    public static Map<String, String> createOptions(String method, HashMap<String, String> params, String fields) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("method", method);
        options.put("timestamp", getCurrentTime());
        options.put("format", "json");
        options.put("app_key", MY_KEY);
        options.put("v", "1.0");
        options.put("sign_method", "md5");
        //参数
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(
                params.entrySet());
        for (Map.Entry<String, String> entry : list) {
            options.put(entry.getKey(), entry.getValue());
        }
        if (!fields.equals("")) {
            options.put("fields", fields);
        }
        //签名
        options.put("sign", getSignString(options));
        //结果
        return options;
    }

    /**
     * raw 把参数排序并进行拼接
     *
     * @param param
     */
    public static String raw(HashMap<String, String> param) {
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(
                param.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, String> entry = list.get(i);
            buffer.append(entry.getKey());
            buffer.append(entry.getValue());
        }
        return buffer.toString();
    }

    private static String encodeUTF(String value) {
        if (value.contains("@") && value.contains(" ")) {
            String str = "";
            String[] sourceStrArray = value.split("@");
            for (int i = 0; i < sourceStrArray.length; i++) {
                if (i > 0) {
                    str = str + "@";
                }
                str = str + sourceStrArray[i].replace(" ", "%20");
            }
            return str;
        } else if (value.contains("@") && !value.contains(" ")) {//如果没有这个 ，当帐号是邮箱的时候会出现 签名错误
            return value;
        } else if (value.contains(":")) {//冒号不用转换
            try {
                value = URLEncoder.encode(value.substring(0, value.indexOf(":")), "utf-8") + ":"
                        + URLEncoder.encode(value.substring(value.indexOf(":") + 1, value.length()), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return value;
        } else {
            try {
                value = URLEncoder.encode(value, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return value;
        }
    }
}
