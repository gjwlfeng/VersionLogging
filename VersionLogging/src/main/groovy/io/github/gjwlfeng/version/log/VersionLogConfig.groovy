package io.github.gjwlfeng.version.log

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskProvider
import org.gradle.util.ConfigureUtil

class VersionLogConfig {
    Integer versionCode
    File vLogWorkDir
    File vLogFile
    File changedLogFile
    VersionLogDto logInfo

    TaskProvider<DefaultTask> helpTask;
    TaskProvider<TempleCreateTask> templeCreateTask;
    TaskProvider<RecordLogTask> recordLogTask;
    HashMap<String, TaskProvider<BuildLogTask>> templeLogMap = new HashMap<>()

    void versionCode(int versionCode) {
        this.versionCode = versionCode
    }

    void versionLogWorkDir(File versionLogWorkDir) {
        this.vLogWorkDir = versionLogWorkDir
    }

    void versionLogFile(File versionLogFile) {
        this.vLogFile = versionLogFile
    }

    void logInfo(Action<VersionLogDto> action) {
        if (logInfo == null) {
            logInfo = new VersionLogDto()
        }
        action.execute(logInfo)
    }

    void logInfo(Closure c) {
        if (logInfo == null) {
            logInfo = new VersionLogDto()
        }
        ConfigureUtil.configure(c, logInfo);
    }

    public static String getFullTempleTaskName(String fileName,String fileSuffix ) {
        return "${Constant.TEMPLE_TASK_NAME_PREFIX}${fileName}_${fileSuffix}"
    }

    public static String getFullTaskName(String name) {
        return "${Constant.TASK_NAME_PREFIX}${name.capitalize()}"
    }


    @Override
    public String toString() {
        return "VersionLogConfig{" +
                "versionCode=" + versionCode +
                ", vLogWorkDir=" + vLogWorkDir +
                ", vLogFile=" + vLogFile +
                ", changedLogFile=" + changedLogFile +
                ", logInfo=" + logInfo +
                '}';
    }
}
