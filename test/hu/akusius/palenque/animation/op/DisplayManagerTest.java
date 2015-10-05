package hu.akusius.palenque.animation.op;

import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class DisplayManagerTest {

  public DisplayManagerTest() {
  }

  @Test
  public void test1() {
    PlayManager pm = new PlayManager();
    DisplayManager dm = new DisplayManager(pm);

    PropToggle autoToggle = dm.getAutoToggle();
    assertTrue(autoToggle.isEnabled());
    assertTrue(autoToggle.isSelected());
    assertFalse(autoToggle.isInGroup());

    EventTester et = new EventTester();
    dm.addChangeListener(et.changeListener);

    pm.getFrameSlider().setValue(FrameInfo.getMaxFrameNum() - 10);
    assertTrue(et.hadChange());

    et.clear();
    assertFalse(et.hadChange());

    dm.adjustZoom(2d);
    assertTrue(et.hadChange());
    assertFalse(autoToggle.isSelected());
    et.clear();

    pm.getFrameSlider().setValue(0);
    assertFalse(et.hadChange());

    double zoom = dm.getZoom();
    autoToggle.setSelected(true);
    assertTrue(autoToggle.isSelected());
    assertTrue(et.hadChange());
    assertThat(dm.getZoom(), not(equalTo(zoom)));
    et.clear();

    dm.rotate(1d, .5d);
    assertTrue(et.hadChange());
    assertFalse(autoToggle.isSelected());
    et.clear();

    dm.translate(10d, 5d);
    assertTrue(et.hadChange());
    et.clear();
  }

}
