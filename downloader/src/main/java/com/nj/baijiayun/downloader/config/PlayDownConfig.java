package com.nj.baijiayun.downloader.config;

/**
 * 视频下载模板
 */
public class PlayDownConfig {
    //保存的文件名称
    private String fileName;
    //视频Id
    private long videoId;
    //视频Token
    private String token;
    //下载的视频是否加密  0 不加密，1加密
    private int encryptType;
    //用户id
    private long uId;
    //职业
    private String periodsId;
    //课程名称
    private String courseName;
    //章名称
    private String periodsName;
    private String sessionId;
    private String courseId;
    private String courseCover;
    private String chapterName;
    private String chapterId;

    public String getSessionId() {
        return sessionId;
    }

    public String getFileName() {
        return fileName;
    }

    public long getVideoId() {
        return videoId;
    }

    public String getToken() {
        return token;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public String getuId() {
        return String.valueOf(uId);
    }

    public String getPeriodsId() {
        return periodsId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getPeriodsName() {
        return periodsName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseCover() {
        return courseCover;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getChapterId() {
        return chapterId;
    }

    public static class Bulider {
        //保存的文件名称
        private String fileName;
        //视频Id
        private long videoId;
        //视频Token
        private String token;
        //下载的视频是否加密  0 不加密，1加密
        private int encryptType;
        //用户id
        private long uid;
        //职业
        private String periodsId;
        //课程名称
        private String courseName;
        //章名称
        private String periodsName;
        private String sessionId;
        private String courseId;
        private String courseCover;
        private String chapterName;
        private String chapterId;

        public String getCourseId() {
            return courseId;
        }

        public Bulider setCourseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Bulider setCourseCover(String courseCover) {
            this.courseCover = courseCover;
            return this;
        }

        public Bulider setChapterName(String chapterName) {
            this.chapterName = chapterName;
            return this;
        }

        public String getChapterId() {
            return chapterId;
        }

        public Bulider setChapterId(String chapterId) {
            this.chapterId = chapterId;
            return this;
        }

        public Bulider setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Bulider setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Bulider setVideoId(long videoId) {
            this.videoId = videoId;
            return this;
        }

        public Bulider setToken(String token) {
            this.token = token;
            return this;
        }

        public Bulider setEncryptType(int encryptType) {
            this.encryptType = encryptType;
            return this;
        }

        public Bulider setUid(long uid) {
            this.uid = uid;
            return this;
        }

        public Bulider setPeriodsId(String periodsId) {
            if (periodsId == null || "".equals(periodsId)) {
                this.periodsId = "";
            }
            this.periodsId = periodsId;
            return this;
        }

        public Bulider setCourseName(String courseName) {
            if (courseName == null || "".equals(courseName)) {
                this.courseName = "";
            }
            this.courseName = courseName;
            return this;
        }

        public Bulider setPeriodsName(String periodsName) {
            if (periodsName == null || "".equals(periodsName)) {
                this.periodsName = "";
            }
            this.periodsName = periodsName;
            return this;
        }


        public PlayDownConfig builder() {
            PlayDownConfig config = new PlayDownConfig();
            //必选参数
            config.fileName = this.fileName;
            config.token = this.token;
            config.uId = this.uid;
            config.videoId = this.videoId;
            config.courseId = this.courseId;
            //可选参数
            config.courseName = this.courseName;
            config.encryptType = this.encryptType;
            config.periodsName = this.periodsName;
            config.periodsId = this.periodsId;
            config.sessionId = this.sessionId;
            config.chapterId = this.chapterId;
            config.chapterName = this.chapterName;
            config.courseCover = this.courseCover;
            return config;
        }
    }

}
