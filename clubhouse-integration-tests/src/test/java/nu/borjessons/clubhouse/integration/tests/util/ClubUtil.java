package nu.borjessons.clubhouse.integration.tests.util;

import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;

public class ClubUtil {
  private static CreateClubModel getCreateClubModel(String ownerEmail) {
    CreateClubModel createClubModel = new CreateClubModel();

    CreateUserModel createUserModel = new CreateUserModel();
    createUserModel.setClubId("Dummy");
    createUserModel.setFirstName("Owner2");
    createUserModel.setLastName("Lastname");
    createUserModel.setEmail(ownerEmail);
    createUserModel.setDateOfBirth("1982-03-16");
    createUserModel.setPassword(EmbeddedDataLoader.DEFAULT_PASSWORD);

    createClubModel.setName("Cool Club");
    createClubModel.setType(Club.Type.MISC);
    createClubModel.setOwner(createUserModel);

    return createClubModel;
  }

  private ClubUtil() {
    throw new IllegalStateException();
  }
}
