package io.github.gjwlfeng.version.log

import org.gradle.api.Action;
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;

class TempleCreateTask extends DefaultTask {

    @InputDirectory
    RegularFileProperty vLogWorkDir = project.objects.fileProperty()

    @TaskAction
    public void run() throws Exception {
        createDefaultLogTempleFile();
    }

    /**
     * 生成默认的模板 demo
     * @return
     */
    def createDefaultLogTempleFile() {
        File templeDir = new File(vLogWorkDir.get().asFile, Constant.TEMPLE_DIR_NAME)
        if (!templeDir.exists()) {
            templeDir.mkdirs();
        }
        File logDemoTempleFile = new File(templeDir, Constant.DEFAULT_LOG_TEMPLE_NAME);
        if (!logDemoTempleFile.exists()) {
            def defaultLogTemple = Utils.getResourceContent(Constant.DEFAULT_LOG_TEMPLE_NAME);
            logDemoTempleFile.write(defaultLogTemple)
        }
    }

    public static class CreationAction implements Action<TempleCreateTask> {

        VersionLogConfig logConfig;

        CreationAction(VersionLogConfig logConfig) {
            this.logConfig = logConfig
        }

        @Override
        void execute(TempleCreateTask task) {
            configVersionLogWorkDir(task)
        }

        def configVersionLogWorkDir(TempleCreateTask task) {
            def vLogWorkDir = logConfig.vLogWorkDir
            if (vLogWorkDir == null) {
                throw new GradleException("必须配置vLogWorkDir参数")
            }
            if (!vLogWorkDir.exists()) {
                throw new GradleException("vLogWorkDir 文件夹不存在.${vLogWorkDir.absolutePath}")
            }

            if (!vLogWorkDir.isDirectory()) {
                throw new GradleException("工作目录(${vLogWorkDir.absolutePath})必须是文件夹")
            }
            task.vLogWorkDir.set(vLogWorkDir)
        }
    }

}
