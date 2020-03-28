package mybatis.logrestore.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import mybatis.logrestore.gui.SqlText;
import mybatis.logrestore.util.ConfigUtil;

/**
 * 预编译语句还原
 */
public class PrepareSqlRestoreAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            throw new RuntimeException("project is null ..");

        }
        SqlText sqlText = ConfigUtil.sqlTextDialog;
        if (sqlText == null) {
            sqlText = ConfigUtil.sqlTextDialog = new SqlText(project);
        }
        sqlText.pack();
        sqlText.setSize(1000, 600);//配置大小
        sqlText.setLocationRelativeTo(null);//位置居中显示
        sqlText.setVisible(true);
    }
}
