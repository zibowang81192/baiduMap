package com.example.edu.hhu.wangzb;
import java.util.Map;
/**
 * @className: EmptyUtil
 * @description: 非空验证工具类
 * @author: ZiboWang
 * @date: 2021/10/27
 * @version:
 **/
public class EmptyUtil {

    /**
     * @see: 验证string类型的是否为空
     */
    public static boolean isNullorEmpty(String str) {
        //为了执行忽略大小写的比较，可以调用equalsIgnoreCase( )方法。当比较两个字符串时，它会认为A-Z和a-z是一样的。
        if ((str == null) || ("".equals(str)) || ("null".equalsIgnoreCase(str)) || ("undefined".equalsIgnoreCase(str))) {
            return true;
        }
        return false;
    }

    /**
     * @see: 验证实体是否为空
     */
    public static <T> boolean isNullorEmpty(T entity) {
        if (entity == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @see: 验证StringBuffer类型的是否为空
     */
    public static boolean isNullorEmpty(StringBuffer str) {
        if (str == null ||"".equals(str.toString())  || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @see: 验证Map类型的是否为空
     */
    public static boolean isNullorEmpty(Map map) {
        if ((map == null) || (map.size() == 0)) {
            return true;
        }
        return false;
    }



    /**
     * @see: 验证Object数组类型的是否为空
     */
    public static boolean isNullorEmpty(Object[] obj) {
        if ((obj == null) || (obj.length == 0)) {
            return true;
        }
        return false;
    }

    /**
     * @see: 验证Long类型的是否为空
     */
    public static boolean isNullorEmpty(Long longTime) {
        if ((longTime == null) || (longTime.longValue() <= 0L)) {
            return true;
        }
        return false;
    }

    /**
     * @see: 验证String数组类型的是否为空
     */
    public static boolean isNullorEmpty(String[] str) {
        if ((str == null) || (str.length == 0)) {
            return true;
        }
        return false;
    }
}
