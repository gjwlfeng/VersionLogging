package io.github.gjwlfeng.version.log

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

public class BuildLogTask extends DefaultTask {

    @InputDirectory
    RegularFileProperty vLogWorkDir = project.objects.fileProperty()

    @InputFile
    RegularFileProperty logTempleFile = project.objects.fileProperty()

    @OutputFile
    RegularFileProperty versionLogFile = project.objects.fileProperty()


    @TaskAction
    public void run() throws Exception {

        BuildLogFactory buildLogFactory = new BuildLogFactory(logTempleFile.get().asFile.parentFile)
        VersionLogManager versionLogManager = new VersionLogManager(versionLogFile.get().asFile)

        def log = versionLogManager.getAllLog()
        if (log == null) {
            log = new ArrayList<VersionLog>()
        }
        def file = new File(vLogWorkDir.get().asFile, logTempleFile.get().asFile.name)
        if (file.exists()) {
            def delete = file.delete();
            if (!delete) {
                throw new GradleException("删除已存在生成的日志文件(${file.absolutePath})失败,请手动删除后再生成")
            }
        }
        buildLogFactory.buildLog(logTempleFile.get().asFile.name, log, file)
        logger.quiet(file.absolutePath)
    }

    public static class CreationAction implements Action<BuildLogTask> {

        VersionLogConfig logConfig
        File logTempleFile

        CreationAction(VersionLogConfig logConfig, File logTempleFile) {
            this.logConfig = logConfig
            this.logTempleFile = logTempleFile
        }

        @Override
        void execute(BuildLogTask task) {
            configVersionLogWorkDir(task)
            configVersionLogFile(task)
            configLogTempleFile(task)
        }

        def configLogTempleFile(BuildLogTask task) {
            task.logTempleFile.set(logTempleFile)
        }

        def configVersionLogFile(BuildLogTask task) {
            def logVersionFile = logConfig.vLogFile
            if (logVersionFile == null) {
                logVersionFile = new File(task.vLogWorkDir.get().asFile, Constant.LOG_SAVE_FILE_NAME)
            }
            task.versionLogFile.set(logVersionFile)
        }


        def configVersionLogWorkDir(BuildLogTask task) {
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
