package hu.akusius.palenque.animation.util;

import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Támogatás az {@link EventListener} eseménykezelőkhöz.
 * Az osztály garantálja, hogy az eseménykezelők a regisztrálás sorrendjében hívódnak meg.
 * @param <T> Az eseményobjektum típusa.
 * @author Bujdosó Ákos
 */
public class EventSupport<T extends EventObject> {

  private final List<EventListener<T>> listeners = new CopyOnWriteArrayList<>();

  private final Object source;

  /**
   * Létrehozás.
   * @param source Az események forrása.
   */
  public EventSupport(Object source) {
    this.source = source;
  }

  /**
   * Új eseménykezelő hozzáadása.
   * @param listener A hozzáadandó eseménykezelő.
   */
  public void addEventListener(EventListener<T> listener) {
    if (listener == null) {
      return;
    }
    listeners.add(listener);
  }

  /**
   * A megadott eseménykezelő eltávolítása.
   * @param listener Az eltávolítandó eseménykezelő.
   */
  public void removeEventListener(EventListener<T> listener) {
    if (listener == null) {
      return;
    }
    listeners.remove(listener);
  }

  /**
   * Az esemény jelzése.
   * @param e Az eseményobjektum.
   */
  @SuppressWarnings("unchecked")
  public void fireEvent(T e) {
    assert e != null;

    for (EventListener<T> listener : this.listeners) {
      listener.notify(e);
    }
  }

  @SuppressWarnings("unchecked")
  private void fireEvent(LazyEventObject<T> le) {
    for (EventListener<T> listener : this.listeners) {
      listener.notify(le.getEventObject());
    }
  }

  /**
   * Esemény jelzése külön paraméterek nélkül. Csak akkor használható, ha T {@link EventObject} típusú.
   */
  public void fireEvent() {
    fireEvent(new LazyEventObject<>(new EventObjectCreator<T>() {

      @Override
      @SuppressWarnings("unchecked")
      public T createEventObject() {
        return (T) new EventObject(source);
      }
    }));
  }

  /**
   * Esemény jelzése 1 paraméterrel. Csak akkor használható, ha T {@link Event1} típusú.
   * @param param A paraméter.
   */
  @SuppressWarnings("unchecked")
  public void fireEvent1(final Object param) {
    fireEvent(new LazyEventObject<>(new EventObjectCreator<T>() {

      @Override
      public T createEventObject() {
        return (T) new Event1<>(source, param);
      }
    }));
  }

  /**
   * Esemény jelzése 2 paraméterrel. Csak akkor használható, ha T {@link Event2} típusú.
   * @param param1 Az első paraméter.
   * @param param2 A második paraméter.
   */
  @SuppressWarnings("unchecked")
  public void fireEvent2(final Object param1, final Object param2) {
    fireEvent(new LazyEventObject<>(new EventObjectCreator<T>() {

      @Override
      public T createEventObject() {
        return (T) new Event2<>(source, param1, param2);
      }
    }));
  }

  /**
   * @return Vannak-e eseménykezelők rákötve.
   */
  public boolean hasListeners() {
    return listeners.size() > 0;
  }

  private interface EventObjectCreator<T> {

    T createEventObject();
  }

  private static class LazyEventObject<T> {

    private final EventObjectCreator<T> creator;

    LazyEventObject(EventObjectCreator<T> creator) {
      this.creator = creator;
    }

    private T eventObject = null;

    T getEventObject() {
      if (eventObject == null) {
        eventObject = creator.createEventObject();
      }

      return eventObject;
    }
  }
}
