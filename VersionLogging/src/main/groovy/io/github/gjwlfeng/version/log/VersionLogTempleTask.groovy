package io.github.gjwlfeng.version.log

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

public class VersionLogTempleTask extends DefaultTask {

    static String LOG_SEPARATOR = "###";

    @InputFile
    final RegularFileProperty changedLogFile = project.objects.fileProperty()

    @InputDirectory
    final RegularFileProperty versionLogWorkDir = project.objects.fileProperty()

    @OutputFile
    final RegularFileProperty versionLogFile = project.objects.fileProperty()

    @Input
    final Property<Integer> versionCode = project.objects.property(Integer)

    @Optional
    @Input
    final Property<String> versionName = project.objects.property(String)

    @Optional
    @Input
    final Property<Map<String, Object>> extraMap = project.objects.property(HashMap)


    private VersionLog getCurrentVersionLog() {

        def currentLogs = getUpdateContent(changedLogFile.get().asFile)

        def log = new VersionLog();
        log.versionName = versionName.get().trim()
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

    private static List<String> getUpdateContent(File logFile) {

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

        VersionLogConfig versionLogConfig = VersionLogConfig.getConfig(project)
        createDefaultLogTempleFile(versionLogConfig);

        VersionLogManager versionLogManager = new VersionLogManager(versionLogFile.get().asFile)
        VersionLog versionLog = getCurrentVersionLog()
        versionLogManager.save(versionLog)

        logger.quiet(GSonFactory.instance.GSon.toJson(versionLog))
    }

    public static class CreationAction implements Action<VersionLogTempleTask> {

        VersionLogConfig logConfig;

        CreationAction(VersionLogConfig logConfig) {
            this.logConfig = logConfig
        }

        @Override
        void execute(VersionLogTempleTask task) {
            configVersionLogWorkDir(task)
            configVersionLogFile(task)
            configVersionCode(task)
            configExtraMap(task)
            configChangedLogFile(task)
        }

        def configChangedLogFile(VersionLogTempleTask task) {
            if (logConfig.changedLogFile != null) {
                if (!logConfig.changedLogFile.exists()) {
                    throw new GradleException("${logConfig.changedLogFile.absolutePath} 文件不存在.")
                }
                task.changedLogFile.set(logConfig.changedLogFile)
            } else {
                throw new GradleException("必须配置 changedLogFile 参数")
            }
        }

        def configVersionLogFile(VersionLogTempleTask task) {
            def logVersionFile = logConfig.vLogFile
            if (logVersionFile == null) {
                logVersionFile = new File(task.versionLogWorkDir.get().asFile, Constant.LOG_SAVE_FILE_NAME)
            }
            task.versionLogFile.set(logVersionFile)
        }

        def configExtraMap(VersionLogTempleTask task) {
            if (logConfig.logInfo != null) {
                if (logConfig.logInfo.extraMap != null) {
                    task.extraMap.set(logConfig.logInfo.extraMap)
                }
            }
        }

        def configVersionCode(VersionLogTempleTask task) {
            def versionCode = logConfig.versionCode
            if (versionCode == null) {
                throw new GradleException("必须配置versionCode参数")
            }
            task.versionCode.set(versionCode)
        }

        def configVersionLogWorkDir(VersionLogTempleTask task) {
            def logVersionWorkDir = logConfig.vLogWorkDir
            if (logVersionWorkDir == null) {
                throw new GradleException("必须配置vLogWorkDir参数")
            }
            if (!logVersionWorkDir.exists()) {
                throw new GradleException("logVersionWorkDir 文件夹不存在.${logVersionWorkDir.absolutePath}")
            }

            if (!logVersionWorkDir.isDirectory()) {
                throw new GradleException("工作目录(${logVersionWorkDir.absolutePath})必须是文件夹")
            }
            task.versionLogWorkDir.set(vLogWorkDir)
        }
    }
}
