package io.github.gjwlfeng.version.log;

public class Utils {
    public static String getResourceContent(String filePath) {
        URL resource = Utils.class.getClassLoader().getResource(filePath);
        return resource.getText("utf-8")
    }


    static def getFileSuffix(File file) {
        if (file == null) {
            return ""
        }

        int lastPositionPos = file.name.lastIndexOf(".")
        if (lastPositionPos != -1) {
            if (file.name.length() - 1 <= lastPositionPos) {
                return ""
            }
            return file.name.substring(lastPositionPos + 1)
        }
        return ""
    }

    static def getFileName(File file) {
        if (file == null) {
            return ""
        }

        int lastPositionPos = file.name.lastIndexOf(".")
        if (lastPositionPos != -1 && lastPositionPos != 0) {
            return file.name.substring(0, lastPositionPos)
        }
        return ""
    }

}
