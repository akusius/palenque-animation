package hu.akusius.palenque.animation.op;

import hu.akusius.palenque.animation.util.Event1;
import hu.akusius.palenque.animation.util.EventListener;
import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class PropActionTest {

  public PropActionTest() {
  }

  @Test
  public void test1() {
    PropAction prop = new PropAction();
    assertThat(prop.isEnabled(), is(true));

    EventTester et = new EventTester();
    EventListener<Event1<Object>> l = et.eventListener("actionPerformed");
    prop.addActionPerformedListener(l);

    prop.performAction();
    assertTrue(et.hadEvent("actionPerformed", EventListener.class));
    et.clear();

    prop.performAction("TEST");
    assertTrue(et.hadEvent("actionPerformed", EventListener.class));
    assertTrue(et.hadEvent(new EventTester.EventMatcher() {

      @Override
      @SuppressWarnings("unchecked")
      public boolean matches(EventTester.TestEvent event) {
        return "actionPerformed".equals(event.getName())
                && "TEST".equals(((Event1<Object>) event.getEventObject()).getParam());
      }
    }));
    et.clear();

    prop.removeActionPerformedListener(l);
    prop.performAction("TEST");
    assertFalse(et.hadAnyEvent());
  }

  @Test(expected = IllegalStateException.class)
  public void test2() {
    PropAction prop = new PropAction();
    assertTrue(prop.isEnabled());

    prop.setEnabled(false);
    prop.performAction();
  }

  private static final class Prop extends PropAction {

    public boolean hasRun = false;

    @Override
    protected void action(Object param) {
      hasRun = true;
    }
  }

  @Test
  public void test3() {
    Prop prop = new Prop();
    assertFalse(prop.hasRun);
    prop.performAction();
    assertTrue(prop.hasRun);
  }

}
