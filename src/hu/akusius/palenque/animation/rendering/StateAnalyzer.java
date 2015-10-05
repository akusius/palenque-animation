package hu.akusius.palenque.animation.rendering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Osztály egy {@link Grid}-(ek)ből felépülő állapot elemzéséhez.
 * Az elemzést stateXX.pqs elnevezésű fájlokba menti az aktuális alkönyvtárban (XX az állapot azonosítója).
 * Egy állapotot egy futtatás során csak egyszer generál le.
 * @author Bujdosó Ákos
 */
final class StateAnalyzer {

  public static final boolean ENABLED = false;

  public static final int SIZE = 51;

  public static final String EXTENSION = "pqs";

  private static final Map<CellType, Character> TYPE_CHARS = new EnumMap<>(CellType.class);

  static {
    TYPE_CHARS.put(CellType.Empty, '.');
    TYPE_CHARS.put(CellType.Center, 'x');
    TYPE_CHARS.put(CellType.Square, 'Q');
    TYPE_CHARS.put(CellType.Sun, 'S');
    TYPE_CHARS.put(CellType.SunPart, 's');
    TYPE_CHARS.put(CellType.Star, 'R');
    TYPE_CHARS.put(CellType.StarPart, 'r');
  }

  private static final Set<Integer> analyzedStates = new HashSet<>(20);

  /**
   * A megadott {@link Grid}-(ek)ből felépülő állapot elemzése.
   * @param state Az elemzési állapot azonosítója.
   * @param grids Az állapotot alkotó {@link Grid}(-ek).
   */
  public static void analyze(int state, Grid... grids) {
    analyze(state, false, grids);
  }

  /**
   * A megadott {@link Grid}-(ek)ből felépülő állapot elemzése.
   * @param state Az elemzési állapot azonosítója.
   * @param rotateGrids A {@link Grid}-eket el kell-e forgatni analizálás előtt.
   * @param grids Az állapotot alkotó {@link Grid}(-ek).
   */
  public static void analyze(int state, boolean rotateGrids, Grid... grids) {
    if (!ENABLED) {
      throw new IllegalStateException();
    }
    if (grids == null || grids.length == 0) {
      throw new IllegalArgumentException();
    }
    if (analyzedStates.contains(state)) {
      return;
    }
    analyzedStates.add(state);

    final Grid grid;
    if (grids.length == 1 && grids[0].getSize() == SIZE) {
      grid = grids[0];
    } else {
      grid = new Grid(SIZE);
      for (Grid g : grids) {
        grid.merge(g);
      }
      if (rotateGrids) {
        grid.rotate(3, true);
      }
    }

    assert SIZE % 2 == 1;
    final int center = (SIZE - 1) / 2;

    CellType[][] cells = new CellType[SIZE][SIZE];
    byte[][] items = grid.getItems();

    for (int y = 0; y < SIZE; y++) {
      for (int x = 0; x < SIZE; x++) {
        byte item = items[SIZE - y - 1][x];   // itt lentről felfelé mennek a sorok
        switch (item) {
          case Grid.ITEM_EMPTY:
            if (cells[y][x] == null) {
              cells[y][x] = CellType.Empty;
            }
            break;
          case Grid.ITEM_SQUARE:
            cells[y][x] = CellType.Square;
            break;
          case Grid.ITEM_SUN:
          case Grid.ITEM_STAR:
            CellType part = item == Grid.ITEM_SUN ? CellType.SunPart : CellType.StarPart;
            for (int yt = y - 1; yt <= y + 1; yt++) {
              for (int xt = x - 1; xt <= x + 1; xt++) {
                cells[yt][xt] = part;
              }
            }
            cells[y][x] = item == Grid.ITEM_SUN ? CellType.Sun : CellType.Star;
            break;
          default:
            throw new AssertionError();
        }
      }
    }

    if (cells[center][center] == CellType.Empty) {
      cells[center][center] = CellType.Center;
    }

    Map<CellType, List<Coords>> typeCoords = new EnumMap<>(CellType.class);
    typeCoords.put(CellType.Square, new ArrayList<Coords>(200));
    typeCoords.put(CellType.Sun, new ArrayList<Coords>(10));
    typeCoords.put(CellType.Star, new ArrayList<Coords>(10));

    try {
      String name = String.format("state%02d.%s", state, EXTENSION);
      File cwd = new File(".");
      Path path = Paths.get(cwd.getCanonicalPath(), name);
      try (BufferedWriter wr = Files.newBufferedWriter(path, Charset.forName("ASCII"));
              PrintWriter pw = new PrintWriter(wr)) {
        pw.printf("PQS %02d\n\n", state);

        for (int y = 0; y < SIZE; y++) {
          for (int x = 0; x < SIZE; x++) {
            CellType type = cells[y][x];
            pw.print(TYPE_CHARS.get(type));

            List<Coords> coordsList = typeCoords.get(type);
            if (coordsList != null) {
              coordsList.add(new Coords(x - center, center - y));
            }
          }
          pw.print('\n');
        }

        pw.print("\n");
        for (CellType type : new CellType[]{CellType.Square, CellType.Sun, CellType.Star}) {
          List<Coords> coordsList = typeCoords.get(type);
          pw.printf("%c %03d", TYPE_CHARS.get(type), coordsList.size());
          for (Coords coords : coordsList) {
            pw.print(' ');
            pw.print(coords.toString());
          }
          pw.print('\n');
        }
      }
      Logger.getLogger(StateAnalyzer.class.getName()).log(Level.INFO,
              String.format("State analyzed: %d -> %s", state, path));
    } catch (Exception ex) {
      Logger.getLogger(StateAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private StateAnalyzer() {
  }

  private enum CellType {

    Empty,
    Center,
    Square,
    Sun,
    SunPart,
    Star,
    StarPart
  }

  private static class Coords {

    private final int x;

    private final int y;

    Coords(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    private static void coordString(StringBuilder sb, int value) {
      int absValue = value < 0 ? -value : value;
      assert absValue < 100;

      sb.append(value < 0 ? '-' : '+');
      if (absValue < 10) {
        sb.append('0');
      }
      sb.append(absValue);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder(10);
      coordString(sb, x);
      sb.append(',');
      coordString(sb, y);
      return sb.toString();
    }
  }
}
