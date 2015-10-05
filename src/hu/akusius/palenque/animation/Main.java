package hu.akusius.palenque.animation;

import hu.akusius.palenque.animation.op.OperationManager;
import hu.akusius.palenque.animation.ui.DialogParent;
import hu.akusius.palenque.animation.ui.MainPanel;
import hu.akusius.palenque.animation.util.UIUtils;
import java.awt.EventQueue;
import javax.swing.JFrame;

/**
 * A fő futtatóosztály.
 * @author Bujdosó Ákos
 */
public class Main {

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (!UIUtils.setLookAndFeel()) {
          return;
        }

        JFrame f = new JFrame("The Palenque Code – Animation");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new MainPanel(new OperationManager(), new DialogParent(f)));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
      }
    });
  }

}
