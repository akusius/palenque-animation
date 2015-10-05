package hu.akusius.palenque.animation.op;

import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class PropSliderTest {

  public PropSliderTest() {
  }

  @Test
  public void test1() {
    try {
      PropSlider p = new PropSlider(2, 1, 1);
      fail();
    } catch (IllegalArgumentException ex) {
    }
    try {
      PropSlider p = new PropSlider(1, 5, 10);
      fail();
    } catch (IllegalArgumentException ex) {
    }
    try {
      PropSlider p = new PropSlider(1, 10, 5);
      p.setValue(11);
      fail();
    } catch (IllegalArgumentException ex) {
    }
    try {
      PropSlider p = new PropSlider(1, 10, 5);
      p.setValueInternal(11);
      fail();
    } catch (IllegalArgumentException ex) {
    }
    try {
      PropSlider p = new PropSlider(1, 10, 5);
      p.setValueInternal(11);
      fail();
    } catch (IllegalArgumentException ex) {
    }
  }

  @Test
  public void test2() {
    PropSlider p = new PropSlider(1, 10, 5);
    assertThat(p.getMin(), equalTo(1));
    assertThat(p.getMax(), equalTo(10));
    assertThat(p.getValue(), equalTo(5));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setValue(3);
    assertThat(p.getValue(), equalTo(3));
    assertTrue(et.hadPropertyChange(PropSlider.PROP_VALUE, 5, 3));

    p.removePropertyChangeListener(et.propertyChangeListener);
    et.clear();

    p.setValue(8);
    assertThat(p.getValue(), equalTo(8));
    assertFalse(et.hadAnyEvent());
  }

  @Test
  public void test3() {
    PropSlider p = new PropSlider(1, 5, 2);
    assertThat(p.getMin(), equalTo(1));
    assertThat(p.getMax(), equalTo(5));
    assertThat(p.getValue(), equalTo(2));
    p.setValueInternal(4);
    assertThat(p.getValue(), equalTo(4));
  }

  @Test
  public void test4() {
    PropSlider p = new PropSlider(1, 5, 2);
    assertThat(p.getMin(), equalTo(1));
    assertThat(p.getMax(), equalTo(5));
    assertThat(p.getValue(), equalTo(2));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setValue(3);
    assertThat(p.getValue(), equalTo(3));
    assertTrue(et.hadPropertyChange(PropSlider.PROP_VALUE, 2, 3));

    et.clear();
    p.setValueInternal(4);
    assertThat(p.getValue(), equalTo(4));
    assertTrue(et.hadPropertyChange(PropSlider.PROP_VALUE, 3, 4));

    et.clear();
    p.setValueInternal(5);
    assertThat(p.getValue(), equalTo(5));
    assertTrue(et.hadPropertyChange(PropSlider.PROP_VALUE, 4, 5));
  }

  @Test
  public void test5() {
    PropSlider p = new PropSlider(1, 5, 2);
    assertThat(p.getMin(), equalTo(1));
    assertThat(p.getMax(), equalTo(5));
    assertThat(p.getValue(), equalTo(2));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setValue(2);
    assertFalse(et.hadAnyEvent());

    p.setValueInternal(3);
    assertThat(p.getValue(), equalTo(3));
    assertTrue(et.hadPropertyChange(PropSlider.PROP_VALUE, 2, 3));
  }

  @Test
  public void test6() {
    PropSlider p = new PropSlider(1, 10, 5);

    assertTrue(p.isEnabled());
    p.setEnabled(false);
    assertFalse(p.isEnabled());

    try {
      p.setValue(3);
      fail();
    } catch (IllegalStateException ex) {
    }

    assertThat(p.getValue(), equalTo(5));
    p.setValueInternal(3);
    assertThat(p.getValue(), equalTo(3));

    p.setEnabled(true);
    assertTrue(p.isEnabled());
    p.setValue(1);
    assertThat(p.getValue(), equalTo(1));
  }
}
