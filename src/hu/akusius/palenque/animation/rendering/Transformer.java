package hu.akusius.palenque.animation.rendering;

import java.awt.Dimension;
import org.other.Matrix;

/**
 * Osztály a transzformációk és a perspektivikus projekció kezeléséhez.
 * @author Bujdosó Ákos
 */
public final class Transformer {

  public static final double CELL_SIZE = 1;

  public static final double defaultFL = 59;

  private static double FL = defaultFL;

  /**
   * Az FL igazítása, hogy egyenlő nagyságúak legyenek a négyzetek (1-es zoomnál).
   * @param dim Az új méret. {@code null} esetén visszaállítja az eredeti FL-t.
   */
  public static void adjustFL(Dimension dim) {
    if (dim == null) {
      FL = defaultFL;
      return;
    }
    int size = Math.min(dim.width, dim.height);
    double fact = Math.round(size / (defaultFL + 1.0));
    FL = size / fact - 1.0;
  }

  public static double getCurrentFL() {
    return FL;
  }

  /**
   * A megadott cella közepét adja vissza.
   * @param x A cella X koordinátája.
   * @param y A cella Y koordinátája.
   * @return A cella középpontja.
   */
  public static double[] getCenter(int x, int y) {
    return new double[]{x, y, 0.0d};
  }

  /**
   * A megadott cellát ábrázoló téglalap (négyzet) pontjai BF, JF, JA, BA sorrendben.
   * @param x A cella X koordinátája.
   * @param y A cella Y koordinátája.
   * @return A cellát ábrázoló téglalap pontjainak tömbje BF, JF, JA, BA sorrendben.
   */
  public static double[][] getPoints(int x, int y) {
    double l = x - .5;
    double r = x + .5;
    double t = y + .5;
    double b = y - .5;
    return new double[][]{{l, t, 0.0d}, {r, t, 0.0d}, {r, b, 0.0d}, {l, b, 0.0d}};
  }

  /**
   * A megadott cellatéglalapot ábrázoló téglalap pontjai BF, JF, JA, BA sorrendben.
   * @param x1 A cella egyik sarkának X koordinátája.
   * @param y1 A cella egyik sarkának Y koordinátája.
   * @param x2 A cella másik sarkának X koordinátája.
   * @param y2 A cella másik sarkának Y koordinátája.
   * @return A cellatéglalapot ábrázoló téglalap pontjainak tömbje BF, JF, JA, BA sorrendben.
   */
  public static double[][] getPoints(int x1, int y1, int x2, int y2) {
    double l = Math.min(x1, x2) - .5;
    double r = Math.max(x1, x2) + .5;
    double t = Math.max(y1, y2) + .5;
    double b = Math.min(y1, y2) - .5;
    return new double[][]{{l, t, 0.0d}, {r, t, 0.0d}, {r, b, 0.0d}, {l, b, 0.0d}};
  }

  /**
   * A megadott pont transzformálása.
   * @param p A transzformálandó pont.
   * @param transMatrix A transzformációs mátrix.
   * @return A transzformált pont.
   */
  public static double[] transform(double[] p, Matrix transMatrix) {
    assert p.length == 3;
    double[] v = new double[3];
    transMatrix.transformPoint(p[0], p[1], p[2], v);
    return v;
  }

  /**
   * A megadott pontok transzformálása.
   * @param points A transzformálandó pontok koordinátáinak tömbje.
   * @param transMatrix A transzformációs mátrix.
   * @return A transzformált pontok koordinátáinak tömbje.
   */
  public static double[][] transform(double[][] points, Matrix transMatrix) {
    double[][] r = new double[points.length][3];
    for (int i = 0; i < points.length; i++) {
      double[] p = points[i];
      assert p.length == 3;
      transMatrix.transformPoint(p[0], p[1], p[2], r[i]);
    }
    return r;
  }

  /**
   * A megadott pont (vektor) perspektivikus projekciója.
   * @param point A projektálandó pont (vektor).
   * @param zoom A nagyítás mértéke.
   * @param dim A megjelenítés dimenziói.
   * @return A perspektivikus projekció eredménye, vagy {@code null}, ha nem képezhető le az adott pont.
   */
  public static int[] project(double[] point, double zoom, Dimension dim) {
    assert point.length == 3;
    assert zoom != 0;

    double near = FL / zoom;
    if (point[2] > near) {
      return null;
    }
    double fact = 1.0 / (near - point[2] + 1.0);  // kamera 1.0 távolságra van near-től
    fact *= Math.min(dim.width, dim.height);

    double x = dim.width / 2.0 + fact * point[0];
    double y = dim.height / 2.0 - fact * point[1];
    return new int[]{(int) (x + .5), (int) (y + .5)};
  }

