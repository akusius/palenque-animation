package hu.akusius.palenque.animation.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.other.Matrix;

/**
 * A fedélkép kirajzolását végző osztály.
 * @author Bujdosó Ákos
 */
public final class LidRenderer {

  private static final double width = 60d;

  private static final double hwratio = 850.0 / 500.0;

  private static final double[] center = {-.5d, -21.5d};

  private static final double[][] corners;

  private static BufferedImage image;

  static {
    double hw = width / 2d;
    double hh = width * hwratio / 2d;
    corners = new double[][]{
      {center[0] - hw, center[1] + hh, 0d},
      {center[0] + hw, center[1] + hh, 0d},
      {center[0] + hw, center[1] - hh, 0d},
      {center[0] - hw, center[1] - hh, 0d}
    };
  }

  /**
   * A fedélkép kirajzolása.
   * @param transMatrix A transzformációs mátrix.
   * @param g A kirajzolás célja.
   * @param zoom A nagyítás mértéke.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Matrix transMatrix, Graphics2D g, double zoom, Dimension dim) {
    try {
      if (image == null) {
        image = ImageIO.read(LidRenderer.class.getResource("lid.jpg"));
      }

      g = (Graphics2D) g.create();

      if (transMatrix.hasRotation()) {
        // Csak a körvonalakat rajzoljuk ki
        g.setColor(Color.GRAY);
        double[][] tps = Transformer.transform(corners, transMatrix);
        for (int i = 0; i < 4; i++) {
          int[][] ps = Transformer.projectLine(new double[][]{tps[i], tps[i < 3 ? i + 1 : 0]}, zoom, dim);
          if (ps != null) {
            g.drawLine(ps[0][0], ps[0][1], ps[1][0], ps[1][1]);
          }
        }
        return;
      }

      double[][] tps = Transformer.transform(new double[][]{corners[0], corners[2]}, transMatrix);
      int[][] ps = Transformer.project(tps, zoom, dim);

      int w = ps[1][0] - ps[0][0] + 1;
      int h = ps[1][1] - ps[0][1] + 1;

//      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.drawImage(image, ps[0][0], ps[0][1], w, h, null);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private LidRenderer() {
  }

}
