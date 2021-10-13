package io.github.gjwlfeng.version.log

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

public class RecordLogTask extends DefaultTask {

    static String LOG_SEPARATOR = "###";

    @InputFile
    RegularFileProperty changedLogFile = project.objects.fileProperty()

    @InputDirectory
    RegularFileProperty vLogWorkDir = project.objects.fileProperty()

    @OutputFile
    RegularFileProperty vLogFile = project.objects.fileProperty()

    @Input
    Property<Integer> versionCode = project.objects.property(Integer)

    @Optional
    @Input
    Property<String> versionName = project.objects.property(String)

    @Optional
    @Input
    MapProperty<String, Object> extraMap = project.objects.mapProperty(String,Object)


    def getCurrentVersionLog() {

        def currentLogs = getUpdateContent(changedLogFile.get().asFile)

        def log = new VersionLog()

        if (versionName.getOrNull() != null) {
            log.versionName = versionName.get().trim()
        }

        log.versionCode = versionCode.get().intValue()

        def time = System.currentTimeMillis()
        log.addTime = time
        log.updateTime = time

        log.logs.addAll(currentLogs)
        if (extraMap.get() != null) {
            log.extraMap.putAll(extraMap.get())
        }
        return log

    }

    def static List<String> getUpdateContent(File logFile) {

        int startNum = -1

        logFile.eachLine { lineContent, num ->
            if (lineContent.trim().startsWith(LOG_SEPARATOR)) {
                startNum = num;
            }
        }

        ArrayList<String> logs = new ArrayList<>()
        logFile.eachLine { lineContent, num ->
            String trimLine = lineContent.trim();
            if (trimLine && startNum < num) {
                logs.add(trimLine)
            }
        }

        return logs
    }


    @TaskAction
    public void run() throws Exception {
        VersionLogManager versionLogManager = new VersionLogManager(vLogFile.get().asFile)
        VersionLog versionLog = getCurrentVersionLog()
        versionLogManager.save(versionLog)
        logger.quiet(GSonFactory.instance.GSon.toJson(versionLog))
    }

    public static class CreationAction implements Action<RecordLogTask> {

        VersionLogConfig logConfig;

        CreationAction(VersionLogConfig logConfig) {
            this.logConfig = logConfig
        }

        @Override
        void execute(RecordLogTask task) {
            configVersionLogWorkDir(task)
            configVersionLogFile(task)
            configVersionCode(task)
            configVersionName(task)
            configExtraMap(task)
            configChangedLogFile(task)
        }


        def configChangedLogFile(RecordLogTask task) {
            def changedLogFile = logConfig.changedLogFile
            if (changedLogFile == null) {
                throw new GradleException("必须配置 changedLogFile 参数")
            }
            if (!changedLogFile.exists()) {
                throw new GradleException("${changedLogFile.absolutePath} 文件不存在.")
            }
            task.changedLogFile.set(changedLogFile)
        }

        def configVersionLogFile(RecordLogTask task) {
            def logVersionFile = logConfig.vLogFile
            if (logVersionFile == null) {
                logVersionFile = new File(task.vLogWorkDir.get().asFile, Constant.LOG_SAVE_FILE_NAME)
            }
            task.vLogFile.set(logVersionFile)
        }

        def configExtraMap(RecordLogTask task) {
            if (logConfig.logInfo != null) {
                if (logConfig.logInfo.extraMap != null) {
                    task.extraMap.set(logConfig.logInfo.extraMap)
                }
            }
        }

        def configVersionCode(RecordLogTask task) {
            if (logConfig.logInfo != null) {
                def versionCode = logConfig.logInfo.versionCode
                if (versionCode == null) {
                    throw new GradleException("必须配置versionCode参数")
                }
                task.versionCode.set(versionCode)
            } else {
                throw new GradleException("必须配置 logInfo 参数")
            }
        }

        def configVersionName(RecordLogTask task) {
            if (logConfig.logInfo != null) {
                if (logConfig.logInfo.versionName != null) {
                    task.versionName.set(logConfig.logInfo.versionName)
                }
            }
        }

        def configVersionLogWorkDir(RecordLogTask task) {
            def vLogWorkDir = logConfig.vLogWorkDir
            if (vLogWorkDir == null) {
                throw new GradleException("必须配置vLogWorkDir参数")
            }
            if (!vLogWorkDir.exists()) {
                throw new GradleException("logVersionWorkDir 文件夹不存在.${vLogWorkDir.absolutePath}")
            }

            if (!vLogWorkDir.isDirectory()) {
                throw new GradleException("工作目录(${vLogWorkDir.absolutePath})必须是文件夹")
            }
            task.vLogWorkDir.set(vLogWorkDir)
        }
    }

}
