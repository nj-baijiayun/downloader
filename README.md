Download库整合的普通的文件下载库和百家云的视频下载，同时进行了高层封装，旨在对不同类型的下载使用相同的api进行使用和管理。

## 快速集成
在工程目标的`build.gradle`文件添加仓库地址
```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven {
            //release仓库地址
            url = 'http://172.20.2.114:8081/repository/maven-releases/'
        }
        maven {
            //snapshot仓库地址
            url = 'http://172.20.2.114:8081/repository/maven-snapshots/'
        }
    }
}

```
在dependencies下添加依赖
```
dependencies {
    //当前最新版本为1.0.0
    implementation 'com.nj.baijiayun:downloader:1.0.0'
}
```
当前最新版本为1.0.0 [版本说明](./changelog.md)


## 使用说明

### 初始化

在使用downloader功能以前需要进行初始化

```java
DownConfig cinfig =  DownConfig.Builder(this)
  					//用户唯一标示，downloader默认支持多用户登陆下载
  					//但是文件下载部分目前尚不支持对同一个文件url多次下载
            .setUid(uid)
            .setFilePath(filePath)//文件缓存路径
            .setVideoPath(videoPath)//百家云视频缓存路径
            .setVideoCustomDomain(customdomain)//百家云专属域名
            .builder();
DownloadManager.init(config);
```
如果切换用户或者其他用户唯一标示变化需要重新设置uid

```java
DownloadManager.updateUid(uid);
```

### 基本操作

#### 下载

目前文件和百家云视频的下载使用了相同的一套api，只是DownloadType和必填参数不同

```java
//文件类下载
DownloadManager.downloadFile(DownloadManager.DownloadType.TYPE_FILE_AUDIO)
    .parentId(courseId)
    .itemId(chapterId)
    .fileName(filename)
    .parentCover(cover)
    .parentName(courseTitle)
    .url(url)
    .fileGenre(genre)
    .start();
```

#### 查询

downloader底层基于Realm数据库，查询时虽然没有直接暴露Realm的api，但是也极大的使用了Realm数据托管对象的特性，通过Realm查询到的对象是原本对象的子类也就是托管对象、托管对象主要通过子类重写get、set方法，我们调用get或者set方法，事实上是在直接操作Realm底层数据库。

目前主要有三种可选的查询参数

```java
/**
 * 获取数据库下载列表
 *
 * @param lifecycleOwner 用于监听生命周期回调
 * @param parentId  用户下载时设置的parentId，一般为课程或文库Id
 * @param downloadTypes 下载的类型、可多选，null为全选
 * @param downloadStatus 下载的状态、可多选，null为全选
 * @return DownloadRealmWrapper 结果列表封装类
 */
public static DownloadRealmWrapper getAllDownloadInfo(LifecycleOwner lifecycleOwner, String parentId, DownloadType[] downloadTypes, Integer[] downloadStatus) {
  return getInstance().getDownloadInfo(lifecycleOwner, parentId, downloadTypes, downloadStatus);
}
```

对于查询的结果DownloadRealmWrapper我们可以直接获取托管的List，也可以通过RxJava的形式获取每次变化后的托管List

```java
//直接获取托管的List
List<DownloadItem> results = wrapper.getResults();

//通过rxJava的方式获取每次变化的托管List
wrapper
 	.getAsFlow()
  .subscribe(new Consumer<List<DownloadItem>>() {
    @Override
    public void accept(List<DownloadItem> downloadItems) throws Exception {
      //数据处理
    }
  });
```

#### 暂停

```java
DownloadManager.pauseDownload(DownloadItem item);
```

#### 恢复下载

```java
DownloadManager.resumeDownload(DownloadItem item);
```

#### 删除任务

```java
DownloadManager.delete(List<DownloadItem> items);
```

