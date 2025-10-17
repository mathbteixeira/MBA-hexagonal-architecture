package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.models.Event;
import br.com.fullcycle.hexagonal.models.Ticket;
import br.com.fullcycle.hexagonal.models.TicketStatus;
import br.com.fullcycle.hexagonal.services.CustomerService;
import br.com.fullcycle.hexagonal.services.EventService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SubscribeCustomerToEventUseCaseTest {

    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        //given
        final var expectedTicketsSize = 1;
        long customerId = TSID.fast().toLong();
        long eventId = TSID.fast().toLong();

        final var anEvent = new Event();
        anEvent.setId(eventId);
        anEvent.setName("Disney");
        anEvent.setTotalSpots(10);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(anEvent.getId(), customerId);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(eventId)).thenReturn(Optional.of(anEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventId, customerId)).thenReturn(Optional.empty());
        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            Assertions.assertTrue(e.getTickets().size() == expectedTicketsSize);
            return e;
        });

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var output = useCase.execute(subscribeInput);

        //then
        Assertions.assertEquals(eventId, output.eventId());
        Assertions.assertNotNull(output.reservationDate());
        Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() throws Exception {
        //given
        final var expectedError = "Event not found";
        long customerId = TSID.fast().toLong();
        long eventId = TSID.fast().toLong();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(eventId)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket com um cliente não existente")
    public void testReserveTicketWithoutCustomer() throws Exception {
        //given
        final var expectedError = "Customer not found";
        long customerId = TSID.fast().toLong();
        long eventId = TSID.fast().toLong();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        //given
        final var expectedError = "Email already registered";
        long customerId = TSID.fast().toLong();
        long eventId = TSID.fast().toLong();

        final var anEvent = new Event();
        anEvent.setId(eventId);
        anEvent.setName("Disney");
        anEvent.setTotalSpots(10);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(anEvent.getId(), customerId);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(eventId)).thenReturn(Optional.of(anEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventId, customerId)).thenReturn(Optional.of(new Ticket()));

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar de um evento que não há mais cadeiras")
    public void testReserveTicketWithoutSlots() throws Exception {
        //given
        final var expectedError = "Event sold out";
        long customerId = TSID.fast().toLong();
        long eventId = TSID.fast().toLong();

        final var anEvent = new Event();
        anEvent.setId(eventId);
        anEvent.setName("Disney");
        anEvent.setTotalSpots(0);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(anEvent.getId(), customerId);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        final var eventService = Mockito.mock(EventService.class);

        when(customerService.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(eventService.findById(eventId)).thenReturn(Optional.of(anEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventId, customerId)).thenReturn(Optional.empty());

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }
}