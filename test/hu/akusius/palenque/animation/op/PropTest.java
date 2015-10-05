package hu.akusius.palenque.animation.op;

import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class PropTest {

  private static final class PropImpl extends Prop {
  }

  public PropTest() {
  }

  @Test
  public void test1() {
    Prop p = new PropImpl();
    assertThat(p.isEnabled(), equalTo(true));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setEnabled(false);
    assertThat(p.isEnabled(), is(false));
    assertTrue(et.hadPropertyChange(Prop.PROP_ENABLED, true, false));

    p.removePropertyChangeListener(et.propertyChangeListener);
    et.clear();

    p.setEnabled(true);
    assertThat(p.isEnabled(), is(true));
    assertFalse(et.hadAnyEvent());
  }

  @Test
  public void test2() {
    Prop p = new PropImpl();
    assertThat(p.isEnabled(), equalTo(true));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setEnabled(false);
    assertThat(p.isEnabled(), is(false));
    assertTrue(et.hadPropertyChange(Prop.PROP_ENABLED, true, false));

    et.clear();
    p.setEnabledInternal(true);
    assertThat(p.isEnabled(), is(true));
    assertTrue(et.hadPropertyChange(Prop.PROP_ENABLED, false, true));

    et.clear();
    p.setEnabledInternal(false);
    assertThat(p.isEnabled(), is(false));
    assertTrue(et.hadPropertyChange(Prop.PROP_ENABLED, true, false));
  }

  @Test
  public void test3() {
    Prop p = new PropImpl();
    assertThat(p.isEnabled(), equalTo(true));

    EventTester et = new EventTester();
    p.addPropertyChangeListener(et.propertyChangeListener);

    p.setEnabled(true);
    assertFalse(et.hadAnyEvent());

    assertThat(p.isEnabled(), equalTo(true));
    assertFalse(et.hadAnyPropertyChange());
    et.clear();

    p.setEnabledInternal(false);
    assertThat(p.isEnabled(), equalTo(false));
    assertTrue(et.hadPropertyChange(Prop.PROP_ENABLED, true, false));
  }
}
