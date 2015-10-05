package hu.akusius.palenque.animation.op;

/**
 * A lehetséges működési módok
 * @author Bujdosó Ákos
 */
public enum OperationMode {

  /**
   * Lejátszás
   */
  Playing,
  /**
   * Mozgatás
   */
  Moving,
  /**
   * Forgatás
   */
  Rotating,
  /**
   * Nagyítás
   */
  Zooming,
  /**
   * Kombinált (mozgatás, forgatás, nagyítás)
   */
  Combined,
}
