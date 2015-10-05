package hu.akusius.palenque.animation.rendering;

/**
 * Egy négyzetrácsot reprezentáló osztály.
 * A koordináták jobbra és felfelé nőnek.
 * Az elemeket a tömbben soronként tároljuk, lentről felfelé.
 * @author Bujdosó Ákos
 */
final class Grid {

  public static final byte ITEM_EMPTY = 0;

  public static final byte ITEM_FIRST = 1;

  public static final byte ITEM_SQUARE = 1;

  public static final byte ITEM_SUN = 2;

  public static final byte ITEM_STAR = 3;

  public static final byte ITEM_LAST = 3;

  private final int size;

  private final byte[][] items;

  private final int ci;

  private int centerX;

  private int centerY;

  /**
   * Új négyzetháló létrehozása a megadott mérettel és (0, 0) középső cellával.
   * @param size A négyzetháló mérete.
   */
  Grid(int size) {
    this(size, 0, 0);
  }

  /**
   * Új négyzetháló létrehozása a megadott mérettel és bal alsó cellával.
   * @param size A négyzetháló mérete. Csak páratlan és 1-nél nagyobb szám lehet.
   * @param centerX A középső cella (abszolút) X koordinátája.
   * @param centerY A középső cella (abszolút) Y koordinátája.
   */
  Grid(int size, int centerX, int centerY) {
    assert size > 1 && size % 2 == 1;
    this.size = size;
    this.items = new byte[size][size];
    this.centerX = centerX;
    this.centerY = centerY;
    this.ci = (size - 1) / 2;
  }

  public int getSize() {
    return size;
  }

  /**
   * @return A négyzethálót alkotó elemek.
   */
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  byte[][] getItems() {
    return items;
  }

  /**
   * @return A középső cella abszolút X koordinátája: elemek hozzáadásakor és összefűzéskor van szerepe.
   */
  public int getCenterX() {
    return centerX;
  }

  /**
   * @return A középső cella abszolút Y koordinátája: elemek hozzáadásakor és összefűzéskor van szerepe.
   */
  public int getCenterY() {
    return centerY;
  }

  /**
   * Új abszolút középpont megadása. Az abszolút középpontnak elemek hozzáadásakor és összefűzéskor van szerepe.
   * @param centerX A középső cella (abszolút) X koordinátája.
   * @param centerY A középső cella (abszolút) Y koordinátája.
   */
  public void setCenter(int centerX, int centerY) {
    this.centerX = centerX;
    this.centerY = centerY;
  }

  /**
   * Egy új elem hozzáadása a négyzetrácshoz.
   * @param type Az elem típusa.
   * @param x Az elem abszolút X koordinátája.
   * @param y Az elem abszolút Y koordinátája.
   */
  public void addItem(byte type, int x, int y) {
    assert !(type < ITEM_FIRST || type > ITEM_LAST);
    int ix = x - centerX + ci;
    int iy = y - centerY + ci;
    assert !(ix < 0 || ix >= size || iy < 0 || iy >= size);
    assert !(type != ITEM_SQUARE && (ix < 1 || ix >= size - 1 || iy < 1 || iy >= size - 1));
    assert !(items[iy][ix] != ITEM_EMPTY && items[iy][ix] != type);
    items[iy][ix] = type;
  }

  /**
   * Több azonos típusú elem hozzáadása.
   * @param type Az elemek típusa.
   * @param coords A koordináták tömbje X1, Y1, X2, Y2, stb. sorrendben.
   */
  public void addItems(byte type, int[] coords) {
    for (int i = 0; i < coords.length - 1; i += 2) {
      addItem(type, coords[i], coords[i + 1]);
    }
  }

  /**
   * Egy függőleges hármas hozzáadása. A hozzáadás során fel lesz bontva négyzetekre.
   * @param x A középső elem X koordinátája.
   * @param y A középső elem Y koordinátája.
   */
  public void addTriplet(int x, int y) {
    addTriplet(x, y, false);
  }

  /**
   * Egy tetszőleges hármas hozzáadása. A hozzáadás során fel lesz bontva négyzetekre.
   * @param x A középső elem X koordinátája.
   * @param y A középső elem Y koordinátája.
   * @param horizontal {@code true} esetén vízszintes a hármas, egyébként függőleges.
   */
  public void addTriplet(int x, int y, boolean horizontal) {
    if (horizontal) {
      addItems(ITEM_SQUARE, new int[]{x - 1, y, x, y, x + 1, y});
    } else {
      addItems(ITEM_SQUARE, new int[]{x, y - 1, x, y, x, y + 1});
    }
  }

