package hu.akusius.palenque.animation.util;

import java.io.*;

/**
 * IO-segédrutinok
 * @author Bujdosó Ákos
 */
public class IoUtils {

  /**
   * Egy {@link String} beolvasása egy {@link InputStream}-ből, UTF-8 kódolással.
   * @param inputStream A beolvasás forrása. Nem lehet {@code null}.
   * @return A beolvasott sztring.
   * @throws IOException Hiba a beolvasás közben.
   */
  public static String readStringFromInputStream(InputStream inputStream) throws IOException {
    return readStringFromInputStream(inputStream, "UTF-8");
  }

  /**
   * Egy {@link String} beolvasása egy {@link InputStream}-ből, a megadott kódolással.
   * @param inputStream A beolvasás forrása. Nem lehet {@code null}.
   * @param encoding A kódolás megnevezése. Nem lehet {@code null}.
   * @return A beolvasott sztring.
   * @throws UnsupportedEncodingException Érvénytelen kódolás lett megadva.
   * @throws IOException Hiba a beolvasás közben.
   */
  public static String readStringFromInputStream(InputStream inputStream, String encoding) throws UnsupportedEncodingException, IOException {
    if (inputStream != null) {
      Writer writer = new StringWriter();

      char[] buffer = new char[0x10000];
      try {
        Reader reader = new BufferedReader(
                new InputStreamReader(inputStream, encoding));
        int n;
        while ((n = reader.read(buffer)) != -1) {
          writer.write(buffer, 0, n);
        }
      } finally {
        inputStream.close();
      }
      return writer.toString();
    } else {
      return "";
    }
  }

  private IoUtils() {
  }
}
