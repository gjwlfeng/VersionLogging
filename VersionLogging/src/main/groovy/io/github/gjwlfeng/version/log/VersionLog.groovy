package io.github.gjwlfeng.version.log

public class VersionLog implements Comparable<VersionLog> {
    int versionCode
    String versionName
    long updateTime
    long addTime

    ArrayList<String> logs = new ArrayList<>();
    Map<String,Object> extraMap=new HashMap<>()

    VersionLog() {
    }

    VersionLog(int versionCode) {
        this.versionCode = versionCode
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        VersionLog that = (VersionLog) o

        if (versionCode != that.versionCode) return false

        return true
    }

    int hashCode() {
        return versionCode
    }

    @Override
    int compareTo(VersionLog o) {
        def codeResult = this.versionCode - o.versionCode
        if (codeResult == 0) {
            def addTimeResult = this.addTime - o.addTime
            if (addTimeResult == 0) {
                return this.updateTime - o.updateTime
            }
            return addTimeResult;
        }
        return codeResult
    }
}

