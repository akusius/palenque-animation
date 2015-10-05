package hu.akusius.palenque.animation.ui;

import java.awt.image.BufferedImage;

/**
 * Egy képforrás a képmentéshez.
 * @author Bujdosó Ákos
 */
public interface ImageSource {

  /**
   * @return A generálandó kép minimális mérete.
   */
  int getMinSize();

  /**
   * @return A generálandó kép maximális mérete.
   */
  int getMaxSize();

  /**
   * @return A generálandó kép javasolt mérete.
   */
  int getSuggestedSize();

  /**
   * A kép legenerálása a megadott mérettel.
   * @param size A kép mérete.
   * @return A legenerált kép.
   */
  BufferedImage generateImage(int size);
}
