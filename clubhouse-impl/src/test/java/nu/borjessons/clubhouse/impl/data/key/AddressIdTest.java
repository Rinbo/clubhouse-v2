package nu.borjessons.clubhouse.impl.data.key;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddressIdTest {
  private static final String HELLO = "hello";

  @Test
  void equalsAndHashCodeTest() {
    AddressId addressId = new AddressId(HELLO);
    Assertions.assertEquals(new AddressId(HELLO), addressId);
    Assertions.assertEquals(new AddressId(HELLO).hashCode(), addressId.hashCode());
    Assertions.assertNotEquals(new AddressId("hej"), addressId);
  }

  @Test
  void toStringTest() {
    Assertions.assertEquals(HELLO, new AddressId(HELLO).toString());
  }
}