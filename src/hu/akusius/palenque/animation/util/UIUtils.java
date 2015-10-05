package hu.akusius.palenque.animation.util;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Segédrutinok a felület és a megjelenés kezeléséhez.
 * @author Bujdosó Ákos
 */
public class UIUtils {

  /**
   * A Nimbus kinézet beállítása és konfigurálása.
   * Ha nem sikerült beállítani, akkor hibát jelez.
   * @return {@code true}, ha sikerült beállítani.
   */
  public static boolean setLookAndFeel() {
    setLF("Nimbus");
    return true;
  }

  /**
   * A megadott nevű kinézet beállítása.
   * @param lfName A kinézet (Look&Feel) neve. Nem lehet {@code null} vagy üres.
   * @return {@code true}, ha sikerült beállítani a kinézetet.
   */
  private static boolean setLF(String lfName) {
    if (lfName == null || lfName.isEmpty()) {
      throw new IllegalArgumentException();
    }

    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if (lfName.equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
      return true;
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      return false;
    }
  }

  /**
   * A megadott dialóguson az Escape billentyű konfigurálása bezárásra
   * @param dialog
   */
  public static void installDialogEscapeCloseOperation(final JDialog dialog) {
    final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    String dispatchWindowClosingActionMapKey = "dispatch:WINDOW_CLOSING";
    Action dispatchClosing = new AbstractAction() {

      @Override
      public void actionPerformed(ActionEvent event) {
        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
      }
    };

    JRootPane root = dialog.getRootPane();
    root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, dispatchWindowClosingActionMapKey);
    root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
  }

  /**
   * Visszaadja a megadott komponenshez tartozó (első) ablakot.
   * @param component A komponens.
   * @return A komponenshez tartozó (első) ablak, vagy {@code null}, ha nincs ilyen.
   */
  public static Window windowForComponent(Component component) {
    for (Component c = component; c != null; c = c.getParent()) {
      if (c instanceof Window) {
        return (Window) c;
      }
    }
    return null;
  }

  private UIUtils() {
  }
}
