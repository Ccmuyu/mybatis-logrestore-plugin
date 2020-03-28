package mybatis.logrestore.util;

import mybatis.logrestore.format.hibernate.BasicFormatterImpl;
import mybatis.logrestore.format.hibernate.Formatter;
import mybatis.logrestore.StringConst;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 还原预编译sql
 */
public class RestoreSqlUtil {
    private static Set<String> assembledType = new HashSet<>();
    private static Set<String> unAssembledType = new HashSet<>();
    private static final String QUESTION_MARK = "?";
    private static final String REPLACE_MARK = "_o_?_b_";
    private static final String PARAM_TYPE_REGEX = "\\(String\\),{0,1}|\\(Timestamp\\),{0,1}|\\(Date\\),{0,1}|\\(Time\\),{0,1}|\\(LocalDate\\),{0,1}|\\(LocalTime\\),{0,1}|\\(LocalDateTime\\),{0,1}|\\(Byte\\),{0,1}|\\(Short\\),{0,1}|\\(Integer\\),{0,1}|\\(Long\\),{0,1}|\\(Float\\),{0,1}|\\(Double\\),{0,1}|\\(BigDecimal\\),{0,1}|\\(Boolean\\),{0,1}|\\(Null\\),{0,1}";
    private static final String PARAM_TYPE_REGEX2 = "(\\(String\\))|(\\(Timestamp\\))|(\\(Date\\))|(\\(Time\\))|(\\(LocalDate\\))|(\\(LocalTime\\))|(\\(LocalDateTime\\))|(\\(Byte\\))|(\\(Short\\))|(\\(Integer\\))|(\\(Long\\))|(\\(Float\\))|(\\(Double\\))|(\\(BigDecimal\\))|(\\(Boolean\\))|(\\(Null\\))";

    //需要装配的参数类型
    static {
        assembledType.add("(String)");
        assembledType.add("(Timestamp)");
        assembledType.add("(Date)");
        assembledType.add("(Time)");
        assembledType.add("(LocalDate)");
        assembledType.add("(LocalTime)");
        assembledType.add("(LocalDateTime)");
    }

    //no need
    static {
        unAssembledType.add("(Byte)");
        unAssembledType.add("(Short)");
        unAssembledType.add("(Integer)");
        unAssembledType.add("(Long)");
        unAssembledType.add("(Float)");
        unAssembledType.add("(Double)");
        unAssembledType.add("(BigDecimal)");
        unAssembledType.add("(Boolean)");
    }

