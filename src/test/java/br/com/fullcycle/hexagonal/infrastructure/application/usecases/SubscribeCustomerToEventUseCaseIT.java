package br.com.fullcycle.hexagonal.infrastructure.application.usecases;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.usecases.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.hexagonal.infrastructure.models.Customer;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Ticket;
import br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus;
import br.com.fullcycle.hexagonal.infrastructure.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.TicketRepository;
import io.hypersistence.tsid.TSID;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscribeCustomerToEventUseCaseIT extends IntegrationTest {

    @Autowired
    private SubscribeCustomerToEventUseCase useCase;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    @Transactional
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        //given
        var customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        var event = createEvent("Disney", 10);
        var eventId = event.getId();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customer.getId());

        //when
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
        var customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        long eventId = TSID.fast().toLong();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customer.getId());

        //when
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
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        //given
        final var expectedError = "Email already registered";
        var customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        var event = createEvent("Disney", 10);
        createTicket(customer, event);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(event.getId(), customer.getId());

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Um mesmo cliente não pode comprar de um evento que não há mais cadeiras")
    public void testReserveTicketWithoutSlots() throws Exception {
        //given
        final var expectedError = "Event sold out";
        var customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        var event = createEvent("Disney", 0);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(event.getId(), customer.getId());

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        final var aCustomer = new Customer();
        aCustomer.setCpf(cpf);
        aCustomer.setEmail(email);
        aCustomer.setName(name);

        return customerRepository.save(aCustomer);
    }

    private Event createEvent(final String name, final int totalSpots) {
        final var anEvent = new Event();
        anEvent.setName(name);
        anEvent.setTotalSpots(totalSpots);

        return eventRepository.save(anEvent);
    }

    private Ticket createTicket(final Customer customer, final Event event) {
        var ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setEvent(event);
        ticket.setReservedAt(Instant.now());
        ticket.setPaidAt(Instant.now());
        ticket.setStatus(TicketStatus.PAID);

        return ticketRepository.save(ticket);
    }
}