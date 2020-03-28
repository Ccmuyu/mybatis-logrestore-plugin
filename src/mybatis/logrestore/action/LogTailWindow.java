package mybatis.logrestore.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.codehaus.jettison.json.JSONArray;

@Deprecated
public class LogTailWindow extends DumbAwareAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if (project != null) {
            new ShowLogInConsoleAction(project).showLogInConsole(project);
        }
        throw new RuntimeException("project is null");
    }
}
