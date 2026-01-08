package br.com.fullcycle.application;

import br.com.fullcycle.IntegrationTest;
import br.com.fullcycle.application.event.SubscribeCustomerToEventUseCase;
import br.com.fullcycle.domain.customer.Customer;
import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.event.ticket.TicketStatus;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.domain.customer.CustomerRepository;
import br.com.fullcycle.domain.event.EventRepository;
import br.com.fullcycle.domain.partner.PartnerRepository;
import br.com.fullcycle.domain.event.ticket.TicketRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

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

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    @Transactional
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() throws Exception {
        //given
        var customer = createCustomer("123.456.789-01", "john.doe@gmail.com", "John Doe");
        var partner = createPartner("41.123.123/0001-00","Disney","disney@gmail.com");
        var event = createEvent("Disney", 10, partner);
        var eventId = event.eventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customer.customerId().value());

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
        var customer = createCustomer("123.456.789-01", "john.doe@gmail.com", "John Doe");
        var eventId = UUID.randomUUID().toString();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(eventId, customer.customerId().value());

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
        var customerId = UUID.randomUUID().toString();
        var eventId = UUID.randomUUID().toString();

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
        var customer = createCustomer("123.456.789-01", "john.doe@gmail.com", "John Doe");
        var partner = createPartner("41.123.123/0001-00","Disney","disney@gmail.com");
        var event = createEvent("Disney", 10, partner);
        createTicket(customer, event);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(event.eventId().value(), customer.customerId().value());
        useCase.execute(subscribeInput);

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Um cliente não pode comprar de um evento que não há mais cadeiras")
    public void testReserveTicketWithoutSlots() throws Exception {
        //given
        final var expectedError = "Event sold out";
        var customer = createCustomer("123.456.789-01", "john.doe@gmail.com", "John Doe");
        var partner = createPartner("41.123.123/0001-00","Disney","disney@gmail.com");
        var event = createEvent("Disney", 1, partner);

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(event.eventId().value(), customer.customerId().value());

        useCase.execute(subscribeInput);

        var customer2 = createCustomer("123.456.789-02", "john.doe2@gmail.com", "John Doe2");

        final var subscribeInput2 = new SubscribeCustomerToEventUseCase.Input(event.eventId().value(), customer2.customerId().value());

        //when
        final var actualException = assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput2));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    private Customer createCustomer(final String cpf, final String email, final String name) {
        return customerRepository.create(Customer.newCustomer(name, cpf, email));
    }

    private Event createEvent(final String name, final int totalSpots, final Partner partner) {
        return eventRepository.create(Event.newEvent(name, LocalDate.now().toString(), totalSpots, partner));
    }

    private Ticket createTicket(final Customer customer, final Event event) {
        return ticketRepository.create(Ticket.newTicket(customer.customerId(), event.eventId()));
    }

    private Partner createPartner(final String cnpj, final String name, final String email) {
        return partnerRepository.create(Partner.newPartner(name, cnpj, email));
    }
}