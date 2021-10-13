package io.github.gjwlfeng.version.log

import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

public class VersionLogManager {

    private File vLogFile

    public VersionLogManager(File vLogFile) {
        this.vLogFile = vLogFile
    }

    public List<VersionLog> getAllLog() {
        if (vLogFile.exists()) {
            Type versionLogListType = new TypeToken<ArrayList<VersionLog>>() {}.getType();
            return GSonFactory.instance.GSon.fromJson(vLogFile.newReader("utf-8"), versionLogListType);
        } else {
            return null
        }
    }

    public synchronized void save(VersionLog vLog) {
        def logs = getAllLog()

        if (logs == null) {
            logs = new ArrayList<VersionLog>();
        }
        def oldLog = logs.find { def log ->
            return log.versionCode == vLog.versionCode
        }
        if (oldLog != null) {
            oldLog.versionName = vLog.versionName
            oldLog.updateTime = vLog.updateTime
            oldLog.logs.clear()
            oldLog.logs.addAll(vLog.logs)
            oldLog.extraMap.clear()
            oldLog.extraMap.putAll(vLog.extraMap)
        } else {
            logs.add(vLog)
        }

        Collections.sort(logs)

        def jsonString = GSonFactory.instance.GSon.toJson(logs);
        vLogFile.write(jsonString)
    }

}