  /**
   * Függőleges hármasok hozzáadása. A hozzáadás során a hármasok fel lesznek bontva négyzetekre.
   * @param coords A középső elemek koordinátáinak tömbje X1, Y1, X2, Y2, stb. sorrendben.
   */
  public void addTriplets(int[] coords) {
    for (int i = 0; i < coords.length - 1; i += 2) {
      addTriplet(coords[i], coords[i + 1], false);
    }
  }

  /**
   * A négyzetrács elemeinek elforgatása 90 fokkal óramutató járása szerinti irányban a megadott számban.
   * @param num A forgatások száma.
   * @param keep {@code true} esetén megtartja az eredeti elemeket, egyébként azokat törli.
   */
  public void rotate(int num, boolean keep) {
    assert num >= 1 && num <= 3;
    for (int iy = 0; iy < size; iy++) {
      for (int ix = 0; ix < size; ix++) {
        byte it = (byte) (items[iy][ix] & 0x0F);
        if (it >= ITEM_FIRST && it <= ITEM_LAST) {
          if (ix == ci && iy == ci) {
            // Középső elem mindig marad a helyén
            continue;
          }
          for (int n = 1; n <= num; n++) {
            if (!keep && n < num) {
              continue;
            }
            final int niy;
            final int nix;
            switch (n) {
              case 1:
                // y, -x
                nix = iy;
                niy = (ci << 1) - ix;
                break;
              case 2:
                // -x, -y
                nix = (ci << 1) - ix;
                niy = (ci << 1) - iy;
                break;
              case 3:
                // -y, x
                nix = (ci << 1) - iy;
                niy = ix;
                break;
              default:
                throw new AssertionError();
            }
            if (!keep) {
              items[iy][ix] &= 0xF0;
            }
            items[niy][nix] |= (byte) (it << 4);
          }
        }
      }
    }

    // A felső nibble-be tettük bele az új elemeket
    // Végigmegyünk másodjára és beletesszük alulra
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        byte b = items[i][j];
        if (b > 0x0F) {
          byte it = (byte) (b & 0x0F);
          byte nit = (byte) (b >> 4);
          assert it == ITEM_EMPTY || nit == it;
          items[i][j] = nit;
        }
      }
    }
  }

  /**
   * A négyzetrács elemeinek nyolcszorozása három elforgatással és tükrözéssel.
   * Az eredeti elemeket a helyükön hagyja.
   */
  public void octuple() {
    rotate(3, true);
    for (int iy = 0; iy < size; iy++) {
      for (int ix = 0; ix < size; ix++) {
        byte it = (byte) (items[iy][ix] & 0x0F);
        if (it >= ITEM_FIRST && it <= ITEM_LAST) {
          int nix = (ci << 1) - ix;
          int niy = iy;
          assert items[niy][nix] == ITEM_EMPTY || items[niy][nix] == it;
          items[niy][nix] = it;
        }
      }
    }
  }

  /**
   * A megadott másik négyzetrács elemeinek hozzáadása a jelenlegi négyzetrácshoz az abszolút koordináták alapján.
   * @param o A másik négyzetrács.
   */
  public void merge(Grid o) {
    for (int iy = 0; iy < o.size; iy++) {
      for (int ix = 0; ix < o.size; ix++) {
        byte b = o.items[iy][ix];
        if (b != ITEM_EMPTY) {
          int nix = ix + o.centerX - o.ci - this.centerX + this.ci;
          int niy = iy + o.centerY - o.ci - this.centerY + this.ci;
          assert nix >= 0 && nix < size && niy >= 0 && niy < size;
          assert !(b != ITEM_SQUARE && (nix < 1 || nix >= size - 1 || niy < 1 || niy >= size - 1));
          assert items[niy][nix] == ITEM_EMPTY || items[niy][nix] == b;
          items[niy][nix] = b;
        }
      }
    }
  }

  /**
   * A négyzetháló klónozása.
   * @return Az új klónozott négyzetrács.
   */
  public Grid cloneGrid() {
    Grid g = new Grid(this.size, this.centerX, this.centerY);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        byte b = items[i][j];
        if (b != ITEM_EMPTY) {
          g.items[i][j] = b;
        }
      }
    }
    return g;
  }
}
