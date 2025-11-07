package br.com.fullcycle.hexagonal.application.usecases.event;

import br.com.fullcycle.hexagonal.IntegrationTest;
import br.com.fullcycle.hexagonal.application.domain.event.ticket.TicketStatus;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.CustomerEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.EventEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.entities.TicketEntity;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.CustomerJpaRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.EventJpaRepository;
import br.com.fullcycle.hexagonal.infrastructure.jpa.repositories.TicketJpaRepository;
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
    private CustomerJpaRepository customerJpaRepository;

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Autowired
    private TicketJpaRepository ticketJpaRepository;

    @Test
    @Transactional
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        //given
        var customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        var event = createEvent("Disney", 10);
        var eventId = event.getId().toString();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customer.getId().toString());

        //when
        final var output = useCase.execute(subscribeInput);

        //then
        Assertions.assertEquals(eventId, output.eventId());
        Assertions.assertNotNull(output.reservationDate());
        Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
    }

    @Test
    @Transactional
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() throws Exception {
        //given
        final var expectedError = "Event not found";
        var customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        var eventId = TSID.fast().toString();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customer.getId().toString());

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Não deve comprar um ticket com um cliente não existente")
    public void testReserveTicketWithoutCustomer() throws Exception {
        //given
        final var expectedError = "Customer not found";
        var customerId = TSID.fast().toString();
        var eventId = TSID.fast().toString();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customerId);

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() throws Exception {
        //given
        final var expectedError = "Email already registered";
        var customer = createCustomer("12345678901", "john.doe@gmail.com", "John Doe");
        var event = createEvent("Disney", 10);
        createTicket(customer, event);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(event.getId().toString(), customer.getId().toString());

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

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(event.getId().toString(), customer.getId().toString());

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    private CustomerEntity createCustomer(final String cpf, final String email, final String name) {
        final var aCustomer = new CustomerEntity();
        aCustomer.setCpf(cpf);
        aCustomer.setEmail(email);
        aCustomer.setName(name);

        return customerJpaRepository.save(aCustomer);
    }

    private EventEntity createEvent(final String name, final int totalSpots) {
        final var anEvent = new EventEntity();
        anEvent.setName(name);
        anEvent.setTotalSpots(totalSpots);

        return eventJpaRepository.save(anEvent);
    }

    private TicketEntity createTicket(final CustomerEntity customer, final EventEntity event) {
        var ticket = new TicketEntity();
        ticket.setCustomerId(customer.getId());
        ticket.setEventId(event.getId());
        ticket.setReservedAt(Instant.now());
        ticket.setPaidAt(Instant.now());
        ticket.setStatus(TicketStatus.PAID);

        return ticketJpaRepository.save(ticket);
    }
}