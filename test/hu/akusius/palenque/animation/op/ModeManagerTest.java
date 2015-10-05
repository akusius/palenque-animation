package hu.akusius.palenque.animation.op;

import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class ModeManagerTest {

  public ModeManagerTest() {
  }

  @Test
  public void test1() {
    ModeManager mm = new ModeManager();

    PropToggle playing = mm.getPlayingToggle();
    PropToggle moving = mm.getMovingToggle();
    PropToggle rotating = mm.getRotatingToggle();
    PropToggle zooming = mm.getZoomingToggle();
    PropToggle combined = mm.getCombinedToggle();

    assertTrue(mm.isEnabled());
    assertTrue(playing.isEnabled());
    assertTrue(moving.isEnabled());
    assertTrue(rotating.isEnabled());
    assertTrue(zooming.isEnabled());
    assertTrue(combined.isEnabled());

    assertFalse(playing.isSelected());
    assertTrue(playing.isInGroup());
    assertFalse(moving.isSelected());
    assertTrue(moving.isInGroup());
    assertFalse(rotating.isSelected());
    assertTrue(rotating.isInGroup());
    assertFalse(zooming.isSelected());
    assertTrue(zooming.isInGroup());
    assertTrue(combined.isSelected());
    assertTrue(combined.isInGroup());

    assertThat(mm.getMode(), equalTo(OperationMode.Combined));

    EventTester et = new EventTester();
    mm.addPropertyChangeListener(et.propertyChangeListener);

    combined.setSelected(true);
    assertFalse(et.hadAnyEvent());

    moving.setSelected(true);
    assertThat(mm.getMode(), equalTo(OperationMode.Moving));
    assertFalse(playing.isSelected());
    assertTrue(moving.isSelected());
    assertTrue(et.hadPropertyChange(ModeManager.PROP_MODE, OperationMode.Combined, OperationMode.Moving));
    assertFalse(et.hadOtherPropertyChange(ModeManager.PROP_MODE));

    et.clear();
    combined.setSelected(true);
    assertThat(mm.getMode(), equalTo(OperationMode.Combined));
    assertFalse(playing.isSelected());
    assertFalse(moving.isSelected());
    assertTrue(combined.isSelected());
    assertTrue(et.hadPropertyChange(ModeManager.PROP_MODE, OperationMode.Moving, OperationMode.Combined));
    assertFalse(et.hadOtherPropertyChange(ModeManager.PROP_MODE));

    mm.setEnabled(false);
    assertFalse(mm.isEnabled());
    assertFalse(playing.isEnabled());
    assertFalse(moving.isEnabled());
    assertFalse(rotating.isEnabled());
    assertFalse(zooming.isEnabled());
    assertFalse(combined.isEnabled());

    assertThat(mm.getMode(), equalTo(OperationMode.Combined));

    mm.setEnabled(true);
    assertTrue(mm.isEnabled());
    assertTrue(playing.isEnabled());
    assertTrue(moving.isEnabled());
    assertTrue(rotating.isEnabled());
    assertTrue(zooming.isEnabled());
    assertTrue(combined.isEnabled());

    assertThat(mm.getMode(), equalTo(OperationMode.Combined));

    zooming.setSelected(true);
    assertThat(mm.getMode(), equalTo(OperationMode.Zooming));
    rotating.setSelected(true);
    assertThat(mm.getMode(), equalTo(OperationMode.Rotating));
    playing.setSelected(true);
    assertThat(mm.getMode(), equalTo(OperationMode.Playing));
  }

}