  /**
   * A megadott pontok perspektivikus projekciója 3D-ban.
   * @param points A projektálandó pontok.
   * @param zoom A nagyítás mértéke.
   * @param dim A megjelenítés dimenziói.
   * @return A perspektivikus projekció eredménye, vagy {@code null}, ha nem képezhető le valamelyik pont.
   */
  public static int[][] project(double[][] points, double zoom, Dimension dim) {
    int[][] r = new int[points.length][2];
    for (int i = 0; i < points.length; i++) {
      double[] p = points[i];
      assert p.length == 3;
      int[] c = project(p, zoom, dim);
      if (c == null) {
        return null;
      }
      r[i][0] = c[0];
      r[i][1] = c[1];
    }
    return r;
  }

  /**
   * A megadott cella transzformálása és projektálása.
   * @param x A cella X koordinátája.
   * @param y A cella Y koordinátája.
   * @param transMatrix A transzformációs mátrix (vagy {@code null}, ha nincs szükség transzformálásra).
   * @param zoom A nagyítás mértéke.
   * @param dim A megjelenítés dimenziói.
   * @return A cellát alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben) vagy {@code null}, ha a cella nem (teljesen) látható.
   */
  public static int[][] project(int x, int y, Matrix transMatrix, double zoom, Dimension dim) {
    double[][] points = getPoints(x, y);
    if (transMatrix != null) {
      points = transform(points, transMatrix);
    }
    return project(points, zoom, dim);
  }

  /**
   * A megadott cellatéglalap transzformálása és projektálása 3D-ban.
   * @param x1 A cella egyik sarkának X koordinátája.
   * @param y1 A cella egyik sarkának Y koordinátája.
   * @param x2 A cella másik sarkának X koordinátája.
   * @param y2 A cella másik sarkának Y koordinátája.
   * @param transMatrix A transzformációs mátrix (vagy {@code null}, ha nincs szükség transzformálásra).
   * @param zoom A nagyítás mértéke.
   * @param dim A megjelenítés dimenziói.
   * @return A cellatéglalapot alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben) vagy {@code null}, ha a cella nem látható.
   */
  public static int[][] project(int x1, int y1, int x2, int y2, Matrix transMatrix, double zoom, Dimension dim) {
    double[][] points = getPoints(x1, y1, x2, y2);
    if (transMatrix != null) {
      points = transform(points, transMatrix);
    }
    return project(points, zoom, dim);
  }

  /**
   * A megadott vonal perspektivikus projekciója.
   * Ha a vonal egyik vége a kamera mögött van, akkor automatikusan csonkolja a vonalat.
   * @param points A vonal két végpontjának koordinátái.
   * @param zoom A nagyítás mértéke.
   * @param dim A megjelenítés dimenziói.
   * @return A perspektivikus projekció eredménye, vagy {@code null}, ha a vonal egyáltalán nem látható.
   */
  public static int[][] projectLine(double[][] points, double zoom, Dimension dim) {
    assert points.length == 2;
    assert points[0].length == 3;
    assert points[1].length == 3;
    assert zoom != 0;

    double near = FL / zoom;
    double z1 = points[0][2];
    double z2 = points[1][2];

    if (z1 > near && z2 > near) {
      return null;
    }
    if (z1 > near) {
      double fact = (near - z2) / (z1 - z2);
      double[][] ps = new double[2][3];
      ps[0][0] = points[1][0] + fact * (points[0][0] - points[1][0]);
      ps[0][1] = points[1][1] + fact * (points[0][1] - points[1][1]);
      ps[0][2] = near;
      ps[1] = points[1];
      return project(ps, zoom, dim);
    }
    if (z2 > near) {
      double fact = (near - z1) / (z2 - z1);
      double[][] ps = new double[2][3];
      ps[0] = points[0];
      ps[1][0] = points[0][0] + fact * (points[1][0] - points[0][0]);
      ps[1][1] = points[0][1] + fact * (points[1][1] - points[0][1]);
      ps[1][2] = near;
      return project(ps, zoom, dim);
    }
    return project(points, zoom, dim);
  }

  private Transformer() {
  }
}
