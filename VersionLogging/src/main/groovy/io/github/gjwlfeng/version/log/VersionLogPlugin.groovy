package io.github.gjwlfeng.version.log
import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionLogPlugin implements Plugin<Project> {

    Project project
    VersionLogConfig logConfig

    @Override
    void apply(Project project) {
        this.project = project
        configExtensions()
        createHelpTask()
        createTempleTask()
        createRecordLogTask()
        createVersionLogTask()
    }

    def createRecordLogTask() {
        def task = project.tasks.register("recordLog", RecordLogTask, new RecordLogTask.CreationAction(logConfig))
        task.configure {
            description "记录日志"
            group Constant.TASK_GROUP_NAME
        }
        logConfig.recordLogTask = task
    }

    def createTempleTask() {
        def task = project.tasks.register("createTemple", TempleCreateTask, new TempleCreateTask.CreationAction(logConfig))
        task.configure {
            description "生成样式模板"
            group Constant.TASK_GROUP_NAME
        }
        logConfig.templeCreateTask = task
    }


    def createHelpTask() {
        def taskName = VersionLogConfig.getFullTaskName("help")
        def task = project.tasks.register(taskName)
        task.configure {
            description "帮助"
            group Constant.TASK_GROUP_NAME
            doLast {
                def content = Utils.getResourceContent(Constant.HELP_VERSION_LOG_MANAGER_NAME);
                project.logger.quiet(content)
            }
        }
        logConfig.helpTask = task
    }

    def configExtensions() {
        logConfig =project.extensions.create(Constant.PLUGIN_EXTENSION_NAME, VersionLogConfig)
    }

    def createVersionLogTask() {
        project.afterEvaluate {
            if (logConfig != null && logConfig.vLogWorkDir != null && logConfig.vLogWorkDir.exists()) {
                def templeDir = new File(logConfig.vLogWorkDir, Constant.TEMPLE_DIR_NAME)

                if (templeDir.exists() && templeDir.isDirectory()) {
                    def files = templeDir.listFiles();
                    if (files != null) {
                        files.each { File templeFile ->
                            if (templeFile.isDirectory()) {
                                return
                            }
                            def fileName = Utils.getFileName(templeFile)
                            def fileSuffix = Utils.getFileSuffix(templeFile)
                            def taskName = VersionLogConfig.getFullTempleTaskName(fileName, fileSuffix)
                            def task = project.tasks.register(
                                    taskName,
                                    BuildLogTask,
                                    new BuildLogTask.CreationAction(logConfig, templeFile))
                            task.configure {
                                description "根据模板${templeFile.absolutePath}生成日志"
                                group Constant.TEMPLE_TASK_GROUP_NAME
                                doFirst {
                                    project.logger.println("开始根据模板生成日志")
                                }
                                doLast {
                                    project.logger.println("生成日志完成")
                                }
                            }

                            logConfig.templeLogMap.put(templeFile.name, task)
                        }
                    }
                }
            }
        }
    }
}

