package hu.akusius.palenque.animation.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Bujdosó Ákos
 */
public class IoUtilsTest {

  private static final Random random = new Random();

  /**
   *
   */
  public IoUtilsTest() {
  }

  private static String generateRandomString(int length) {
    char[] validChars = new char[]{
      'a', 'á', 'b', 'c', 'd', 'e', 'é', 'f', 'g',
      'h', 'i', 'í', 'j', 'k', 'l', 'm', 'n', 'o',
      'ó', 'ö', 'ő', 'p', 'q', 'r', 's', 't', 'u',
      'ú', 'ü', 'ű', 'v', 'w', 'x', 'y', 'z'
    };

    StringBuilder sb = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      char ch = validChars[random.nextInt(validChars.length)];
      sb.append(ch);
    }

    return sb.toString();
  }

  /**
   * readStringFromInputStream tesztelése
   * @throws IOException
   */
  @Test
  public void test1() throws IOException {
    // Előállítunk egy véletlen sztringet
    String s = generateRandomString(random.nextInt(1000000));

    byte[] utf8 = s.getBytes("UTF-8");
    assertThat(IoUtils.readStringFromInputStream(new ByteArrayInputStream(utf8)), equalTo(s));
    assertThat(IoUtils.readStringFromInputStream(new ByteArrayInputStream(utf8), "UTF-8"), equalTo(s));

    byte[] iso88592 = s.getBytes("ISO-8859-2");
    assertThat(IoUtils.readStringFromInputStream(new ByteArrayInputStream(iso88592), "ISO-8859-2"), equalTo(s));
  }
}
