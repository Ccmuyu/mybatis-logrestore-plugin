package mybatis.logrestore;

/**
 * 字符串常量
 */
public class StringConst {
    public static final String SPLIT_LINE = "-------------------------------------------------------------------------";
    public static final String PLUGIN_ID = "MyBatisLogRestorePlugin";
    public static final String PREPARING = "Preparing:";
    public static final String PARAMETERS = "Parameters:";
    public static final String FILTER_KEY = PLUGIN_ID + "MyBatisLog.Filters";
    public static final String PREPARING_KEY = PLUGIN_ID + "preparing";
    public static final String PARAMETERS_KEY = PLUGIN_ID + "parameters";
    public static final String STARTUP_KEY = PLUGIN_ID + "startup";

    public static final String consoleKey = PLUGIN_ID + "consoleView";
    public static final String runningKey = PLUGIN_ID + "running";
    public static final String sqlFormatKey = PLUGIN_ID + "sqlFormat";
    public static final String indexNumKey = PLUGIN_ID + "indexNum";
}
