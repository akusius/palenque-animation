package hu.akusius.palenque.animation.rendering;

import java.awt.Dimension;
import java.awt.Graphics2D;
import org.other.Matrix;

/**
 * Egy {@code Grid} kirajzolása.
 * @author Bujdosó Ákos
 */
final class GridRenderer {

  private static final byte HORZ = 1;

  private static final byte VERT = 2;

  private static final byte BOTH = HORZ | VERT;

  /**
   * A megadott négyzetrács kirajzolása.
   * @param grid A kirajzolandó négyzetrács
   * @param transformMatrix Az alkalmazandó transzformációs mátrix.
   * @param g A kirajzolás célja.
   * @param zoom A nagyítás mértéke.
   * @param dim A kirajzolás dimenziói.
   */
  public static void render(Grid grid, final Matrix transformMatrix, final Graphics2D g, final double zoom, final Dimension dim) {
    int size = grid.getSize();
    int ci = (size - 1) / 2;
    int cx = grid.getCenterX();
    int cy = grid.getCenterY();
    final int iax = cx - ci;
    final int iay = cy - ci;
    byte[][] items = grid.getItems();
    byte[][] segs = null;
    for (int iy = 0; iy < size; iy++) {
      for (int ix = 0; ix < size; ix++) {
        byte b = items[iy][ix];
        if (b == Grid.ITEM_EMPTY) {
          continue;
        }

        int x = ix + iax;
        int y = iy + iay;

        assert b == Grid.ITEM_SQUARE || b == Grid.ITEM_SUN || b == Grid.ITEM_STAR;

        if (b == Grid.ITEM_SQUARE) {
          // Itt csak szegmensekre bontjuk a négyzetet, és eltároljuk ezeket
          if (segs == null) {
            segs = new byte[size + 1][size + 1];
          }
          segs[iy][ix] = BOTH;   // bal és alsó
          segs[iy][ix + 1] |= VERT;   // jobb
          segs[iy + 1][ix] |= HORZ;   // felső
        } else if (b == Grid.ITEM_SUN) {
          int[][] ps = Transformer.project(x - 1, y - 1, x + 1, y + 1, transformMatrix, zoom, dim);
          if (ps != null) {
            g.drawLine(ps[0][0], ps[0][1], ps[2][0], ps[2][1]);
            g.drawLine(ps[1][0], ps[1][1], ps[3][0], ps[3][1]);
          }
        } else if (b == Grid.ITEM_STAR) {
          int[][] ps1 = Transformer.project(x - 1, y - 1, x + 1, y + 1, transformMatrix, zoom, dim);
          int[][] ps2 = Transformer.project(x, y, transformMatrix, zoom, dim);
          if (ps1 != null && ps2 != null) {
            g.drawLine(ps1[0][0], ps1[0][1], ps1[2][0], ps1[2][1]);
            g.drawLine(ps1[1][0], ps1[1][1], ps1[3][0], ps1[3][1]);
            g.drawPolyline(
                    new int[]{ps2[0][0], ps2[1][0], ps2[2][0], ps2[3][0], ps2[0][0]},
                    new int[]{ps2[0][1], ps2[1][1], ps2[2][1], ps2[3][1], ps2[0][1]},
                    5);
          }
        }
      }
    }

    if (segs == null) {
      // Készen vagyunk
      return;
    }

    SegmentRenderer renderer = new SegmentRenderer() {

      @Override
      public void renderSegment(int x1, int y1, int x2, int y2, int p1, int p2) {
        // System.out.println(String.format("x1: %d, y1: %d, x2: %d, y2: %d, p1: %d, p2: %d", x1, y1, x2, y2, p1, p2));
        double[][] points = Transformer.getPoints(x1 + iax, y1 + iay, x2 + iax, y2 + iay);
        double[][] ps = new double[2][3];
        ps[0] = points[p1];
        ps[1] = points[p2];
        ps = Transformer.transform(ps, transformMatrix);
        int[][] pps = Transformer.projectLine(ps, zoom, dim);
        if (pps != null) {
          g.drawLine(pps[0][0], pps[0][1], pps[1][0], pps[1][1]);
        }
      }
    };

    // Vízszintes szegmensek
    for (int row = 0; row <= size; row++) {
      int start = -1;
      for (int col = 0; col <= size; col++) {
        byte s = segs[row][col];
        if (start == -1 && (s & HORZ) != 0) {
          // Itt kezdődik egy szegmens
          start = col;
        } else if (start != -1 && (s & HORZ) == 0) {
          // Végére értünk egy szegmensnek, meghúzzuk a vonalat
          if (row < size) {
            renderer.renderSegment(start, row, col - 1, row, 3, 2);
          } else {
            renderer.renderSegment(start, row - 1, col - 1, row - 1, 0, 1);
          }
          start = -1;
        }
      }
      if (start != -1) {
        if (row < size) {
          renderer.renderSegment(start, row, size - 1, row, 3, 2);
        } else {
          renderer.renderSegment(start, row - 1, size - 1, row - 1, 0, 1);
        }
      }
    }

    // Föggőleges szegmensek
    for (int col = 0; col <= size; col++) {
      int start = -1;
      for (int row = 0; row <= size; row++) {
        byte s = segs[row][col];
        if (start == -1 && (s & VERT) != 0) {
          // Itt kezdődik egy szegmens
          start = row;
        } else if (start != -1 && (s & VERT) == 0) {
          // Végére értünk egy szegmensnek, meghúzzuk a vonalat
          if (col < size) {
            renderer.renderSegment(col, start, col, row - 1, 0, 3);
          } else {
            renderer.renderSegment(col - 1, start, col - 1, row - 1, 1, 2);
          }
          start = -1;
        }
      }
      if (start != -1) {
        if (col < size) {
          renderer.renderSegment(col, start, col, size - 1, 0, 3);
        } else {
          renderer.renderSegment(col - 1, start, col - 1, size - 1, 1, 2);
        }
      }
    }
  }

  private GridRenderer() {
  }

  private interface SegmentRenderer {

    void renderSegment(int x1, int y1, int x2, int y2, int p1, int p2);
  }

}
