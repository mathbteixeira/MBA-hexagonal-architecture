package br.com.fullcycle.hexagonal.infrastructure.application.usecases;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateEventUseCaseIT extends IntegrationTest {

    @Autowired
    private CreateEventUseCase useCase;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void tearDown() {
        eventRepository.deleteAll();
        partnerRepository.deleteAll();
    }

    @Test
    void testCreate() {
        //given
        final var partner = createPartner("41536538000100", "john.doe@gmail.com", "John Doe");
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = partner.getId();

        final var createInput =
                new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        //when
        final var output = useCase.execute(createInput);

        //then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedDate, output.date());
        Assertions.assertEquals(expectedName, output.name());
        Assertions.assertEquals(expectedPartnerId, output.partnerId());
        Assertions.assertEquals(expectedTotalSpots, output.totalSpots());
    }

    @Test
    @DisplayName("Não deve criar um evento quando o Partner não for encontrado")
    void testCreateEvent_whenPartnerDoesntExist_shouldThrowError() throws Exception {
        //given
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = TSID.fast().toLong();
        final var expectedError = "Partner not found";

        final var createInput =
                new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        final Partner aPartner = new Partner();
        aPartner.setCnpj(cnpj);
        aPartner.setEmail(email);
        aPartner.setName(name);
        return partnerRepository.save(aPartner);
    }
}