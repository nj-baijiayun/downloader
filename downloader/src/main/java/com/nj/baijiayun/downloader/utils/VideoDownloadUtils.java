package com.nj.baijiayun.downloader.utils;

public class VideoDownloadUtils {
    /**
     * 转化大小
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {

        int rank = 0;
        double formatSize = size;
        while (formatSize >= 1024) {
            formatSize = formatSize / 1024;
            rank++;
        }
        return String.format(getFormat(rank), formatSize);
    }

    private static String getFormat(int rank) {
        switch (rank) {
            case 0:
                return "%.1fB";
            case 1:
                return "%.1fKB";
            case 2:
                return "%.1fMB";
            case 3:
                return "%.1fGB";
            case 4:
                return "%.1fTB";
            default:
                return "%.1f";
        }
    }

    public static String getFormatDuration(long duration) {
        String suffix = "";
        int rank = 0;
        long rankDuration = duration;
        while (rankDuration > 60 && rank <= 2) {
            long remain = rankDuration % 60;

            suffix = (remain == 0 ? "" : getRankDuration(rank, remain));
            rank++;
            rankDuration = rankDuration / 60;
        }
        return getRankDuration(rank, rankDuration) + suffix;
    }

    private static String getRankDuration(int rank, long rankDuration) {
        String rankFormat;
        switch (rank) {
            case 0:
                rankFormat = "%d秒";
                break;
            case 1:
                rankFormat = "%d分钟";
                break;
            case 2:
                rankFormat = "%d小时";
                break;
            default:
                rankFormat = "%d小时";
                break;
        }
        return String.format(rankFormat, rankDuration);
    }
}
