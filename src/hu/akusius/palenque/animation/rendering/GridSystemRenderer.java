package hu.akusius.palenque.animation.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.other.Matrix;

/**
 * A koordinátarendszer kirajzolását végző osztály.
 * @author Bujdosó Ákos
 */
public final class GridSystemRenderer {

  private static final int RANGE = 25;

  private static final int STRONG_LINE_STEP = 5;

  private static final Color normalColor = new Color(170, 170, 170, 90);

  private static final Color strongColor = new Color(100, 100, 100, 90);

  private static List<Line> normalLines;

  private static List<Line> strongLines;

  private static Matrix lastTransMatrix;

  private static double lastZoom = -1.0;

  private static Dimension lastDim;

  /**
   * A négyzetrács kirajzolása.
   * @param g A kirajzolás célja.
   * @param transMatrix A transzformációs mátrix.
   * @param zoom A nagyítás mértéke.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Graphics2D g, Matrix transMatrix, double zoom, Dimension dim) {
    g.setColor(strongColor);

    drawCenter(transMatrix, zoom, dim, g);

    if (normalLines == null || strongLines == null) {
      createLines();
    }

    boolean needReproject
            = !Objects.equals(transMatrix, lastTransMatrix) || zoom != lastZoom || !Objects.equals(dim, lastDim);

    for (Line line : strongLines) {
      line.draw(g, transMatrix, zoom, dim, needReproject);
    }

    g.setColor(normalColor);

    for (Line line : normalLines) {
      line.draw(g, transMatrix, zoom, dim, needReproject);
    }

    lastTransMatrix = transMatrix;
    lastZoom = zoom;
    lastDim = dim;
  }

  private static void drawCenter(Matrix transMatrix, double zoom, Dimension dim, Graphics2D g) {
    double[] c = Transformer.getCenter(0, 0);
    double d = Transformer.CELL_SIZE / 6.0;

    double[][] lps = new double[][]{
      {c[0] - d, c[1] + d, 0d},
      {c[0] + d, c[1] - d, 0d},
      {c[0] + d, c[1] + d, 0d},
      {c[0] - d, c[1] - d, 0d},};

    lps = Transformer.transform(lps, transMatrix);

    int[][] ps = Transformer.project(lps, zoom, dim);
    if (ps != null) {
      g.drawLine(ps[0][0], ps[0][1], ps[1][0], ps[1][1]);
      g.drawLine(ps[2][0], ps[2][1], ps[3][0], ps[3][1]);
    }
  }

  private static void createLines() {
    int num = RANGE * 2 + 1;
    normalLines = new ArrayList<>(num);
    strongLines = new ArrayList<>(num);

    for (int i = 0; i <= RANGE; i++) {
      addLine(i, -RANGE, i, RANGE, 1, 2, i);
      addLine(-RANGE, i, RANGE, i, 0, 1, i);
    }

    for (int i = 0; i >= -RANGE; i--) {
      addLine(i, -RANGE, i, RANGE, 0, 3, i);
      addLine(-RANGE, i, RANGE, i, 3, 2, i);
    }
  }

  private static void addLine(int x1, int y1, int x2, int y2, int p1, int p2, int num) {
    double[][] ps = Transformer.getPoints(x1, y1, x2, y2);
    Line line = new Line(ps[p1][0], ps[p1][1], ps[p2][0], ps[p2][1]);
    if (num % STRONG_LINE_STEP == 0) {
      strongLines.add(line);
    } else {
      normalLines.add(line);
    }
  }

  private GridSystemRenderer() {
  }

  private static final class Line {

    double[][] points;

    int[][] ps;

    Line() {
    }

    Line(double x1, double y1, double x2, double y2) {
      this.points = new double[][]{{x1, y1, 0.0d}, {x2, y2, 0.0d}};
    }

    void draw(Graphics2D g, Matrix transMatrix, double z, Dimension d, boolean needReproject) {
      if (ps == null || needReproject) {
        final double[][] tps;
        if (transMatrix != null) {
          tps = Transformer.transform(points, transMatrix);
        } else {
          tps = points;
        }
        ps = Transformer.projectLine(tps, z, d);
      }
      if (ps != null) {
        g.drawLine(ps[0][0], ps[0][1], ps[1][0], ps[1][1]);
      }
    }
  }
}
