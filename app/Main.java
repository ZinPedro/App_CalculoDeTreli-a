
import UserInterface.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setReshowDelay(0);

            new MainFrame().setVisible(true);

        });

    }

}