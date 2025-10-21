package br.com.fullcycle.hexagonal.infrastructure.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.CreateEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CreateEventUseCaseTest {

    @Test
    void testCreate() {
        //given
        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney";
        final var expectedTotalSpots = 10;
        final var expectedPartnerId = TSID.fast().toLong();

        final var createInput =
                new CreateEventUseCase.Input(expectedDate, expectedName, expectedPartnerId, expectedTotalSpots);

        //when
        final var eventService = Mockito.mock(EventService.class);
        final var partnerService = Mockito.mock(PartnerService.class);

        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            e.setId(TSID.fast().toLong());
            return e;
        });

        when(partnerService.findById(eq(expectedPartnerId))).thenReturn(Optional.of(new Partner()));

        final var useCase = new CreateEventUseCase(partnerService, eventService);
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
        final var eventService = Mockito.mock(EventService.class);
        final var partnerService = Mockito.mock(PartnerService.class);

        when(partnerService.findById(eq(expectedPartnerId))).thenReturn(Optional.empty());

        final var useCase = new CreateEventUseCase(partnerService, eventService);
        final var output = assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, output.getMessage());
    }
}