package hu.akusius.palenque.animation.ui;

import java.awt.Font;
import javax.swing.JComponent;

/**
 * Segédrutinok a fontok beállításához.
 * @author Bujdosó Ákos
 */
class FontUtility {

  /**
   * Az alapértelmezetthez képest az új fontméret.
   */
  public static final float FontScale = 0.95f;

  /**
   * Beállítja a megadott komponensen az új fontméretet.
   * @param component
   * @param scaleFactor
   */
  public static void scaleFont(JComponent component, float scaleFactor) {
    if (component == null) {
      return;
    }

    Font currentFont = component.getFont();
    // Csak egész méretet alkalmazunk, hogy ne csússzanak el a betűk
    int newSize = (int) (currentFont.getSize2D() * scaleFactor);
    component.setFont(currentFont.deriveFont((float) newSize));
  }

  /**
   * Beállítja a megadott komponensen az új fontméretet.
   * @param component
   */
  public static void scaleFont(JComponent component) {
    scaleFont(component, FontScale);
  }

  private FontUtility() {
  }
}
