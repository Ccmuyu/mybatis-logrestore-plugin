package mybatis.logrestore.gui;

import com.intellij.openapi.project.Project;
import mybatis.logrestore.StringConst;
import mybatis.logrestore.util.RestoreSqlUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;


public class SqlText extends JFrame {

    private JPanel panel1;
    private JButton buttonOK;
    private JButton buttonCopy;
    private JButton buttonClose;
    private JTextArea originalTextArea;
    private JTextArea resArea;
    private JButton buttonClear;

    public SqlText(Project project) {
        this.setTitle("restore prepare sql from text"); //设置标题
        setContentPane(panel1);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK(project));
        buttonCopy.addActionListener(e -> onCopy());
        buttonClear.addActionListener(e -> onClear());
        buttonClose.addActionListener(e -> onClose());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
        panel1.registerKeyboardAction(e -> onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(Project project) {
        if (originalTextArea == null || StringUtils.isBlank(originalTextArea.getText())) {
            this.resArea.setText("Can't restore sql from text.-0");
            return;
        }
        String originalText = originalTextArea.getText();
        final String PREPARING = StringConst.PREPARING;
        final String PARAMETERS = StringConst.PARAMETERS;
        if (originalText.contains(PREPARING) && originalText.contains(PARAMETERS)) {
            List<String> sqlArr = prepareText(originalText);
            if (sqlArr.isEmpty()) {
                resArea.setText("no mybatis prepare sql provided.");
            }
            if (sqlArr != null && sqlArr.size() >= 2) {
                int couple = sqlArr.size() / 2;
                StringBuilder sql = new StringBuilder();
                for (int i = 0; i < couple; i++) {
                    String result = prepare(sqlArr.get(2 * i), sqlArr.get(2 * i + 1));
                    sql.append(result);
                    if (i != couple - 1) {
                        sql.append("\r\n################################################################################");
                    }
                }
                String res = sql.toString();
                if (res.isEmpty()) {
                    resArea.setText("empty sql..");
                } else {
                    resArea.setText(res);
                }
            } else {
                this.resArea.setText("Can't restore sql from text.-2");
            }
        } else {
            this.resArea.setText("Can't restore sql from text.-3");
        }
    }


    private List<String> prepareText(String s) {
        String[] split = s.split("\n");
        List<String> res = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            while ((split[i] == null || split[i].isEmpty())) {
                i++;
            }
            if (split[i].contains(StringConst.PREPARING) || split[i].contains(StringConst.PARAMETERS)) {
                res.add(split[i]);
            }
        }
        return res;
    }

    private String prepare(String preparingLine, String parametersLine) {
        return RestoreSqlUtil.restoreSql(preparingLine, parametersLine, true);
    }

    private void onCopy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(this.resArea.getText());
        clipboard.setContents(selection, null);
    }

    private void onClear() {
        this.resArea.setText("");
        this.originalTextArea.setText("");
    }

    private void onClose() {
        this.setVisible(false);
    }
}
