package util;

import hu.akusius.palenque.animation.util.EventListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Bujdosó Ákos
 */
public class EventTester {

  private final List<TestEvent> eventList = new ArrayList<>(10);

  /**
   * Új esemény hozzáadása.
   * @param name
   * @param listenerType
   * @param eventObject
   */
  public void add(String name, Class<?> listenerType, EventObject eventObject) {
    TestEvent testEvent = new TestEvent();
    testEvent.name = name;
    testEvent.listenerType = listenerType;
    testEvent.eventObject = eventObject;
    testEvent.index = eventList.size();

    eventList.add(testEvent);
  }

  /**
   *
   */
  public void clear() {
    eventList.clear();
  }

  /**
   *
   * @param matcher
   * @return
   */
  public TestEvent[] getEvents(EventMatcher matcher) {
    List<TestEvent> result = new ArrayList<>(10);
    for (TestEvent testEvent : this.eventList) {
      if (matcher.matches(testEvent)) {
        result.add(testEvent);
      }
    }

    return result.toArray(new TestEvent[0]);
  }

  /**
   *
   * @param name
   * @return
   */
  public TestEvent[] getEventsByName(final String name) {
    return getEvents(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return name.equalsIgnoreCase(event.name);
      }
    });
  }

  /**
   *
   * @param listenerType
   * @return
   */
  public TestEvent[] getEventsByType(final Class<?> listenerType) {
    return getEvents(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return listenerType.equals(event.listenerType);
      }
    });
  }

  /**
   *
   * @param name
   * @param listenerType
   * @return
   */
  public TestEvent[] getEventsByNameAndType(final String name, final Class<?> listenerType) {
    return getEvents(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return name.equalsIgnoreCase(event.name)
                && listenerType.equals(event.listenerType);
      }
    });

  }

  /**
   *
   * @param matcher
   * @return
   */
  public boolean hadEvent(EventMatcher matcher) {
    for (TestEvent testEvent : this.eventList) {
      if (matcher.matches(testEvent)) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   * @param name
   * @param listenerType
   * @return
   */
  public boolean hadEvent(final String name, final Class<?> listenerType) {
    return hadEvent(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return name.equals(event.name)
                && listenerType.equals(event.listenerType);
      }
    });
  }

  /**
   *
   * @return
   */
  public boolean hadAnyEvent() {
    return hadEvent(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return true;
      }
    });
  }

  /**
   *
   * @return
   */
  public boolean hadChange() {
    return hadEvent("stateChanged", ChangeListener.class);
  }

  /**
   *
   * @return
   */
  public boolean hadAnyPropertyChange() {
    return hadEvent(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return "propertyChange".equals(event.name)
                && event.listenerType == PropertyChangeListener.class;
      }
    });
  }

  /**
   *
   * @param propName
   * @return
   */
  public boolean hadPropertyChange(final String propName) {
    return hadEvent(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return "propertyChange".equals(event.name)
                && event.listenerType == PropertyChangeListener.class
                && propName.equals(((PropertyChangeEvent) event.eventObject).getPropertyName());
      }
    });
  }

  /**
   *
   * @param propName
   * @param oldValue
   * @param newValue
   * @return
   */
  public boolean hadPropertyChange(final String propName, final Object oldValue, final Object newValue) {
    return hadEvent(new EventMatcher() {

      private PropertyChangeEvent e;

      @Override
      public boolean matches(TestEvent event) {
        if (!"propertyChange".equals(event.name)
                || event.listenerType != PropertyChangeListener.class) {
          return false;
        }

        e = (PropertyChangeEvent) event.eventObject;

        return propName.equals(e.getPropertyName())
                && nullSafeEquals(oldValue, e.getOldValue())
                && nullSafeEquals(newValue, e.getNewValue());
      }
    });
  }

  /**
   *
   * @param propName
   * @return
   */
  public boolean hadVetoableChange(final String propName) {
    return hadEvent(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return "vetoableChange".equals(event.name)
                && event.listenerType == VetoableChangeListener.class
                && propName.equals(((PropertyChangeEvent) event.eventObject).getPropertyName());
      }
    });
  }

  /**
   *
   * @param propName
   * @return
   */
  public boolean hadOtherPropertyChange(final String propName) {
    return hadEvent(new EventMatcher() {

      @Override
      public boolean matches(TestEvent event) {
        return "propertyChange".equals(event.name)
                && event.listenerType == PropertyChangeListener.class
                && !propName.equals(((PropertyChangeEvent) event.eventObject).getPropertyName());
      }
    });
  }

  /**
   *
   */
  public final ChangeListener changeListener
          = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
              EventTester.this.add("stateChanged", ChangeListener.class, e);
            }
          };

  /**
   *
   */
  public final PropertyChangeListener propertyChangeListener
          = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              EventTester.this.add("propertyChange", PropertyChangeListener.class, evt);
            }
          };

  /**
   *
   */
  public final VetoableChangeListener vetoableChangeListener
          = new VetoableChangeListener() {

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
              EventTester.this.add("vetoableChange", VetoableChangeListener.class, evt);
            }
          };

  /**
   *
   */
  public final VetoableChangeListener vetoableChangeListenerVeto
          = new VetoableChangeListener() {

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
              EventTester.this.add("vetoableChange", VetoableChangeListener.class, evt);
              throw new PropertyVetoException("Veto", evt);
            }
          };

  private final EventListener<EventObject> eventListener = new EventListener<EventObject>() {

    @Override
    public void notify(EventObject e) {
      EventTester.this.add("notify", EventListener.class, e);
    }
  };

  /**
   *
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked")
  public <T extends EventObject> EventListener<T> eventListener() {
    return (EventListener<T>) this.eventListener;
  }

  /**
   *
   * @param <T>
   * @param name
   * @return
   */
  public <T extends EventObject> EventListener<T> eventListener(final String name) {
    return new EventListener<T>() {

      @Override
      public void notify(T e) {
        EventTester.this.add(name, EventListener.class, e);
      }
    };
  }

  /**
   *
   * @param o1
   * @param o2
   * @return
   */
  protected boolean nullSafeEquals(Object o1, Object o2) {
    return o1 == null && o2 == null
            || o1 != null && o1.equals(o2);
  }

  /**
   *
   */
  @SuppressWarnings("PublicInnerClass")
  public interface EventMatcher {

    /**
     *
     * @param event
     * @return
     */
    boolean matches(TestEvent event);
  }

  /**
   *
   */
  @SuppressWarnings("PublicInnerClass")
  public class TestEvent {

    private String name;

    private Class<?> listenerType;

    private EventObject eventObject;

    private int index;

    /**
     *
     * @return
     */
    public EventObject getEventObject() {
      return eventObject;
    }

    /**
     *
     * @return
     */
    public Class<?> getListenerType() {
      return listenerType;
    }

    /**
     *
     * @return
     */
    public String getName() {
      return name;
    }

    /**
     *
     * @return
     */
    public int getIndex() {
      return index;
    }
  }
}
