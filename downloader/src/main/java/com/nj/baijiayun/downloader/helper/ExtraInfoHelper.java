package com.nj.baijiayun.downloader.helper;

import java.util.StringTokenizer;

/**
 * 用于组合和分解视频携带的ExtraInfo
 */
public class ExtraInfoHelper {

    public static final String DELIM = "^^";
    public static final String NULL = "null";
    private static String lastExtraInfo;
    private static String[] lastExtraInfos;


    /**
     * 获取指定位置的额外信息
     */
    public static String getExtraInfoIn(String extraInfo, int index) {
        if (lastExtraInfo != null && lastExtraInfo.endsWith(extraInfo)) {
            return convertNull(lastExtraInfos[index]);
        }
        StringTokenizer st = new StringTokenizer(extraInfo, DELIM);//把","作为分割标志，然后把分割好的字符赋予StringTokenizer对象。
        String[] strArray = new String[st.countTokens()];//通过StringTokenizer 类的countTokens方法计算在生成异常之前可以调用此 tokenizer 的 nextToken 方法的次数。
        int i = 0;
        while (st.hasMoreTokens()) {//看看此 tokenizer 的字符串中是否还有更多的可用标记。
            strArray[i++] = st.nextToken().trim();//返回此 string tokenizer 的下一个标记。
        }
        lastExtraInfo = extraInfo;
        lastExtraInfos = strArray;

        return strArray[index];
    }

    private static String convertNull(String info) {
        if (!NULL.equals(info)) {
            return info;
        }
        return null;
    }

    public static String getUid(String extraInfo) {
        return getExtraInfoIn(extraInfo, 0);
    }

    public static String getCourseId(String extraInfo) {
        return getExtraInfoIn(extraInfo, 1);
    }

    public static String getCourseCover(String extraInfo) {
        return getExtraInfoIn(extraInfo, 2);
    }

    public static String getCourseName(String extraInfo) {
        return getExtraInfoIn(extraInfo, 3);
    }

    public static String getChapterId(String extraInfo) {
        return getExtraInfoIn(extraInfo, 6);
    }

    public static String getChapterName(String extraInfo) {
        return getExtraInfoIn(extraInfo, 7);
    }

    public static String getPeriodsId(String extraInfo) {
        return getExtraInfoIn(extraInfo, 4);
    }

    public static String getPeriodsName(String extraInfo) {
        return getExtraInfoIn(extraInfo, 5);
    }

    /**
     * 构造ExtraInfo
     */
    public static String makeExtraInfos(String uid, String courseId, String courseCover,
                                        String courseName, String chapterId, String chapterName,
                                        String periodsId, String periodsName) {

        return getNotNullString(uid) + DELIM + getNotNullString(courseId) + DELIM +
                getNotNullString(courseCover) + DELIM + getNotNullString(courseName) + DELIM +
                getNotNullString(periodsId) + DELIM + getNotNullString(periodsName) + DELIM +
                getNotNullString(chapterName) + DELIM + getNotNullString(chapterId);
    }

    private static String getNotNullString(String str) {
        return str == null ? NULL : str;
    }

}
