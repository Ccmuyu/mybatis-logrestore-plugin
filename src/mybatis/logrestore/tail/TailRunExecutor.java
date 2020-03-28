package mybatis.logrestore.tail;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import mybatis.logrestore.StringConst;
import mybatis.logrestore.util.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


@Deprecated
public class TailRunExecutor extends Executor {
    public static final String TOOL_WINDOWS_ID = "MyBatis Log Restore.";

	@Override
	@NotNull
	public String getStartActionText() {
		return TOOL_WINDOWS_ID;
	}

	@Override
	public String getToolWindowId() {
		return TOOL_WINDOWS_ID;
	}

    @Override
    public Icon getToolWindowIcon() {
        return Icons.MyBatisIcon;
    }

    @Override
    @NotNull
    public Icon getIcon() {
        return Icons.MyBatisIcon;
    }

    @Override
    public Icon getDisabledIcon() {
        return Icons.DisabledRunIcon;
    }

	@Override
	public String getDescription() {
		return "mybatis 日志还原";
	}

	@Override
	@NotNull
	public String getActionName() {
		return TOOL_WINDOWS_ID;
	}

	@Override
	@NotNull
	public String getId() {
		return StringConst.PLUGIN_ID;
	}

	@Override
	public String getContextActionId() {
		return "MyBatisLogRestoreActionId";
	}

	@Override
	public String getHelpId() {
		return null;
	}

	public static Executor getRunExecutorInstance() {
		return ExecutorRegistry.getInstance().getExecutorById(StringConst.PLUGIN_ID);
	}
}
