package hu.akusius.palenque.animation.op;

/**
 * A képkockavezérlőt kezelő tulajdonság.
 * @author Bujdosó Ákos
 */
public class PropSliderFrame extends PropSlider {

  public PropSliderFrame() {
    super(0, FrameInfo.getMaxFrameNum(), 0);
  }

  /**
   * @return A minimális értékhez (első képkockához) tartozó {@link FrameInfo}.
   */
  public FrameInfo getMinFrameInfo() {
    return FrameInfo.getFrameInfo(this.getMin());
  }

  /**
   * @return A maximális értékhez (utolsó képkockához) tartozó {@link FrameInfo}.
   */
  public FrameInfo getMaxFrameInfo() {
    return FrameInfo.getFrameInfo(this.getMax());
  }

  /**
   * @return Az aktuális értékhez tartozó {@link FrameInfo}.
   */
  public FrameInfo getCurrentFrameInfo() {
    return FrameInfo.getFrameInfo(this.getValue());
  }

  /**
   * @return {@code true}, ha az első kockán állunk éppen.
   */
  public boolean isFirstFrame() {
    return this.getValue() == this.getMin();
  }

  /**
   * @return {@code true}, ha az utolsó kockán állunk éppen.
   */
  public boolean isLastFrame() {
    return this.getValue() == this.getMax();
  }

  /**
   * @return {@code true}, ha egy belső (nem első és nem utolsó) kockán állunk éppen.
   */
  public boolean isInternalFrame() {
    int value = this.getValue();
    return value > this.getMin() && value < this.getMax();
  }
}
