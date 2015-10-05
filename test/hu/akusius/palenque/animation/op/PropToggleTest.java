package hu.akusius.palenque.animation.op;

import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class PropToggleTest {

  public PropToggleTest() {
  }

  @Test
  public void test1() {
    PropToggle p = new PropToggle(true);
    assertThat(p.isSelected(), is(true));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setSelected(false);
    assertThat(p.isSelected(), is(false));
    assertTrue(et.hadPropertyChange(PropToggle.PROP_SELECTED, true, false));

    p.removePropertyChangeListener(et.propertyChangeListener);
    et.clear();

    p.setSelected(true);
    assertThat(p.isSelected(), is(true));
    assertFalse(et.hadAnyEvent());
  }

  @Test
  public void test2() {
    PropToggle p = new PropToggle(true);
    assertThat(p.isSelected(), is(true));
    p.setSelectedInternal(false);
    assertThat(p.isSelected(), is(false));
  }

  @Test
  public void test3() {
    PropToggle p = new PropToggle(true);
    assertThat(p.isSelected(), equalTo(true));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setSelected(false);
    assertThat(p.isSelected(), is(false));
    assertTrue(et.hadPropertyChange(PropToggle.PROP_SELECTED, true, false));

    et.clear();
    p.setSelectedInternal(true);
    assertThat(p.isSelected(), is(true));
    assertTrue(et.hadPropertyChange(PropToggle.PROP_SELECTED, false, true));

    et.clear();
    p.setSelectedInternal(false);
    assertThat(p.isSelected(), is(false));
    assertTrue(et.hadPropertyChange(PropToggle.PROP_SELECTED, true, false));
  }

  @Test
  public void test4() {
    PropToggle p = new PropToggle(true);
    assertThat(p.isSelected(), equalTo(true));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setSelected(true);
    assertFalse(et.hadAnyEvent());

    assertThat(p.isSelected(), equalTo(true));
    assertFalse(et.hadAnyPropertyChange());
    et.clear();

    p.setSelectedInternal(false);
    assertThat(p.isSelected(), equalTo(false));
    assertTrue(et.hadPropertyChange(PropToggle.PROP_SELECTED, true, false));
  }

  @Test
  public void test5() {
    PropToggle p = new PropToggle(true);
    assertTrue(p.isEnabled());
    assertTrue(p.isSelected());

    p.setEnabled(false);
    assertFalse(p.isEnabled());
    assertTrue(p.isSelected());

    try {
      p.setSelected(false);
      fail();
    } catch (IllegalStateException ex) {
    }

    assertTrue(p.isSelected());
    p.setSelectedInternal(false);
    assertFalse(p.isSelected());

    p.setEnabled(true);
    assertTrue(p.isEnabled());
    assertFalse(p.isSelected());

    p.setSelected(true);
    assertTrue(p.isEnabled());
    assertTrue(p.isSelected());
  }

  @Test
  public void test6() {
    PropToggle t1 = new PropToggle(true);
    PropToggle t2 = new PropToggle(false);
    PropToggle t3 = new PropToggle(false);

    assertFalse(t1.isInGroup());
    assertFalse(t2.isInGroup());
    assertFalse(t3.isInGroup());

    PropToggle.configGroup(t1, t2, t3);
    assertTrue(t1.isInGroup());
    assertTrue(t2.isInGroup());
    assertTrue(t3.isInGroup());

    try {
      t1.setSelected(false);
      fail();
    } catch (IllegalStateException ex) {
    }

    assertTrue(t1.isSelected());
    assertFalse(t2.isSelected());
    assertFalse(t3.isSelected());

    assertTrue(t1.isEnabled());
    assertTrue(t2.isEnabled());
    assertTrue(t3.isEnabled());

    t2.setSelectedInternal(true);
    assertFalse(t1.isSelected());
    assertTrue(t2.isSelected());
    assertFalse(t3.isSelected());

    t3.setSelected(true);
    assertFalse(t1.isSelected());
    assertFalse(t2.isSelected());
    assertTrue(t3.isSelected());

    t1.setEnabled(false);
    t2.setEnabled(false);
    t1.setSelectedInternal(true);
    assertTrue(t1.isSelected());
    assertFalse(t2.isSelected());
    assertFalse(t3.isSelected());
  }

}