    public static String match(String p, String str) {
        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(0);
        }
        return "";
    }

    public static String restoreSql(final String preparingLine, final String parametersLine) {
        return restoreSql(preparingLine, parametersLine, false);
    }

    /**
     * Sql语句还原，整个插件的核心就是该方法
     *
     * @param preparingLine  带有预编译sql的行日志
     * @param parametersLine 参数行日志
     * @return
     */
    public static String restoreSql(final String preparingLine, final String parametersLine, boolean format) {
        String restoreSql;
        String preparingSql;
        String parametersSql = "";
        final String PREPARING = StringConst.PREPARING;
        final String PARAMETERS = StringConst.PARAMETERS;
        try {
            if (preparingLine.contains(PREPARING)) {
                preparingSql = preparingLine.split(PREPARING)[1].trim();
            } else {
                preparingSql = preparingLine;
            }
            boolean hasParam = false;
            if (parametersLine.contains(PARAMETERS)) {
                if (parametersLine.split(PARAMETERS).length > 1) {
                    parametersSql = parametersLine.split(PARAMETERS)[1];
                    if (StringUtils.isNotBlank(parametersSql)) {
                        hasParam = true;
                    }
                }
            } else {
                parametersSql = parametersLine;
            }
            if (hasParam) {
                preparingSql = StringUtils.replace(preparingSql, QUESTION_MARK, REPLACE_MARK);
                preparingSql = StringUtils.removeEnd(preparingSql, "\n");
                parametersSql = StringUtils.removeEnd(parametersSql, "\n");
                int questionMarkCount = StringUtils.countMatches(preparingSql, REPLACE_MARK);
                String[] paramArray = parametersSql.split(PARAM_TYPE_REGEX);
                for (int i = 0; i < paramArray.length; ++i) {
                    if (questionMarkCount <= paramArray.length || parametersSql.indexOf("null") == -1) {
                        break;
                    } else {
                        if (parametersSql.contains(", null,")) {
                            parametersSql = parametersSql.replaceFirst(", null,", ", null(Null),");
                        } else {
                            parametersSql = parametersSql.replaceFirst("null,", "null(Null),");
                        }
                    }
                    paramArray = parametersSql.split(PARAM_TYPE_REGEX);
                }
                for (int i = 0; i < paramArray.length; ++i) {
                    paramArray[i] = StringUtils.removeStart(paramArray[i], " ");
                    parametersSql = StringUtils.replaceOnce(StringUtils.removeStart(parametersSql, " "), paramArray[i], "");
                    String paramType = match(PARAM_TYPE_REGEX2, parametersSql);
                    preparingSql = StringUtils.replaceOnce(preparingSql, REPLACE_MARK, assembledParamValue(paramArray[i], paramType));
                    paramType = paramType.replace("(", "\\(").replace(")", "\\)") + ", ";
                    parametersSql = parametersSql.replaceFirst(paramType, "");
                }
            }
            restoreSql = simpleFormat(preparingSql);
            if (!restoreSql.endsWith(";")) {
                restoreSql += ";";
            }
            if (restoreSql.contains(REPLACE_MARK)) {
                restoreSql = StringUtils.replace(restoreSql, REPLACE_MARK, "error");
                restoreSql += "\n---This is an error sql!---";
            }
        } catch (Exception e) {
            return "restore mybatis sql error!";
        }
        if (format) {
            Formatter formatter = new BasicFormatterImpl();
            return formatter.format(restoreSql);
        }
        return restoreSql;
    }

    public static String assembledParamValue(String paramValue, String paramType) {
        if (assembledType.contains(paramType)) {
            paramValue = "'" + paramValue + "'";
        }
        return paramValue;
    }

    public static boolean endWithAssembledTypes(String parametersLine) {
        for (String str : assembledType) {
            if (parametersLine.endsWith(str + "\n")) {
                return true;
            }
        }
        for (String str : unAssembledType) {
            if (parametersLine.endsWith(str + "\n")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 简单的格式化
     *
     * @param sql
     * @return
     */
    public static String simpleFormat(String sql) {
        if (StringUtils.isNotBlank(sql)) {
            return sql.replaceAll("(?i)\\s+from\\s+", "\n FROM ")
                    .replaceAll("(?i)\\s+select\\s+", "\n SELECT ")
                    .replaceAll("(?i)\\s+where\\s+", "\n WHERE ")
                    .replaceAll("(?i)\\s+left join\\s+", "\n LEFT JOIN ")
                    .replaceAll("(?i)\\s+right join\\s+", "\n RIGHT JOIN ")
                    .replaceAll("(?i)\\s+inner join\\s+", "\n INNER JOIN ")
                    .replaceAll("(?i)\\s+limit\\s+", "\n LIMIT ")
                    .replaceAll("(?i)\\s+on\\s+", "\n ON ")
                    .replaceAll("(?i)\\s+union\\s+", "\n UNION ");
        }
        return "";
    }

    public static void main(String[] args) {

        String sql = "2020-03-25 09:48:53.790 DEBUG main org.apache.ibatis.logging.slf4j.Slf4jImpl.debug(Slf4jImpl.java:47) ==>  Preparing: select * from ( ( select r.receipt_apply_code AS orderNo, r.receipt_amount AS amount, '1' AS transactionType, cr.receiptDate as transactionDate, r.customer_code AS payerCode, r.receipt_code AS payeeCode from invoice_receipt_apply r left join CashReceipt cr on r.sys_bill = cr.voucherNo WHERE cr.receiptDate between ? and ? and r.receipt_code = ? AND r.customer_code = ? AND r.apply_state = 30 ) union all ( SELECT f.refund_apply_code AS orderNo, f.amount AS amount, '2' AS transactionType, pr.refund_time AS transactionDate, f.company_code AS payerCode, f.payee_code AS payeeCode FROM payonline_refund_apply f left join payonline_refund pr on f.refund_apply_code = pr.refund_apply_code WHERE pr.refund_time between ? and ? and f.company_code = ? AND f.payee_code = ? AND f.refund_status = 30 ) ) tmp  \n";
        String param = "2020-03-25 09:48:53.791 DEBUG main org.apache.ibatis.logging.slf4j.Slf4jImpl.debug(Slf4jImpl.java:47) ==> Parameters: 2020-01-16 17:11:40.0(Timestamp), 2020-03-20 17:15:41.0(Timestamp), 568141757(String), 568144671(String), 2020-01-16 17:11:40.0(Timestamp), 2020-03-20 17:15:41.0(Timestamp), 568141757(String), 568144671(String) \n";

        String restoreSql = restoreSql(sql, param);
        Formatter formatter = new BasicFormatterImpl();
        String result = formatter.format(restoreSql);
        System.out.println(restoreSql);
        System.out.println("----------------------");
        System.out.println(result);
    }
}
