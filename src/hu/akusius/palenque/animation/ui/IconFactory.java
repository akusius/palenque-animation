package hu.akusius.palenque.animation.ui;

import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Segédosztály ikonok előállításához.
 * @author Bujdosó Ákos
 */
public class IconFactory {

  /**
   * A megadott nevű ikon beolvasása.
   * @param name Az ikon neve (és kiterjesztése).
   * @return A beolvasott ikon, vagy {@code null}, ha nem sikerült a beolvasás.
   */
  public static ImageIcon readIcon(String name) {
    final URL url = IconFactory.class.getResource("icons/" + name);

    if (url == null) {
      return null;
    }

    return new ImageIcon(url);
  }

  private IconFactory() {
  }
}
