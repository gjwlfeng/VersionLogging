## gradle配置

vLogConfig {
    //工作目录
    vLogWorkDir = new File("E:\XXXXX\")

    //可选,整理后的日志保存文件，默认为工作目录 log_version.json
    vLogFile = new File("E:\XXXXX\log.txt")

    //项目日志
    changedLogFile = new File("E:\XXXXX\changed_log.txt")

    //版本日志记录
    logInfo {
         //版本号
         versionCode = 12
         //可选，版本名称
         versionName ='v1.1.0'

         //添加额外信息
         addExtraInfo "key1","value1"
         addExtraInfo "key2","value2"
    }
}


## 日志保存位置

    默认日志记录保存在 "vLogWorkDir"文件夹下的"log_version.json"中

## 日志文件输出的样式不是自己要想的怎么办？

    可以运行 "createTemple"任务，生成默认模板， 参照默认模板文件，通过定义输出模板来定义日志样式

## 自定义日志输出模板

1.运行 "createTemple"任务，生成默认模板

2.在工作目录 "temple"文件夹中新建log.md文件

3.可以参考 temple/default.md 默认模板文件来自定义自己的日志模板

4.可用的数据可以参考"logFile"文件的信息

5.同步一下gradle,这时就有个"logTemple_xxx_xxx"命令了

6.在terminal 下执行命令

    windows:gradlew logTemple_xxx_xxx
    linux:./gradlew logTemple_xxx_xxx
