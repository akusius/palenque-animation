package hu.akusius.palenque.animation.op;

/**
 * Egy csúszka típusú tulajdonság.
 * @author Bujdosó Ákos
 */
public class PropSlider extends Prop {

  final private int min;

  final private int max;

  /**
   * Létrehozás.
   * @param min Minimális érték.
   * @param max Maximális érték.
   * @param value A konkrét érték.
   * @throws IllegalArgumentException Érvénytelen értékek lettek megadva.
   */
  public PropSlider(int min, int max, int value) throws IllegalArgumentException {
    if (max < min || value < min || value > max) {
      throw new IllegalArgumentException();
    }
    this.min = min;
    this.max = max;
    this.value = value;
  }

  /**
   * @return A minimális érték.
   */
  public int getMin() {
    return min;
  }

  /**
   * @return A maximális érték.
   */
  public int getMax() {
    return max;
  }

  private int value;

  public static final String PROP_VALUE = "value";

  /**
   * @return A konkrét érték.
   */
  public int getValue() {
    return value;
  }

  /**
   * Új érték beállítása.
   * @param value Az új érték.
   * @throws IllegalArgumentException Az érték nem esik a határok közé.
   * @throws IllegalStateException Nincs engedélyezve a művelet.
   */
  public void setValue(int value) throws IllegalArgumentException, IllegalStateException {
    if (!this.isEnabled()) {
      throw new IllegalStateException();
    }
    this.setValueInternal(value);
  }

  /**
   * Új érték beállítása.
   * @param value Az új érték.
   * @throws IllegalArgumentException Az érték nem esik a határok közé.
   */
  void setValueInternal(int value) throws IllegalArgumentException {
    if (value < min || value > max) {
      throw new IllegalArgumentException();
    }
    int oldValue = this.value;
    this.value = value;
    propertyChangeSupport.firePropertyChange(PROP_VALUE, oldValue, value);
  }

}
