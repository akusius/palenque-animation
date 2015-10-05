package hu.akusius.palenque.animation.rendering;

import org.junit.Test;

import static hu.akusius.palenque.animation.rendering.Grid.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class GridTest {

  public GridTest() {
  }

  private static byte[][] ra(byte[][] a) {
    for (int i = 0; i < a.length / 2; i++) {
      byte[] row = a[i];
      int j = a.length - i - 1;
      a[i] = a[j];
      a[j] = row;
    }
    return a;
  }

  @Test
  public void test1() {
    Grid grid = new Grid(3);
    assertThat(grid.getSize(), equalTo(3));
    assertThat(grid.getCenterX(), equalTo(0));
    assertThat(grid.getCenterY(), equalTo(0));
    grid.addItems(ITEM_SQUARE, new int[]{-1, -1, 0, 0});
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());
    grid.setCenter(2, 4);
    assertThat(grid.getCenterX(), equalTo(2));
    assertThat(grid.getCenterY(), equalTo(4));
    grid.addItems(ITEM_SQUARE, new int[]{3, 5, 1, 4});
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_SQUARE, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());
  }

  @Test
  public void test2() {
    Grid grid = new Grid(3, -8, -2);
    assertThat(grid.getSize(), equalTo(3));
    assertThat(grid.getCenterX(), equalTo(-8));
    assertThat(grid.getCenterY(), equalTo(-2));
    grid.addItem(ITEM_SQUARE, -7, -2);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());

    grid.setCenter(0, 0);
    assertThat(grid.getCenterX(), equalTo(0));
    assertThat(grid.getCenterY(), equalTo(0));
    grid.addItem(ITEM_STAR, 0, 0);
    grid.addItem(ITEM_SQUARE, -1, 1);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_STAR, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());
  }

  @Test
  public void test3() {
    Grid grid = new Grid(3, 7, 12);
    grid.addItem(ITEM_SQUARE, 7, 11);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.rotate(1, false);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());
    grid.rotate(2, false);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());
    grid.addItem(ITEM_SUN, 7, 12);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_SUN, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());
    grid.rotate(3, true);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_SUN, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
  }

  @Test
  public void test4() {
    Grid grid = new Grid(3);
    grid.addItem(ITEM_SQUARE, 0, -1);
    grid.addItem(ITEM_SQUARE, 1, 0);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.rotate(1, false);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.rotate(1, true);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.rotate(3, false);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.rotate(2, true);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.addItem(ITEM_SUN, 0, 0);
    grid.rotate(3, true);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_SUN, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
  }

  @Test
  public void test5() {
    Grid grid = new Grid(5);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());
    grid.addItem(ITEM_SQUARE, 1, -2);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.octuple();
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
    grid.octuple();
    grid.addItem(ITEM_STAR, 0, 0);
    grid.octuple();
    grid.addItem(ITEM_STAR, 0, 0);
    grid.octuple();
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_STAR, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY}
    }), grid.getItems());
  }

  @Test
  public void test6() {
    Grid grid3 = new Grid(3, -10, -5);
    grid3.addItem(ITEM_SQUARE, -9, -4);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid3.getItems());

    Grid grid5 = new Grid(5, -11, -6);
    grid5.merge(grid3);
    grid5.merge(grid3);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid5.getItems());

    grid3.setCenter(-13, -8);
    grid5.setCenter(-10, -5);
    grid5.merge(grid3);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid5.getItems());

    grid3.setCenter(0, 0);
    grid3.addItem(ITEM_STAR, 0, 0);
    grid5.setCenter(0, 0);
    grid5.merge(grid3);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_SQUARE, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_STAR, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY, ITEM_EMPTY}
    }), grid5.getItems());

    Grid grid5c = grid5.cloneGrid();
    assertArrayEquals(grid5c.getItems(), grid5.getItems());
    assertThat(grid5.getItems(), not(sameInstance(grid5c.getItems())));
    assertThat(grid5.getSize(), equalTo(grid5c.getSize()));
    assertThat(grid5.getCenterX(), equalTo(grid5c.getCenterX()));
    assertThat(grid5.getCenterY(), equalTo(grid5c.getCenterY()));
  }

  @Test
  public void test7() {
    Grid grid = new Grid(3, 5, 7);

    grid.addTriplet(4, 7);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());

    grid.addTriplet(5, 8, true);
    assertArrayEquals(ra(new byte[][]{
      {ITEM_SQUARE, ITEM_SQUARE, ITEM_SQUARE},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY},
      {ITEM_SQUARE, ITEM_EMPTY, ITEM_EMPTY}
    }), grid.getItems());

    grid.addTriplets(new int[]{5, 7, 6, 7});
    assertArrayEquals(ra(new byte[][]{
      {ITEM_SQUARE, ITEM_SQUARE, ITEM_SQUARE},
      {ITEM_SQUARE, ITEM_SQUARE, ITEM_SQUARE},
      {ITEM_SQUARE, ITEM_SQUARE, ITEM_SQUARE}
    }), grid.getItems());
  }
}
