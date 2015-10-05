package hu.akusius.palenque.animation.ui;

import hu.akusius.palenque.animation.util.UIUtils;
import java.awt.Component;
import java.awt.Window;

/**
 * Osztály a dialógusok szülőkomponensének kezeléséhez.
 * @author Bujdosó Ákos
 */
public final class DialogParent {

  private final Component component;

  private Window window;

  private boolean windowInitialized = false;

  /**
   * Létrehozás a megadott komponenssel.
   * @param component A komponens.
   */
  public DialogParent(Component component) {
    this.component = component;
  }

  /**
   * @return A létrehozáskor megadott komponens.
   */
  public Component getComponent() {
    return component;
  }

  /**
   * Az első híváskor visszakeresi a megadott komponenshez tartozó (első) ablakot.
   * @return A megadott komponenshez tartozó (első) ablak.
   */
  public Window getWindow() {
    if (!windowInitialized) {
      window = UIUtils.windowForComponent(component);
      windowInitialized = true;
    }

    return window;
  }
}
