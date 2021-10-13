package io.github.gjwlfeng.version.log;

public class VersionLogDto {
    Integer versionCode
    String versionName
    Map<String, Object> extraMap = new HashMap<>()

    void versionCode(Integer versionCode) {
        this.versionCode = versionCode
    }
    void versionName(String versionName) {
        this.versionName = versionName
    }

    public void addExtraInfo(String key, Object value) {
        extraMap.put(key, value)
    }
}

