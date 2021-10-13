package io.github.gjwlfeng.version.log

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler

public class BuildLogFactory {

    Configuration cfg = null;

    public BuildLogFactory(File templeDir) throws IOException {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setDirectoryForTemplateLoading(templeDir);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void buildLog(String templeFileName, ArrayList<VersionLog> logs, File logFile) throws Exception {

        Map<String, Object> root = new HashMap<>();
        root.put("logInfos", logs);

        FileOutputStream fos = null;
        Writer out = null;
        try {
            Template temp = cfg.getTemplate(templeFileName);
            fos = new FileOutputStream(logFile);
            out = new OutputStreamWriter(fos);
            temp.process(root, out);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ignored) {

            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ignored) {

            }
        }

    }
}
