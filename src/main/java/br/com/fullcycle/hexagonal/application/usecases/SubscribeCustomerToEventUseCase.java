package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.application.domain.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.EventId;
import br.com.fullcycle.hexagonal.application.domain.Ticket;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repositories.CustomerRepository;
import br.com.fullcycle.hexagonal.application.repositories.EventRepository;
import br.com.fullcycle.hexagonal.application.repositories.TicketRepository;

import java.time.Instant;
import java.util.Objects;

public class SubscribeCustomerToEventUseCase extends UseCase<SubscribeCustomerToEventUseCase.Input, SubscribeCustomerToEventUseCase.Output> {

    private final CustomerRepository customerRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public SubscribeCustomerToEventUseCase(CustomerRepository customerRepository,
                                           EventRepository eventRepository,
                                           TicketRepository ticketRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
    }

    @Override
    public Output execute(final Input input) {
        var customer = customerRepository.customerOfId(CustomerId.with(input.customerId))
                .orElseThrow(() -> new ValidationException("Customer not found"));

        var event = eventRepository.eventOfId(EventId.with(input.eventId))
                .orElseThrow(() -> new ValidationException("Event not found"));

        final Ticket ticket = event.reserveTicket(customer.customerId());

        ticketRepository.create(ticket);
        eventRepository.update(event);

        return new Output(event.eventId().value(), ticket.ticketId().value(), ticket.ticketStatus().name(), ticket.reservedAt());
    }

    public record Input(String eventId, String customerId) {}

    public record Output(String eventId, String ticketId, String ticketStatus, Instant reservationDate) {}
}
