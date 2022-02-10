package nu.borjessons.clubhouse.impl.data.converter;

import javax.persistence.AttributeConverter;

import nu.borjessons.clubhouse.impl.data.key.AddressId;

public class AddressIdConverter implements AttributeConverter<AddressId, String> {
  @Override
  public String convertToDatabaseColumn(AddressId addressId) {
    return addressId.toString();
  }

  @Override
  public AddressId convertToEntityAttribute(String string) {
    return new AddressId(string);
  }
}


